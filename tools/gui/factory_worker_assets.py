"""Draws the Factory Worker task screen sheet and the two small placeholder textures it needs.

Outputs (all under src/main/resources/assets/freshcaa/textures):
  gui/container/factory_worker.png  256x256 sheet, 176x222 panel at (0,0) with slot frames
  item/clipboard.png                16x16 item icon
  block/work_post_sign.png          16x16 sign board face (cookie on planks)

Run: python tools/gui/factory_worker_assets.py
"""
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
TEX = ROOT / "src/main/resources/assets/freshcaa/textures"

PANEL_W, PANEL_H = 176, 222
BG = (198, 198, 198, 255)
LIGHT = (255, 255, 255, 255)
DARK = (85, 85, 85, 255)
BLACK = (0, 0, 0, 255)
SLOT_BG = (139, 139, 139, 255)
SLOT_DARK = (55, 55, 55, 255)
SLOT_LIGHT = (255, 255, 255, 255)

CARRY_X, CARRY_Y, CARRY_COUNT = 8, 108, 4
FILTER_X, FILTER_Y = 152, 108
INV_X, INV_Y = 8, 140
HOTBAR_Y = INV_Y + 58


def panel(draw: ImageDraw.ImageDraw) -> None:
    """Vanilla-style bevelled grey panel with rounded corners."""
    w, h = PANEL_W, PANEL_H
    draw.rectangle([0, 0, w - 1, h - 1], fill=BG)
    # outer black outline with the corners knocked off
    draw.rectangle([3, 0, w - 4, 0], fill=BLACK)
    draw.rectangle([3, h - 1, w - 4, h - 1], fill=BLACK)
    draw.rectangle([0, 3, 0, h - 4], fill=BLACK)
    draw.rectangle([w - 1, 3, w - 1, h - 4], fill=BLACK)
    for x, y in [(1, 1), (2, 1), (1, 2), (w - 2, 1), (w - 3, 1), (w - 2, 2), (1, h - 2), (2, h - 2), (1, h - 3),
                 (w - 2, h - 2), (w - 3, h - 2), (w - 2, h - 3)]:
        draw.point((x, y), fill=BLACK)
    # bevel: light top/left, dark bottom/right
    draw.rectangle([3, 1, w - 4, 2], fill=LIGHT)
    draw.rectangle([1, 3, 2, h - 4], fill=LIGHT)
    draw.rectangle([3, h - 3, w - 4, h - 2], fill=DARK)
    draw.rectangle([w - 3, 3, w - 2, h - 4], fill=DARK)
    draw.point((3, 3), fill=LIGHT)
    draw.point((w - 4, h - 4), fill=DARK)
    # transparent corners
    for x, y in [(0, 0), (1, 0), (0, 1), (w - 1, 0), (w - 2, 0), (w - 1, 1), (0, h - 1), (1, h - 1), (0, h - 2),
                 (w - 1, h - 1), (w - 2, h - 1), (w - 1, h - 2)]:
        draw.point((x, y), fill=(0, 0, 0, 0))


def slot(draw: ImageDraw.ImageDraw, x: int, y: int) -> None:
    """18x18 inset slot frame whose top-left is at (x-1, y-1) for a slot at (x, y)."""
    x0, y0 = x - 1, y - 1
    draw.rectangle([x0, y0, x0 + 17, y0 + 17], fill=SLOT_BG)
    draw.rectangle([x0, y0, x0 + 16, y0], fill=SLOT_DARK)
    draw.rectangle([x0, y0, x0, y0 + 16], fill=SLOT_DARK)
    draw.rectangle([x0 + 1, y0 + 17, x0 + 17, y0 + 17], fill=SLOT_LIGHT)
    draw.rectangle([x0 + 17, y0 + 1, x0 + 17, y0 + 17], fill=SLOT_LIGHT)


def sheet() -> None:
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    panel(draw)
    # a faint divider between the task column and the binding panel, and above the status line
    draw.rectangle([63, 17, 63, 92], fill=DARK)
    draw.rectangle([64, 17, 64, 92], fill=LIGHT)
    draw.rectangle([8, 93, 167, 93], fill=DARK)
    draw.rectangle([8, 94, 167, 94], fill=LIGHT)
    for i in range(CARRY_COUNT):
        slot(draw, CARRY_X + i * 18, CARRY_Y)
    slot(draw, FILTER_X, FILTER_Y)
    for row in range(3):
        for col in range(9):
            slot(draw, INV_X + col * 18, INV_Y + row * 18)
    for col in range(9):
        slot(draw, INV_X + col * 18, HOTBAR_Y)
    out = TEX / "gui/container/factory_worker.png"
    out.parent.mkdir(parents=True, exist_ok=True)
    img.save(out)
    print("wrote", out)


def clipboard_icon() -> None:
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    board = (140, 96, 52, 255)
    board_dark = (96, 62, 30, 255)
    paper = (236, 232, 216, 255)
    paper_shadow = (200, 194, 172, 255)
    clip = (170, 170, 178, 255)
    clip_dark = (96, 96, 104, 255)
    ink = (70, 70, 90, 255)
    d.rectangle([3, 1, 12, 14], fill=board, outline=board_dark)
    d.rectangle([4, 4, 11, 13], fill=paper)
    d.line([(4, 13), (11, 13)], fill=paper_shadow)
    d.line([(11, 4), (11, 13)], fill=paper_shadow)
    d.rectangle([6, 0, 9, 3], fill=clip, outline=clip_dark)
    for y in (6, 8, 10):
        d.line([(5, y), (9, y)], fill=ink)
    d.point((10, 12), fill=ink)
    out = TEX / "item/clipboard.png"
    img.save(out)
    print("wrote", out)


def sign_face() -> None:
    """Board face: planks with a small cookie badge. Only rows 0-6 are used by the model."""
    planks = Image.open(TEX / "block/pecan_planks.png").convert("RGBA").resize((16, 16))
    img = planks.copy()
    d = ImageDraw.Draw(img)
    cookie = (196, 132, 62, 255)
    cookie_dark = (140, 88, 36, 255)
    chip = (74, 44, 24, 255)
    cx, cy = 8, 3
    d.ellipse([cx - 3, cy - 3, cx + 3, cy + 3], fill=cookie, outline=cookie_dark)
    for x, y in [(cx - 1, cy - 1), (cx + 1, cy), (cx - 1, cy + 2), (cx + 2, cy - 2)]:
        d.point((x, y), fill=chip)
    out = TEX / "block/work_post_sign.png"
    img.save(out)
    print("wrote", out)


if __name__ == "__main__":
    sheet()
    clipboard_icon()
    sign_face()
