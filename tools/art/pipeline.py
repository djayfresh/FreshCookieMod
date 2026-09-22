"""Texture upgrade pipeline: texture -> ComfyUI img2img (SDXL + pixel-art LoRA) -> clean game texture.

    python tools/art/pipeline.py test item/chocolate_chip_cookie --denoise 0.35 0.5 0.65
    python tools/art/pipeline.py run --manifest tools/art/manifest.json [--run NAME] [--only raisin pecan_leaves]

Every invocation belongs to a run (default: a timestamp, or --run NAME). Locally the run lives in
tools/art/out/runs/{run}/{staged,raw,generated}; on the ComfyUI server its images are saved under
output/FreshCookies/{run}/. `generated` holds post-processed textures ready for apply.py.
"""
import argparse
import json
import random
import time
from pathlib import Path

from PIL import Image, ImageChops

from comfy_client import ComfyClient

ROOT = Path(__file__).resolve().parents[2]
TEXTURES = ROOT / "src/main/resources/assets/freshcaa/textures"
OUT = ROOT / "tools/art/out"
RUNS = OUT / "runs"
SERVER_FOLDER = "FreshCookies"  # subfolder of the ComfyUI output directory

CHECKPOINT = "sd_xl_base_1.0.safetensors"
LORA = "pixel-art-xl.safetensors"
WORK_SIZE = 512  # diffusion resolution; textures are upscaled to this first

NEGATIVE = "blurry, smooth gradients, photo, realistic, 3d render, text, watermark, signature, frame, border, noise"
STYLE = "pixel art, minecraft texture, crisp pixels, flat colours, simple shading, limited palette, "

LEAF_HOLE_FRACTION = 0.14   # darkest share of pixels that become transparent
LEAF_LEVELS = (60, 215)     # grayscale range after contrast stretch (the game multiplies by the tint)
SEAM_BLEND = 96             # px of the 512 work image blended across the wrap seam for tileable textures


class Run:
    def __init__(self, name: str):
        self.name = name
        self.dir = RUNS / name
        self.staged = self.dir / "staged"
        self.raw = self.dir / "raw"
        self.generated = self.dir / "generated"
        for d in (self.staged, self.raw, self.generated):
            d.mkdir(parents=True, exist_ok=True)

    def prefix(self, name: str) -> str:
        """filename_prefix for SaveImage: ComfyUI nests it under its output folder."""
        return f"{SERVER_FOLDER}/{self.name}/{name}"


def synth_leaves(seed: int, size: int = 64) -> Image.Image:
    """Procedural tileable foliage base: overlapping leaf blobs in a few greens on a dark ground. Diffusion
    restyles this at low denoise; the old 16x16 mask carries no usable structure for a canopy."""
    from PIL import ImageDraw
    rng = random.Random(seed)
    img = Image.new("RGBA", (size, size), (30, 52, 22, 255))
    d = ImageDraw.Draw(img)
    greens = [(52, 88, 36), (66, 110, 44), (84, 134, 54), (104, 158, 66), (126, 178, 80)]
    for _ in range(size * size // 12):
        x, y = rng.randrange(size), rng.randrange(size)
        w, h = rng.randint(4, 8), rng.randint(3, 6)
        c = rng.choice(greens)
        for ox in (-size, 0, size):          # draw wrapped copies so the base tiles
            for oy in (-size, 0, size):
                d.ellipse((x + ox, y + oy, x + ox + w, y + oy + h), fill=c + (255,))
                d.ellipse((x + ox + 1, y + oy + 1, x + ox + w - 1, y + oy + h - 1),
                          fill=(min(255, c[0] + 18), min(255, c[1] + 22), min(255, c[2] + 12), 255))
    return img


def make_tileable(img: Image.Image, blend: int = SEAM_BLEND) -> Image.Image:
    """Hide the wrap seam: shift the image by half so the seams sit in the middle, then crossfade the
    seam cross with the unshifted image (whose middle is continuous). The result wraps on all four edges."""
    w, h = img.size
    shifted = ImageChops.offset(img, w // 2, h // 2)
    mask = Image.new("L", (w, h), 0)
    px = mask.load()
    for y in range(h):
        wy = max(0.0, 1.0 - abs(y - h / 2) / blend)
        for x in range(w):
            wx = max(0.0, 1.0 - abs(x - w / 2) / blend)
            px[x, y] = int(255 * max(wx, wy))
    return Image.composite(img, shifted, mask)


def stage_input(run: Run, rel: str, background=(128, 128, 128), *, synth_seed: int | None = None) -> tuple[Path, Image.Image]:
    """Composite the texture onto a flat background (diffusion has no alpha) and return the staged file + original.
    With synth_seed the staged image is a procedural foliage base instead of the original (leaves)."""
    src = TEXTURES / f"{rel}.png"
    original = Image.open(src).convert("RGBA")
    layer = synth_leaves(synth_seed) if synth_seed is not None else original
    bg = Image.new("RGBA", layer.size, tuple(background) + (255,))
    bg.alpha_composite(layer)
    staged = run.staged / f"freshcaa_{run.name}_{Path(rel).name}.png"
    bg.convert("RGB").save(staged)
    return staged, original


def build_workflow(image_name: str, prompt: str, *, denoise: float, seed: int, steps: int = 28, cfg: float = 6.0,
                   lora_strength: float = 0.9, prefix: str = "freshcaa", upscale: str = "nearest-exact") -> dict:
    return {
        "1": {"class_type": "CheckpointLoaderSimple", "inputs": {"ckpt_name": CHECKPOINT}},
        "2": {"class_type": "LoraLoader", "inputs": {"model": ["1", 0], "clip": ["1", 1], "lora_name": LORA,
                                                     "strength_model": lora_strength, "strength_clip": lora_strength}},
        "3": {"class_type": "CLIPTextEncode", "inputs": {"clip": ["2", 1], "text": STYLE + prompt}},
        "4": {"class_type": "CLIPTextEncode", "inputs": {"clip": ["2", 1], "text": NEGATIVE}},
        "5": {"class_type": "LoadImage", "inputs": {"image": image_name}},
        "6": {"class_type": "ImageScale", "inputs": {"image": ["5", 0], "upscale_method": upscale,
                                                     "width": WORK_SIZE, "height": WORK_SIZE, "crop": "disabled"}},
        "7": {"class_type": "VAEEncode", "inputs": {"pixels": ["6", 0], "vae": ["1", 2]}},
        "8": {"class_type": "KSampler", "inputs": {"model": ["2", 0], "positive": ["3", 0], "negative": ["4", 0],
                                                   "latent_image": ["7", 0], "seed": seed, "steps": steps, "cfg": cfg,
                                                   "sampler_name": "euler", "scheduler": "karras", "denoise": denoise}},
        "9": {"class_type": "VAEDecode", "inputs": {"samples": ["8", 0], "vae": ["1", 2]}},
        "10": {"class_type": "SaveImage", "inputs": {"images": ["9", 0], "filename_prefix": prefix}},
    }


def postprocess(raw: Path, original: Image.Image, target: int, colors: int = 32, grayscale: bool = False,
                tileable: bool = False) -> Image.Image:
    """Shrink the AI output to the target size, quantize, and restore the original alpha mask.
    grayscale keeps textures the game tints at runtime neutral."""
    img = Image.open(raw).convert("RGB")
    if tileable:
        img = make_tileable(img)
    small = img.resize((target, target), Image.Resampling.BOX)
    if grayscale:
        small = small.convert("L").convert("RGB")
    small = small.quantize(colors=colors, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).convert("RGBA")
    alpha = original.getchannel("A").resize((target, target), Image.Resampling.NEAREST)
    small.putalpha(alpha)
    return small


def postprocess_leaves(raw: Path, target: int, colors: int = 24) -> Image.Image:
    """Leaves: made tileable, grayscale (the game tints them), contrast-stretched so the tint has some depth,
    and the holes come from the darkest pixels of the generated foliage rather than the old 16x16 mask."""
    img = make_tileable(Image.open(raw).convert("RGB"))
    gray = img.resize((target, target), Image.Resampling.BOX).convert("L")
    lo, hi = gray.getextrema()
    a, b = LEAF_LEVELS
    gray = gray.point(lambda v: a + (v - lo) * (b - a) // max(1, hi - lo))
    values = sorted(gray.get_flattened_data()) if hasattr(gray, "get_flattened_data") else sorted(gray.getdata())
    cut = values[int(len(values) * LEAF_HOLE_FRACTION)]
    alpha = gray.point(lambda v: 0 if v <= cut else 255)
    out = gray.convert("RGB").quantize(colors=colors, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).convert("RGBA")
    out.putalpha(alpha)
    return out


def preview(images: list[Image.Image], labels: list[str], scale: int = 8) -> Image.Image:
    """Nearest-neighbour zoom of several small textures side by side for eyeballing."""
    from PIL import ImageDraw
    cell = max(i.size[0] for i in images) * scale
    sheet = Image.new("RGBA", (len(images) * (cell + 8) + 8, cell + 24), (60, 60, 60, 255))
    d = ImageDraw.Draw(sheet)
    for i, (img, label) in enumerate(zip(images, labels)):
        x = 8 + i * (cell + 8)
        sheet.alpha_composite(img.resize((cell, cell), Image.Resampling.NEAREST), (x, 8))
        d.text((x, cell + 10), label, fill=(240, 240, 240, 255))
    return sheet


def generate(client: ComfyClient, run: Run, rel: str, prompt: str, *, denoise: float, seed: int, upscale: str,
             target: int, colors: int, mode: str = "default", background=(128, 128, 128), tileable: bool = False,
             tag: str | None = None) -> Path:
    """Stage, generate, post-process one texture into run.generated; returns the generated file."""
    leaves = mode == "leaves"
    staged, original = stage_input(run, rel, background, synth_seed=seed if leaves else None)
    name = client.upload_image(staged)
    stem = tag or Path(rel).name
    out_rel = tag or rel
    wf = build_workflow(name, prompt, denoise=denoise, seed=seed, upscale=upscale, prefix=run.prefix(stem))
    raw = client.run(wf, run.raw / f"{out_rel}.png")[0]
    if leaves:
        out = postprocess_leaves(raw, target, colors)
    else:
        out = postprocess(raw, original, target, colors, mode == "grayscale", tileable)
    dest = run.generated / f"{out_rel}.png"
    dest.parent.mkdir(parents=True, exist_ok=True)
    out.save(dest)
    return dest


def cmd_test(args):
    client = ComfyClient(args.url)
    run = Run(args.run)
    prompt = args.prompt or Path(args.texture).name.replace("_", " ")
    original = Image.open(TEXTURES / f"{args.texture}.png").convert("RGBA")
    results, labels = [original], ["original"]
    for denoise in args.denoise:
        tag = f"test_{Path(args.texture).name}_{args.upscale[:3]}_d{denoise}"
        dest = generate(client, run, args.texture, prompt, denoise=denoise, seed=args.seed, upscale=args.upscale,
                        target=args.size, colors=args.colors, mode=args.mode, tileable=args.tileable, tag=tag)
        results.append(Image.open(dest).convert("RGBA")); labels.append(f"{args.upscale[:3]} d{denoise}")
        print("generated:", dest)
    p = run.dir / f"preview_{Path(args.texture).name}_{args.upscale[:3]}.png"
    preview(results, labels).save(p)
    print("preview:", p)


def cmd_run(args):
    client = ComfyClient(args.url)
    run = Run(args.run)
    manifest = json.loads(Path(args.manifest).read_text())
    only = set(args.only or [])
    for entry in manifest["textures"]:
        rel, prompt = entry["path"], entry["prompt"]
        if only and rel not in only and Path(rel).name not in only:
            continue
        seed = entry.get("seed", random.randrange(2**31))
        mode = entry.get("mode", "grayscale" if entry.get("grayscale") else "default")
        dest = generate(client, run, rel, prompt, seed=seed,
                        denoise=entry.get("denoise", manifest.get("denoise", 0.5)),
                        upscale=entry.get("upscale", manifest.get("upscale", "nearest-exact")),
                        target=entry.get("size", manifest.get("size", 32)),
                        colors=entry.get("colors", manifest.get("colors", 32)),
                        mode=mode, background=tuple(entry.get("background", (128, 128, 128))),
                        tileable=entry.get("tileable", False))
        print(f"{rel}: seed {seed} -> {dest}")
    print("run:", run.dir)


if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("--url", default=ComfyClient.__init__.__defaults__[0])
    ap.add_argument("--run", default=time.strftime("%Y%m%d-%H%M%S"), help="run name (folder under out/runs and on the server)")
    sub = ap.add_subparsers(dest="cmd", required=True)
    t = sub.add_parser("test")
    t.add_argument("texture", help="e.g. item/chocolate_chip_cookie")
    t.add_argument("--prompt")
    t.add_argument("--denoise", type=float, nargs="+", default=[0.35, 0.5, 0.65])
    t.add_argument("--seed", type=int, default=1234)
    t.add_argument("--size", type=int, default=32)
    t.add_argument("--colors", type=int, default=32)
    t.add_argument("--mode", default="default", choices=["default", "grayscale", "leaves"])
    t.add_argument("--tileable", action="store_true", help="blend the wrap seam (blocks that repeat)")
    t.add_argument("--upscale", default="nearest-exact", choices=["nearest-exact", "bilinear", "bicubic", "lanczos", "area"])
    t.set_defaults(func=cmd_test)
    r = sub.add_parser("run")
    r.add_argument("--manifest", required=True)
    r.add_argument("--only", nargs="*", help="texture names to (re)generate, e.g. raisin block/pecan_planks")
    r.set_defaults(func=cmd_run)
    a = ap.parse_args()
    a.func(a)
