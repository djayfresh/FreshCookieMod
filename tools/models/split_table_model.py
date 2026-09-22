"""Split the Blockbench Java export of the Sun Drying Table into the per-part block models the game needs.

    python tools/models/split_table_model.py

Input : tools/models/sun_drying_table_export.json (File > Export > Java Block/Item Model of sun_drying_table.bbmodel)
Output: assets/freshcaa/models/block/sun_drying_table.json            legs, rails, mesh   (cutout)
        assets/freshcaa/models/block/sun_drying_table_grapes.json     grapes layer        (cutout)
        assets/freshcaa/models/block/sun_drying_table_raisins.json    raisin layer        (cutout)
        assets/freshcaa/models/block/sun_drying_table_lens_{n,e,s,w}.json  post + glass    (translucent)
        assets/freshcaa/models/block/sun_drying_table_beam_{n,e,s,w}.json  light beam, shown only while lit

The blockstate file composes these with multipart: facing rotates every part, `contents` picks the
layer, lens_1..lens_4 add one lens model each and, together with `lit`, one beam each. Element rotation
signs are taken from Blockbench unchanged: its preview matches the game.
"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
EXPORT = ROOT / "tools/models/sun_drying_table_export.json"
MODELS = ROOT / "src/main/resources/assets/freshcaa/models/block"

PARTS = {
    "sun_drying_table": (lambda n: not n.startswith(("grapes", "raisins", "lens_", "beam_")), "minecraft:cutout"),
    "sun_drying_table_grapes": (lambda n: n.startswith("grapes"), "minecraft:cutout"),
    "sun_drying_table_raisins": (lambda n: n.startswith("raisins"), "minecraft:cutout"),
    "sun_drying_table_lens_n": (lambda n: n.startswith("lens_n_"), "minecraft:translucent"),
    "sun_drying_table_lens_s": (lambda n: n.startswith("lens_s_"), "minecraft:translucent"),
    "sun_drying_table_lens_w": (lambda n: n.startswith("lens_w_"), "minecraft:translucent"),
    "sun_drying_table_lens_e": (lambda n: n.startswith("lens_e_"), "minecraft:translucent"),
    "sun_drying_table_beam_n": (lambda n: n == "beam_n", "minecraft:translucent"),
    "sun_drying_table_beam_s": (lambda n: n == "beam_s", "minecraft:translucent"),
    "sun_drying_table_beam_w": (lambda n: n == "beam_w", "minecraft:translucent"),
    "sun_drying_table_beam_e": (lambda n: n == "beam_e", "minecraft:translucent"),
}


def main():
    export = json.loads(EXPORT.read_text(encoding="utf-8"))
    # Blockbench numbers textures; the game wants names. "#3" -> "#sun_drying_table_raisins".
    id_to_name = {f"#{k}": Path(v).name for k, v in export["textures"].items()}

    for model_name, (selector, render_type) in PARTS.items():
        elements = []
        used = []
        for element in export["elements"]:
            if not selector(element["name"]):
                continue
            element = json.loads(json.dumps(element))
            element.pop("name", None)
            for face in element["faces"].values():
                name = id_to_name[face["texture"]]
                face["texture"] = f"#{name}"
                if name not in used:
                    used.append(name)
            elements.append(element)
        if not elements:
            raise SystemExit(f"no elements matched {model_name}")
        textures = {name: f"freshcaa:block/{name}" for name in used}
        textures["particle"] = textures.get("sun_drying_table_wood", next(iter(textures.values())))
        model = {
            "credit": "Made with Blockbench (tools/models/sun_drying_table.bbmodel)",
            "render_type": render_type,
            "textures": textures,
            "elements": elements,
        }
        out = MODELS / f"{model_name}.json"
        out.write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8")
        print(f"{out.relative_to(ROOT)}: {len(elements)} elements, textures {used}")


if __name__ == "__main__":
    main()
