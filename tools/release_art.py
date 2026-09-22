"""Release art built with PIL from the mod's own textures.

    python tools/release_art.py logo    -> src/main/resources/logo.png (128x128 mod logo)
    python tools/release_art.py cover   -> tools/out/planet_minecraft_cover.png (1280x720)

The cover borrows oak planks and the ascii bitmap font from the Minecraft jar
that ModDev already has under build/moddev/artifacts.
"""
from __future__ import annotations

import io
import json
import math
import sys
import zipfile
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/freshcaa"
TEX = ASSETS / "textures"
MC_JAR = ROOT / "build/moddev/artifacts/minecraft-patched-26.3.0.8-beta-merged.jar"
OUT = ROOT / "tools/out"


def tex(name: str) -> Image.Image:
    return Image.open(TEX / f"{name}.png").convert("RGBA")


def mc_tex(path: str) -> Image.Image:
    with zipfile.ZipFile(MC_JAR) as z:
        return Image.open(io.BytesIO(z.read(f"assets/minecraft/textures/{path}"))).convert("RGBA")


def up(img: Image.Image, k: int) -> Image.Image:
    return img.resize((img.width * k, img.height * k), Image.NEAREST)


# ---------------------------------------------------------------- bitmap font
class AsciiFont:
    def __init__(self) -> None:
        self.sheet = mc_tex("font/ascii.png")

    def glyph(self, ch: str) -> Image.Image:
        c = ord(ch)
        g = self.sheet.crop(((c % 16) * 8, (c // 16) * 8, (c % 16) * 8 + 8, (c // 16) * 8 + 8))
        if ch == " ":
            return g.crop((0, 0, 3, 8))
        bbox = g.getbbox()
        return g.crop((0, 0, bbox[2], 8)) if bbox else g.crop((0, 0, 4, 8))

    def render(self, text: str, color=(255, 255, 255), scale: int = 8, shadow=(63, 63, 63)) -> Image.Image:
        glyphs = [self.glyph(ch) for ch in text]
        w = sum(g.width + 1 for g in glyphs)
        line = Image.new("RGBA", (w + 1, 9), (0, 0, 0, 0))
        x = 0
        for g in glyphs:
            line.paste(Image.new("RGBA", g.size, shadow + (255,)), (x + 1, 1), g.split()[3])
            x += g.width + 1
        x = 0
        for g in glyphs:
            line.paste(Image.new("RGBA", g.size, color + (255,)), (x, 0), g.split()[3])
            x += g.width + 1
        return up(line, scale)


# ------------------------------------------------------- isometric block render
COS30, SIN30 = math.cos(math.radians(30)), math.sin(math.radians(30))


def project(x: float, y: float, z: float, s: float, ox: float, oy: float):
    return ox + (x - z) * COS30 * s, oy + (x + z) * SIN30 * s - y * s


def shade(img: Image.Image, f: float) -> Image.Image:
    r, g, b, a = img.split()
    lut = [int(v * f) for v in range(256)]
    return Image.merge("RGBA", (r.point(lut), g.point(lut), b.point(lut), a))


def paste_quad(canvas: Image.Image, face: Image.Image, p0, p1, p2):
    """Map face so its (0,0) lands on p0, (w,0) on p1, (0,h) on p2 (a parallelogram)."""
    w, h = face.size
    ax, ay = (p1[0] - p0[0]) / w, (p1[1] - p0[1]) / w
    bx, by = (p2[0] - p0[0]) / h, (p2[1] - p0[1]) / h
    det = ax * by - bx * ay
    ia, ib, ic, id_ = by / det, -bx / det, -ay / det, ax / det
    xs = [p0[0], p1[0], p2[0], p1[0] + p2[0] - p0[0]]
    ys = [p0[1], p1[1], p2[1], p1[1] + p2[1] - p0[1]]
    x0, y0 = int(math.floor(min(xs))), int(math.floor(min(ys)))
    x1, y1 = int(math.ceil(max(xs))), int(math.ceil(max(ys)))
    c = ia * (x0 - p0[0]) + ib * (y0 - p0[1])
    f = ic * (x0 - p0[0]) + id_ * (y0 - p0[1])
    warped = face.transform((x1 - x0, y1 - y0), Image.AFFINE, (ia, ib, c, ic, id_, f), Image.NEAREST)
    canvas.alpha_composite(warped, (x0, y0))


def render_block(models: list[str], s: int = 8) -> Image.Image:
    """Isometric render of axis-aligned elements from block model JSON files (rotated elements skipped)."""
    textures: dict[str, Image.Image] = {}
    elements = []
    for name in models:
        m = json.loads((ASSETS / f"models/block/{name}.json").read_text())
        for key, ref in m["textures"].items():
            textures[key] = tex("block/" + ref.split(":block/")[1])
        for e in m["elements"]:
            if "rotation" in e:
                continue
            elements.append(e)
    size = int(s * 40)
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    ox, oy = size / 2, s * 22
    elements.sort(key=lambda e: (e["from"][0] + e["from"][2], e["from"][1]))
    for e in elements:
        x0, y0, z0 = e["from"]
        x1, y1, z1 = e["to"]
        faces = e["faces"]

        def face_img(fname):
            f = faces.get(fname)
            if not f:
                return None
            t = textures[f["texture"].lstrip("#")]
            u0, v0, u1, v1 = f.get("uv", [0, 0, 16, 16])
            return t.crop((int(u0), int(v0), max(int(u0) + 1, int(u1)), max(int(v0) + 1, int(v1))))

        def P(x, y, z):
            return project(x, y, z, s, ox, oy)

        top = face_img("up")
        if top is not None:
            paste_quad(canvas, up(top, s), P(x0, y1, z0), P(x1, y1, z0), P(x0, y1, z1))
        south = face_img("south")
        if south is not None:
            paste_quad(canvas, shade(up(south, s), 0.8), P(x0, y1, z1), P(x1, y1, z1), P(x0, y0, z1))
        east = face_img("east")
        if east is not None:
            paste_quad(canvas, shade(up(east, s), 0.6), P(x1, y1, z1), P(x1, y1, z0), P(x1, y0, z1))
    return canvas.crop(canvas.getbbox())


# ----------------------------------------------------------------------- outputs
def make_logo() -> Path:
    logo = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    logo.alpha_composite(up(tex("item/chocolate_chip_cookie"), 8), (0, 0))
    out = ROOT / "src/main/resources/logo.png"
    logo.save(out)
    return out


def make_cover() -> Path:
    W, H = 1280, 720
    planks = up(mc_tex("block/oak_planks.png"), 5)  # 80 px tiles
    cover = Image.new("RGBA", (W, H))
    for y in range(0, H, planks.height):
        for x in range(0, W, planks.width):
            cover.paste(planks, (x, y))
    cover = Image.alpha_composite(cover, Image.new("RGBA", (W, H), (0, 0, 0, 70)))
    small = Image.new("L", (64, 36), 0)
    px = small.load()
    for y in range(36):
        for x in range(64):
            dx, dy = (x + 0.5) / 64 - 0.5, (y + 0.5) / 36 - 0.5
            px[x, y] = int(min(1.0, (dx * dx + dy * dy) * 2.2) * 150)
    vign = small.resize((W, H), Image.BICUBIC)
    cover = Image.alpha_composite(cover, Image.merge("RGBA", (Image.new("L", (W, H), 0),) * 3 + (vign,)))

    def drop(img: Image.Image, x: int, y: int, sh: int = 10):
        shadow = Image.new("RGBA", img.size, (0, 0, 0, 110))
        cover.paste(shadow, (x + sh, y + sh), img.split()[3])
        cover.alpha_composite(img, (x, y))

    table = render_block(["sun_drying_table", "sun_drying_table_grapes"], s=12)
    drop(table, 800, 170, 14)

    cookies = ["chocolate_chip_cookie", "oatmeal_raisin_cookie", "peanut_butter_cookie", "pecan_cookie", "white_macadamia_cookie"]
    spots = [(80, 420), (250, 480), (430, 400), (600, 470), (720, 560)]
    for name, (x, y) in zip(cookies, spots):
        drop(up(tex(f"item/{name}"), 9), x, y)
    for name, (x, y) in [("grapes", (960, 540)), ("raisin", (1080, 470)), ("lens", (1120, 150))]:
        drop(up(tex(f"item/{name}"), 7), x, y)

    font = AsciiFont()
    title = font.render("Fresh Cookies", color=(255, 235, 140), scale=10, shadow=(90, 60, 20))
    sub = font.render("Cookies Are Amazing", color=(255, 255, 255), scale=5)
    ver = font.render("Minecraft 26.3 / NeoForge", color=(170, 255, 170), scale=4)
    cover.alpha_composite(title, (70, 70))
    cover.alpha_composite(sub, (76, 70 + title.height + 10))
    cover.alpha_composite(ver, (76, 70 + title.height + sub.height + 30))

    OUT.mkdir(parents=True, exist_ok=True)
    out = OUT / "planet_minecraft_cover.png"
    cover.convert("RGB").save(out)
    return out


if __name__ == "__main__":
    what = sys.argv[1] if len(sys.argv) > 1 else "all"
    if what in ("logo", "all"):
        print(make_logo())
    if what in ("cover", "all"):
        print(make_cover())
