"""Render every mod texture onto one labelled sheet, optionally side by side with a generated set.

    python tools/art/contact_sheet.py                               # originals only
    python tools/art/contact_sheet.py --compare tools/art/out/runs/<run>/generated
"""
import argparse
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
TEXTURES = ROOT / "src/main/resources/assets/freshcaa/textures"
OUT = ROOT / "tools/art/out"

SCALE = 6
CELL = 16 * SCALE
PAD = 8
LABEL_H = 14
COLUMNS = 8


def load_cell(path: Path) -> Image.Image:
    img = Image.open(path).convert("RGBA")
    if max(img.size) > 32:  # mob skin / GUI: shrink so the sheet stays readable
        img = img.resize((32, 32), Image.Resampling.BOX)
    return img.resize((CELL, CELL), Image.Resampling.NEAREST)


def build(compare: Path | None) -> Path:
    files = sorted(TEXTURES.rglob("*.png"))
    cell_w = CELL * (2 if compare else 1)
    rows = (len(files) + COLUMNS - 1) // COLUMNS
    sheet = Image.new("RGBA", (COLUMNS * (cell_w + PAD) + PAD, rows * (CELL + LABEL_H + PAD) + PAD), (40, 40, 40, 255))
    draw = ImageDraw.Draw(sheet)
    for i, path in enumerate(files):
        x = PAD + (i % COLUMNS) * (cell_w + PAD)
        y = PAD + (i // COLUMNS) * (CELL + LABEL_H + PAD)
        sheet.alpha_composite(load_cell(path), (x, y))
        if compare:
            other = compare / path.relative_to(TEXTURES)
            if other.exists():
                sheet.alpha_composite(load_cell(other), (x + CELL, y))
            else:
                draw.rectangle((x + CELL, y, x + 2 * CELL - 1, y + CELL - 1), outline=(200, 60, 60, 255))
        draw.text((x, y + CELL), path.stem[:22], fill=(230, 230, 230, 255))
    OUT.mkdir(parents=True, exist_ok=True)
    target = OUT / ("contact_sheet_compare.png" if compare else "contact_sheet_original.png")
    sheet.save(target)
    return target


if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("--compare", type=Path)
    print(build(ap.parse_args().compare))
