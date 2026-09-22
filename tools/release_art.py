"""Release art built with PIL from the mod's own textures.

    python tools/release_art.py logo    -> src/main/resources/logo.png (128x128 mod logo)
    python tools/release_art.py cover   -> tools/out/planet_minecraft_cover.png (1280x720)
    python tools/release_art.py setup   -> tools/out/planet_minecraft_setup.png (recipes + lens table)
    python tools/release_art.py features-> tools/out/planet_minecraft_features.png (feature cards)

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


def rotate(pt, rot):
    if not rot:
        return pt
    a = math.radians(rot["angle"])
    ox, oy, oz = rot["origin"]
    x, y, z = pt[0] - ox, pt[1] - oy, pt[2] - oz
    c, s_ = math.cos(a), math.sin(a)
    if rot["axis"] == "x":
        y, z = y * c - z * s_, y * s_ + z * c
    elif rot["axis"] == "y":
        x, z = x * c + z * s_, -x * s_ + z * c
    else:
        x, y = x * c - y * s_, x * s_ + y * c
    return x + ox, y + oy, z + oz


def load_elements(models: list[str], textures: dict[str, Image.Image]) -> list[dict]:
    elements = []
    for name in models:
        m = json.loads((ASSETS / f"models/block/{name}.json").read_text())
        for key, ref in m["textures"].items():
            textures[key] = tex("block/" + ref.split(":block/")[1])
        elements.extend(m["elements"])
    return elements


def cube_elements(top: str, side: str) -> list[dict]:
    faces = {f: {"texture": "#" + (top if f in ("up", "down") else side)} for f in ("up", "down", "north", "south", "east", "west")}
    return [{"from": [0, 0, 0], "to": [16, 16, 16], "faces": faces}]


def render_elements(elements: list[dict], textures: dict[str, Image.Image], s: int = 8, layers=None, yaw: float = 0) -> Image.Image:
    """Isometric render of block-model elements, including rotated ones, with back-face culling.

    `layers` maps element index -> draw layer; higher layers draw after everything below them.
    """
    size = int(s * 48)
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    ox, oy = size / 2, s * 28
    layers = layers or {}

    view = {"angle": yaw, "axis": "y", "origin": [8, 0, 8]}

    def P(pt):
        pt = rotate(pt, view)
        return project(pt[0], pt[1], pt[2], s, ox, oy)

    def depth(e):
        x0, y0, z0 = e["from"]
        x1, y1, z1 = e["to"]
        cx, cy, cz = rotate(rotate(((x0 + x1) / 2, (y0 + y1) / 2, (z0 + z1) / 2), e.get("rotation")), view)
        return cx + cy + cz

    order = sorted(range(len(elements)), key=lambda i: (layers.get(i, 0), depth(elements[i])))
    for i in order:
        e = elements[i]
        x0, y0, z0 = e["from"]
        x1, y1, z1 = e["to"]
        rot = e.get("rotation")
        # (p0, p1, p2, normal): uv origin, u end, v end
        geom = {
            "up": ((x0, y1, z0), (x1, y1, z0), (x0, y1, z1), (0, 1, 0)),
            "down": ((x0, y0, z1), (x1, y0, z1), (x0, y0, z0), (0, -1, 0)),
            "south": ((x0, y1, z1), (x1, y1, z1), (x0, y0, z1), (0, 0, 1)),
            "north": ((x1, y1, z0), (x0, y1, z0), (x1, y0, z0), (0, 0, -1)),
            "east": ((x1, y1, z1), (x1, y1, z0), (x1, y0, z1), (1, 0, 0)),
            "west": ((x0, y1, z0), (x0, y1, z1), (x0, y0, z0), (-1, 0, 0)),
        }
        for fname, f in e["faces"].items():
            a, b, c, n = geom[fname]
            n = rotate(n, {**rot, "origin": [0, 0, 0]}) if rot else n
            n = rotate(n, {**view, "origin": [0, 0, 0]})
            if n[0] + n[1] + n[2] <= 1e-6:  # facing away from the camera at (+x,+y,+z)
                continue
            t = textures[f["texture"].lstrip("#")]
            u0, v0, u1, v1 = f.get("uv", [0, 0, 16, 16])
            face = t.crop((int(u0), int(v0), max(int(u0) + 1, int(u1)), max(int(v0) + 1, int(v1))))
            if fname in ("west", "north"):
                face = face.transpose(Image.FLIP_LEFT_RIGHT)
            shade_f = 0.6 + 0.4 * max(n[1], 0) + 0.2 * max(n[2], 0)
            face = up(face, s)
            if f.get("light_emission"):
                shade_f = 1.0
            pa, pb, pc = P(rotate(a, rot)), P(rotate(b, rot)), P(rotate(c, rot))
            if abs((pb[0] - pa[0]) * (pc[1] - pa[1]) - (pc[0] - pa[0]) * (pb[1] - pa[1])) < 1e-6:
                continue  # edge-on
            paste_quad(canvas, shade(face, shade_f), pa, pb, pc)
    return canvas.crop(canvas.getbbox())


def render_block(models: list[str], s: int = 8, overlay: list[str] | None = None, yaw: float = 0) -> Image.Image:
    """Isometric render of block model JSON files; `overlay` models always draw on top (beams)."""
    textures: dict[str, Image.Image] = {}
    elements = load_elements(models, textures)
    layers = {}
    if overlay:
        first = len(elements)
        elements += load_elements(overlay, textures)
        layers = {i: 1 for i in range(first, len(elements))}
    return render_elements(elements, textures, s, layers, yaw)


def render_cube(top: str, side: str, s: int = 8) -> Image.Image:
    """Cube from block textures; 'minecraft:x' comes from the jar, anything else from the mod."""
    def load(n):
        return mc_tex(f"block/{n[10:]}.png") if n.startswith("minecraft:") else tex("block/" + n)
    return render_elements(cube_elements("t", "s"), {"t": load(top), "s": load(side)}, s)


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


# ------------------------------------------------------------ shared helpers
LENS_TABLE = ["sun_drying_table", "sun_drying_table_grapes"] + [f"sun_drying_table_lens_{d}" for d in "nesw"]
BEAMS = [f"sun_drying_table_beam_{d}" for d in "nesw"]


def item(name: str) -> Image.Image:
    """Item sprite: 'freshcaa:x' from the mod, 'minecraft:x' from the jar (glass_pane uses the glass block face)."""
    ns, _, n = name.partition(":")
    if ns == "minecraft":
        return mc_tex(f"block/{n[:-5]}.png" if n.endswith("_pane") else f"item/{n}.png")
    if not (TEX / f"item/{n}.png").exists():  # block-only: tiny isometric render as the item
        r = render_block([n], s=2)
        return r.resize((16, int(16 * r.height / r.width)), Image.NEAREST)
    return tex(f"item/{n}")


def background(W: int, H: int) -> Image.Image:
    planks = up(mc_tex("block/oak_planks.png"), 5)
    bg = Image.new("RGBA", (W, H))
    for y in range(0, H, planks.height):
        for x in range(0, W, planks.width):
            bg.paste(planks, (x, y))
    bg = Image.alpha_composite(bg, Image.new("RGBA", (W, H), (0, 0, 0, 70)))
    small = Image.new("L", (64, 36), 0)
    px = small.load()
    for y in range(36):
        for x in range(64):
            dx, dy = (x + 0.5) / 64 - 0.5, (y + 0.5) / 36 - 0.5
            px[x, y] = int(min(1.0, (dx * dx + dy * dy) * 2.2) * 150)
    vign = small.resize((W, H), Image.BICUBIC)
    return Image.alpha_composite(bg, Image.merge("RGBA", (Image.new("L", (W, H), 0),) * 3 + (vign,)))


def drop_onto(canvas: Image.Image, img: Image.Image, x: int, y: int, sh: int = 8):
    shadow = Image.new("RGBA", img.size, (0, 0, 0, 110))
    canvas.paste(shadow, (x + sh, y + sh), img.split()[3])
    canvas.alpha_composite(img, (x, y))


def panel(w: int, h: int) -> Image.Image:
    """Inventory-style panel: light grey with the classic bevel."""
    p = Image.new("RGBA", (w, h), (198, 198, 198, 255))
    d = p.load()
    for i in range(w):
        for k in range(4):
            d[i, k] = (255, 255, 255, 255)
            d[i, h - 1 - k] = (85, 85, 85, 255)
    for j in range(h):
        for k in range(4):
            d[k, j] = (255, 255, 255, 255)
            d[w - 1 - k, j] = (85, 85, 85, 255)
    return p


def slot(k: int) -> Image.Image:
    s = Image.new("RGBA", (18 * k, 18 * k), (139, 139, 139, 255))
    d = s.load()
    for i in range(18 * k):
        for j in range(k):
            d[i, j] = (55, 55, 55, 255)
            d[i, 18 * k - 1 - j] = (255, 255, 255, 255)
    for j in range(18 * k):
        for i in range(k):
            d[i, j] = (55, 55, 55, 255)
            d[18 * k - 1 - i, j] = (255, 255, 255, 255)
    return s


def arrow(k: int, color=(80, 80, 80, 255)) -> Image.Image:
    a = Image.new("RGBA", (22, 15), (0, 0, 0, 0))
    d = a.load()
    for x in range(0, 15):
        for y in range(5, 10):
            d[x, y] = color
    for i in range(8):
        for y in range(i, 15 - i):
            d[14 + i, y] = color
    return up(a, k)


def crafting(grid: list[list[str | None]], result: str, k: int = 5, count: int = 1) -> Image.Image:
    """3x3 crafting grid -> result, inventory style, at k px per texel."""
    font = AsciiFont()
    W, H = 18 * 3 + 8 + 22 + 8 + 26 + 16, 18 * 3 + 16
    p = panel(W * k, H * k)
    sl = slot(k)
    for r in range(3):
        for c in range(3):
            x, y = (8 + c * 18) * k, (8 + r * 18) * k
            p.alpha_composite(sl, (x, y))
            it = grid[r][c]
            if it:
                p.alpha_composite(up(item(it), k), (x + k, y + k))
    ax = (8 + 54 + 8) * k
    p.alpha_composite(arrow(k), (ax, (8 + 27 - 7) * k))
    rx, ry = (8 + 54 + 8 + 22 + 8) * k, (8 + 27 - 13) * k
    p.alpha_composite(up(slot(1).resize((26, 26), Image.NEAREST), k), (rx, ry))
    p.alpha_composite(up(item(result), k), (rx + 5 * k, ry + 5 * k))
    if count > 1:
        n = font.render(str(count), scale=k)
        p.alpha_composite(n, (rx + 26 * k - n.width - 2 * k, ry + 26 * k - n.height - 2 * k))
    return p


def caption(font: AsciiFont, text: str, scale: int = 3, color=(255, 255, 255)) -> Image.Image:
    return font.render(text, color=color, scale=scale)


def box(x0, y0, z0, w, h, d, u, v, inflate=0.0, rotation=None, mirror=False) -> dict:
    """Villager-style box: y-up coordinates, texOffs (u, v) laid out the way ModelPart boxes are."""
    faces = {
        "up": [u + d, v, u + d + w, v + d],
        "south": [u + d, v + d, u + d + w, v + d + h],        # front
        "west": [u, v + d, u + d, v + d + h],                  # the character's right side
        "east": [u + d + w, v + d, u + 2 * d + w, v + d + h],  # the character's left side
        "north": [u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h],
    }
    if mirror:
        faces["east"], faces["west"] = faces["west"], faces["east"]
    e = {
        "from": [x0 - inflate, y0 - inflate, z0 - inflate],
        "to": [x0 + w + inflate, y0 + h + inflate, z0 + d + inflate],
        "faces": {k: {"uv": uv, "texture": "#skin"} for k, uv in faces.items()},
    }
    if rotation:
        e["rotation"] = rotation
    return e


def worker_sprite(k: int = 6, yaw: float = 25) -> Image.Image:
    """3/4 view of the Factory Worker built from the villager model boxes and its skin."""
    X, Z = 8, 8  # centre the model on the renderer's yaw pivot
    arms_rot = {"angle": -43, "axis": "x", "origin": [X, 21, Z + 1]}
    base = [
        box(X - 4, 0, Z - 2, 4, 12, 4, 0, 22),                 # right leg
        box(X, 0, Z - 2, 4, 12, 4, 0, 22, mirror=True),        # left leg
        box(X - 4, 12, Z - 3, 8, 12, 6, 16, 20),               # body
        box(X - 4, 24, Z - 4, 8, 10, 8, 0, 0),                 # head
    ]
    over = [
        box(X - 4, 6, Z - 3, 8, 18, 6, 0, 38, inflate=0.5),    # jacket
        box(X - 8, 15, Z - 1, 4, 8, 4, 44, 22, rotation=arms_rot, mirror=True),
        box(X + 4, 15, Z - 1, 4, 8, 4, 44, 22, rotation=arms_rot),
        box(X - 4, 15, Z - 1, 8, 4, 4, 40, 38, rotation=arms_rot),
        box(X - 1, 21, Z + 4, 2, 4, 2, 24, 0),                 # nose
        box(X - 4, 24, Z - 4, 8, 10, 8, 32, 0, inflate=0.5),   # hat layer
    ]
    layers = {i: 1 for i in range(len(base), len(base) + len(over))}
    return render_elements(base + over, {"skin": tex("entity/factory_worker")}, k, layers, yaw)


def tinted(img: Image.Image, rgb) -> Image.Image:
    r, g, b, a = img.split()
    return Image.merge("RGBA", (r.point(lambda v: v * rgb[0] // 255), g.point(lambda v: v * rgb[1] // 255), b.point(lambda v: v * rgb[2] // 255), a))


FOLIAGE = (119, 171, 47)


# ----------------------------------------------------------------- setup image
def make_setup() -> Path:
    W, H = 1280, 720
    img = background(W, H)
    font = AsciiFont()

    img.alpha_composite(font.render("Setting up the Sun Drying Table", color=(255, 235, 140), scale=5, shadow=(90, 60, 20)), (40, 36))

    st = "minecraft:stick"; g = "minecraft:gold_ingot"; d = "minecraft:diamond"
    n = "minecraft:iron_nugget"; gp = "minecraft:glass_pane"
    r1 = crafting([[st, g, st], [g, d, g], [st, g, st]], "freshcaa:sun_drying_table", k=3)
    r2 = crafting([[n, n, n], [n, gp, n], [n, n, n]], "freshcaa:lens", k=3)
    drop_onto(img, r1, 40, 120)
    img.alpha_composite(caption(font, "1. Sticks, gold and a diamond.", 3), (44, 120 + r1.height + 14))
    drop_onto(img, r2, 40, 390)
    img.alpha_composite(caption(font, "2. Iron nuggets around a glass pane.", 3), (44, 390 + r2.height + 14))
    img.alpha_composite(caption(font, "   Optional. Up to four per table.", 3, (200, 200, 200)), (44, 390 + r2.height + 48))

    table = render_block(LENS_TABLE, s=11, overlay=BEAMS, yaw=-15)
    drop_onto(img, table, 800, 120, 10)
    for i, (line, col) in enumerate([("3. Put it under open sky.", (255, 255, 255)), ("   Grapes on the mesh,", (255, 255, 255)),
                                     ("   lenses on the sides.", (255, 255, 255)), ("   Four lenses dry 5x faster.", (255, 235, 140))]):
        img.alpha_composite(caption(font, line, 3, col), (700, 530 + i * 36))

    gr, ar, ra = up(item("freshcaa:grapes"), 4), arrow(4, (255, 235, 140, 255)), up(item("freshcaa:raisin"), 4)
    x = 960
    drop_onto(img, gr, x, 34, 5)
    img.alpha_composite(ar, (x + 80, 36))
    drop_onto(img, ra, x + 184, 34, 5)

    OUT.mkdir(parents=True, exist_ok=True)
    out = OUT / "planet_minecraft_setup.png"
    img.convert("RGB").save(out)
    return out


# -------------------------------------------------------------- features image
def make_features() -> Path:
    W, H = 1280, 720
    img = background(W, H)
    font = AsciiFont()
    img.alpha_composite(font.render("What's in the jar", color=(255, 235, 140), scale=6, shadow=(90, 60, 20)), (40, 28))

    def card(x, y, w, h, title):
        box_ = Image.new("RGBA", (w, h), (0, 0, 0, 95))
        img.alpha_composite(box_, (x, y))
        img.alpha_composite(caption(font, title, 3, (255, 235, 140)), (x + 14, y + 12))

    def foot(x, y, text):
        img.alpha_composite(caption(font, text, 2, (220, 220, 220)), (x + 16, y + 254))

    cw, ch, gap, top = 400, 292, 20, 100
    cols = [gap, gap * 2 + cw, gap * 3 + cw * 2]
    rows = [top, top + ch + 16]

    # 1. cookies + dough
    x, y = cols[0], rows[0]
    card(x, y, cw, ch, "Cookies + dough")
    cookies = ["chocolate_chip_cookie", "oatmeal_raisin_cookie", "peanut_butter_cookie", "pecan_cookie", "white_macadamia_cookie"]
    for i, c in enumerate(cookies):
        drop_onto(img, up(tex(f"item/{c}"), 4), x + 22 + i * 72, y + 58, 5)
        drop_onto(img, up(tex(f"item/{c}_dough"), 4), x + 22 + i * 72, y + 150, 5)
    foot(x, y, "Dough: egg, sugar, wheat. Bake.")

    # 2. sun drying table
    x, y = cols[1], rows[0]
    card(x, y, cw, ch, "Sun Drying Table")
    table = render_block(LENS_TABLE, s=5, overlay=BEAMS, yaw=-15)
    drop_onto(img, table, x + (cw - table.width) // 2, y + 52, 6)
    foot(x, y, "Sun-powered raisins. No fuel.")

    # 3. crops
    x, y = cols[2], rows[0]
    card(x, y, cw, ch, "Crops from grass")
    farm = render_cube("minecraft:farmland_moist", "minecraft:dirt", s=3)
    for i, (plant, seed) in enumerate([("grape_vine_stage5", "grapes"), ("peanut_plant_stage4", "peanuts")]):
        px = x + 50 + i * 190
        drop_onto(img, farm, px, y + 138, 5)
        drop_onto(img, up(tex(f"block/{plant}"), 6), px - 6, y + 62, 5)
        drop_onto(img, up(tex(f"item/{seed}"), 3), px + 110, y + 60, 4)
    foot(x, y, "Farmed like wheat.")

    # 4. trees
    x, y = cols[0], rows[1]
    card(x, y, cw, ch, "Nut trees")
    for i, wood in enumerate(["pecan", "macadamia"]):
        px = x + 40 + i * 190
        leaves = render_elements(cube_elements("t", "t"), {"t": tinted(tex(f"block/{wood}_leaves"), FOLIAGE)}, s=3)
        drop_onto(img, render_cube(f"{wood}_log_top", f"{wood}_log", s=3), px, y + 140, 5)
        drop_onto(img, leaves, px, y + 92, 5)
        drop_onto(img, up(tex(f"block/{wood}_sapling"), 3), px + 110, y + 150, 4)
        drop_onto(img, up(tex("item/" + ("pecan" if wood == "pecan" else "white_macadamia")), 3), px + 110, y + 80, 4)
    foot(x, y, "Pecan and Macadamia.")

    # 5. factory worker
    x, y = cols[1], rows[1]
    card(x, y, cw, ch, "Factory Worker")
    w = worker_sprite(6)
    drop_onto(img, w, x + 36, y + 54, 8)
    drop_onto(img, up(tex("item/factory_worker_spawn_egg"), 4), x + 316, y + 216, 5)
    for i, line in enumerate(["Hard hat. Earmuffs.", "Wanders in the sun", "looking for chests.", "Has never once", "carried a cookie."]):
        img.alpha_composite(caption(font, line, 2, (220, 220, 220)), (x + 176, y + 66 + i * 28))

    # 6. the rest
    x, y = cols[2], rows[1]
    card(x, y, cw, ch, "Also in the box")
    for i, line in enumerate(["- Fresh Cookies creative tab", "- Seven advancements", "- Config: lens speed, grass", "  drop odds, dough poison odds", "- Servers and singleplayer", "- Minecraft 26.3 / NeoForge"]):
        img.alpha_composite(caption(font, line, 2, (220, 220, 220)), (x + 20, y + 62 + i * 30))
    drop_onto(img, up(tex("item/cookie_dough"), 4), x + 320, y + 224, 5)

    OUT.mkdir(parents=True, exist_ok=True)
    out = OUT / "planet_minecraft_features.png"
    img.convert("RGB").save(out)
    return out


if __name__ == "__main__":
    what = sys.argv[1] if len(sys.argv) > 1 else "all"
    if what in ("logo", "all"):
        print(make_logo())
    if what in ("cover", "all"):
        print(make_cover())
    if what in ("setup", "all"):
        print(make_setup())
    if what in ("features", "all"):
        print(make_features())
