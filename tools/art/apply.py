"""Copy the reviewed generated textures of a run into the mod's asset tree, applying the hand-picked fixes.

    python tools/art/apply.py --run batch1                                   # everything in that run
    python tools/art/apply.py --run leaves2 --only pecan_leaves macadamia_leaves
"""
import argparse
import shutil
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
TEXTURES = ROOT / "src/main/resources/assets/freshcaa/textures"
RUNS = ROOT / "tools/art/out/runs"

# Textures where a specific test render beat the manifest run: texture -> path under out/runs.
OVERRIDES = {
    "block/macadamia_log": "batch1/generated/test_macadamia_log_nea_d0.5.png",
    "item/raisin": "batch1/generated/test_raisin_bic_d0.75.png",
}
# Textures to leave untouched (functional sheets, or not worth the risk).
SKIP = {"gui/container/sun_drying_table", "item/factory_worker_spawn_egg"}
SKIN_SOURCE = "batch1/generated/test_factory_worker_nea_d0.22.png"


def strip_halo(img: Image.Image, max_saturation: float = 0.12) -> Image.Image:
    """Make near-grey pixels transparent: the flat staging background bleeding into an item's outline."""
    px = img.load()
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            hi, lo = max(r, g, b), min(r, g, b)
            sat = 0 if hi == 0 else (hi - lo) / hi
            if sat < max_saturation and hi > 60:
                px[x, y] = (r, g, b, 0)
    return img


def composite_skin(generated: Path, original: Path) -> Image.Image:
    """Keep the generated shading but restore strongly blue pixels (the earmuffs) from the original."""
    gen = Image.open(generated).convert("RGBA")
    org = Image.open(original).convert("RGBA")
    gp, op = gen.load(), org.load()
    for y in range(org.height):
        for x in range(org.width):
            r, g, b, a = op[x, y]
            if a > 0 and b > r + 40 and b > g + 40:
                gp[x, y] = op[x, y]
    return gen


def latest_run() -> str:
    runs = sorted((p for p in RUNS.iterdir() if (p / "generated").is_dir()), key=lambda p: p.stat().st_mtime)
    if not runs:
        raise SystemExit("no runs under " + str(RUNS))
    return runs[-1].name


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--run", help="run name under out/runs (default: most recent)")
    ap.add_argument("--only", nargs="*", help="texture names to apply, e.g. pecan_leaves item/raisin")
    ap.add_argument("--no-overrides", action="store_true", help="ignore OVERRIDES and the skin composite")
    args = ap.parse_args()
    run = args.run or latest_run()
    gen = RUNS / run / "generated"
    only = set(args.only or [])
    applied = []
    for src in sorted(gen.rglob("*.png")):
        rel = src.relative_to(gen).with_suffix("").as_posix()
        if rel.startswith("test_") or rel in SKIP or (only and rel not in only and Path(rel).name not in only):
            continue
        dest = TEXTURES / f"{rel}.png"
        if not dest.exists():
            continue
        chosen = src if args.no_overrides or rel not in OVERRIDES else RUNS / OVERRIDES[rel]
        if rel == "item/raisin":
            strip_halo(Image.open(chosen).convert("RGBA")).save(dest)
        else:
            shutil.copyfile(chosen, dest)
        applied.append(rel)

    skin_gen = RUNS / SKIN_SOURCE
    if not args.no_overrides and skin_gen.exists() and (not only or {"factory_worker", "entity/factory_worker"} & only):
        skin = TEXTURES / "entity/factory_worker.png"
        composite_skin(skin_gen, skin).save(skin)
        applied.append("entity/factory_worker")
    print(f"run {run}: applied {len(applied)} textures")
    for a in applied:
        print(" ", a)


if __name__ == "__main__":
    main()
