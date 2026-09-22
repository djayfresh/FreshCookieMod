# FreshCookieMod modernization plan

## Findings (2026-09-22)
- Repo: Forge 1.6.4 in MCP layout, no tags, last commit May 2016. Version markers conflict (mcmod.info v0.8, ReadMe.txt V1.0, commit 59e55a2 "Version 0.9"). `Source 1.7.10` is an empty stub.
- Dropbox `G:\Dropbox\Projects\FreshCookieMod`:
  - `Mod/Trunk/forge/mcp`: 1.6.4 workspace, mod source identical to repo (whitespace only).
  - `1.8/`: ForgeGradle 1.8 port, Feb 2015, 43 java files + 3 blockstate JSONs + unrelated "pirates" test mod. Only work not in git.
  - `1.7.10/`, `src 1.7.10/`: stub / bare decompile. Discard.
  - `Install/`, `Mod.7z` (212 MB), `Realms-of-Chaos-master.zip`: built zips, unrelated. Discard.
- Art: 24 block + 17 item textures (16x16), factoryworker.png (64x64), SuntableGUI.png (256x256), .xcf sources in `Assets/`.
- Toolchain: JDK 7/8/10/17/18 installed. Modern MC needs JDK 21.

## Phase 1: Tag history  — DONE 2026-09-22
1. `v0.8-mc1.6.4` at 8af3c8b (2014 initial commit, mcmod.info v0.8)
2. `v0.9-mc1.6.4` at 59e55a2 ("Version 0.9")
3. `v1.0-mc1.6.4` at d133a14 (final 1.6.4 with Factory Worker mob)
4. Import Dropbox `1.8/src/main` (minus pirates + build output) on branch `archive/port-1.8`, tag `wip-mc1.8`
5. Push tags; Dropbox folder becomes cold backup
- Status: tags `v0.8-mc1.6.4`, `v0.9-mc1.6.4`, `v1.0-mc1.6.4`, `wip-mc1.8` pushed; the 1.8 archive branch was merged (PR #16) and the tree moved to `legacy/Source 1.8/` next to the 1.6.4 and 1.7.10 sources.

## Phase 2: Port to latest Minecraft  — DONE 2026-09-22 (branch neoforge-26.3)
- Loader: NeoForge 26.3.0.8-beta on Minecraft 26.3 (Java 25). Only beta NeoForge builds existed for 26.3 at port time; bump neo_version when a release lands.
- Rewrite guided by old code, not incremental port:
  - NeoForge MDK, Gradle, JDK 21, modid `freshcaa`; old source moves to `legacy/`
  - DeferredRegister for items/blocks (no numeric IDs, no config ID loader)
  - 5 cookies, 6 doughs, 6 materials as items with food properties
  - Peanut plant + grape vine as crop blocks with per-stage blockstate/model JSON
  - Pecan + macadamia logs/leaves/saplings/planks; world gen via JSON features + biome modifiers
  - Sun Drying Table: block entity + menu + screen; real day-only check (fixes old click-to-update bug)
  - Recipes -> JSON, achievements -> advancements, LanguageRegistry -> en_us.json
  - Factory Worker: EntityType, attributes, model, renderer, spawn egg, find-chest + circling goals
  - Creative tab; neoforge.mods.toml
- Verified: compiles, jar builds, client loads with no missing models/textures, dedicated server loads all data packs, RCON tests: blocks place, table dries grapes only in direct sun (roof/night/rain/wheat all stop it), worker summons, tree features place. Tag `v2.0.0` after merge.
- Dev server note: set pause-when-empty-seconds=0 in run/server.properties or block entities stop ticking after 60s with no players.

## Phase 3: Art upgrade via ComfyUI (external server)  — ATTEMPTED, ROLLED BACK; open
1. Connect to server, confirm models/custom nodes
2. Target resolution per asset class: blocks/items 32x32 (or keep 16x16), GUI + mob skin stay native size
3. Reusable workflow: img2img low denoise, pixel-art model, tile ControlNet; post: palette quantize, preserve alpha, enforce tiling on logs/leaves
4. Special cases: mob skin per UV region; GUI regenerate background only, slots untouched
5. Contact sheet old vs new, in-game review, iterate; generate real mod logo
6. Commit textures, originals under `legacy/`, tag `v2.1.0`

**Status (2026-09-22):** first attempt REJECTED. Full 42-texture batch (SDXL + pixel-art LoRA img2img, 32x32) was reviewed in game and rolled back to the v2.0.0 originals: blurry, lost detail, leaves floated and did not reach block edges. Tooling kept in `tools/art/` (per-run output under `out/runs/{run}` and `FreshCookies/{run}` on the server). Next attempt should trial a few textures with a different model/approach before any batch.


## Phase 4: Modelled Sun Drying Table with lenses  — DONE 2026-09-22, tag `v2.1.0`

Goal: replace the cube table with a real 3D table (four legs, mesh top), show what is on it
(empty / grapes / raisins), and add Mirror upgrades that sit above the table and focus sunlight onto it.
Branch `table-model`, tag `v2.1.0` when merged.

### Design decisions
- **Geometry**: JSON block model with elements, no block entity renderer. Legs 2x2 px at each corner,
  y 0-11; frame rails y 11-13; mesh top a 1 px thick plate at y 13-14 inset 1 px, textured with a cutout
  wire-mesh texture. Everything inside one block; the mirrors extend above it (element y up to 32 is
  allowed in block models).
- **Contents state**: `EnumProperty<Contents> CONTENTS` = `empty | grapes | raisins`, derived on the
  server from the slots each tick: result slot has items -> `raisins`; else input slot has items ->
  `grapes`; else `empty`. Rendered as an extra thin element on the mesh (grapes layer / raisin layer
  textures). Kept as block state, not a renderer, so it is cheap and works with shaders and item frames.
- **Mirrors**: new item `freshcaa:mirror`. The table gets 4 upgrade slots (container size 2 -> 6).
  `IntegerProperty MIRRORS` 0-4 mirrors the number of filled upgrade slots; rendered with a multipart
  blockstate that adds one tilted mirror element per count at the four corners (NE, NW, SE, SW relative
  to `facing`), roughly y 22-26, tilted 22.5 degrees inward, on thin posts. Effect: drying speed
  `1 + mirrors` progress per tick, i.e. 4 mirrors dry 5x faster (config `mirrorSpeedBonus`).
  Direct sunlight is still required; mirrors never remove that rule.
- **Upgrade slots** accept only items tagged `freshcaa:sun_drying_table_upgrades` (just `mirror` for now),
  stack size 1 per slot, so future upgrades (lens, tray) need only an item and tag entry.
- **Mirror recipe**: shaped, `iron_nugget` x3 top row, `glass_pane` x3 middle, `iron_nugget` x3 bottom
  -> 2 mirrors. Table recipe unchanged.
- **Rendering flags**: `noOcclusion()`, cutout render type declared in the model JSON
  (`"render_type": "minecraft:cutout"`), `isViewBlocking` false, custom `VoxelShape` = legs + top
  (mirrors are visual only, not collidable). Analog output signal and all existing behaviour kept.
- **Old worlds**: new properties default to `empty` / `0` on load; container grows from 2 to 6 slots,
  old item slots 0 and 1 keep their index, so existing tables and their contents survive.

### Steps
1. **Model generator** `tools/models/sun_drying_table.py`: writes the table, grapes, raisins, and mirror
   part models from a few numbers (leg size, mesh height, mirror tilt) so tweaks are one edit, not
   hand-editing 30 elements. Outputs into `assets/freshcaa/models/block/`.
2. **Textures** (16x16, hand-drawn/pixel edits, not AI): `sun_drying_table_wood` (legs/frame),
   `sun_drying_table_mesh` (cutout wire), `sun_drying_table_grapes`, `sun_drying_table_raisins`,
   `mirror` (glass face), `mirror_back`, item icon `item/mirror`. Retire the three old cube textures.
3. **Block**: add `CONTENTS` and `MIRRORS` properties, shape, render flags, rotate/mirror handling for the
   new properties; blockstate JSON becomes multipart (facing x contents x mirror parts).
4. **Block entity**: 6 slots (`SLOT_UPGRADE_0..3`), `getMirrorCount()`, speed multiplier in `serverTick`,
   state sync when contents or mirror count changes, `canPlaceItem` restricts upgrade slots to the tag,
   `WorldlyContainer` faces unchanged (hoppers never touch upgrade slots).
5. **Menu + screen**: 4 upgrade slots in a column on the right (x 152, y 17/35/53/71), tag-restricted
   `Slot.mayPlace`, `quickMoveStack` routes mirrors to upgrade slots; GUI sheet gets the four slot frames
   and a small mirror ghost icon (256x256 sheet edited with a PIL script, not regenerated).
6. **Item**: register `MIRROR`, add to creative tab, recipe + advancement, `sun_drying_table_upgrades`
   tag, lang keys (item, config), table item model = empty table (no mirrors).
7. **Config**: `mirrorSpeedBonus` (default 1.0 per mirror, 0-4).
8. **Verify**: compile; dedicated server RCON tests (place table, put grapes -> `contents=grapes`,
   wait -> `raisins`, insert 4 mirrors -> `mirrors=4` and roughly 5x throughput, cover -> stops, break ->
   drops grapes/raisins/mirrors); client run for the model, shape outline, cutout mesh, mirror placement
   from all four facings, item in hand/GUI; MultiMC jar for the user's review.
9. Update README (mirror item, upgrade slots), bump `mod_version` 2.1.0, merge, tag `v2.1.0`.

### Status (2026-09-22)
Shipped as `v2.1.0` (merged to master, tagged, pushed). Design changed during modelling: the four **mirrors became lenses** (semi-transparent glass in an iron ring on a post, tilted 45 degrees so the glass dips over the mesh), and each lens casts a glowing beam plus sparkle particles onto the mesh while the table is lit. Slots map to fixed sides (slot 1 north, 2 east, 3 south, 4 west). The lens recipe is eight iron nuggets around a glass pane for one lens. The model, lenses and beams all live in the Blockbench project `tools/models/sun_drying_table.bbmodel`, driven over the Blockbench MCP plugin; `tools/models/split_table_model.py` splits the Java export into the per-part models composed by the multipart blockstate. Verified on the dedicated server over RCON (state follows slots, covering stops drying, drops on break, two lenses dry at 3x) and in game by the user. Old worlds keep their tables and items.

Resolved open points: +100% speed per lens (`lensSpeedBonus`), one lens per craft, `raisins` shows as soon as the first raisin lands.

## What's left (2026-09-22)

### Loader
- NeoForge for 26.3 is still beta only (`neo_version=26.3.0.8-beta`). Bump to the release build when it lands, rebuild, retest the client and the RCON suite.

### Art (Phase 3 retry)
- Trial a handful of textures with a different approach before any batch: a stronger model on the ComfyUI box (Flux 2 Klein, Z-image) or txt2img from a reference, at 16x16 or 32x32, and get an in-game verdict first. Pipeline and manifest are ready in `tools/art/`.
- Leaves need their own treatment: the AI runs produced blobs; either hand-drawn or the procedural base in `synth_leaves` with a better restyle.
- Hand-polish the programmatic 16x16 textures added in Phase 4: table wood, mesh, grape and raisin layers, lens, beam, lens item icon. They are placeholders drawn with PIL.
- Mod logo (`logoFile` in neoforge.mods.toml) was never made. The GUI sheet and the Factory Worker spawn egg were never touched.

### Sun Drying Table polish
- Ghost lens icon in the empty upgrade slots (skipped; slots are plain frames).
- Recipe-unlock advancement for the lens (the other recipes have none either; decide whether to add them all).
- Hoppers deliberately cannot reach the upgrade slots; revisit if automation of lenses is wanted.
- Beam and lens angles were tuned by eye in Blockbench; a top-down check of all four facings in game is still worth a look.

### Housekeeping
- Remote branches `neoforge-26.3`, `table-model` and `archive/port-1.8` are merged and can be deleted on GitHub.
- The MultiMC test instance still has the `JvmArgs` crash-log override I added while chasing the 0xC0000005 crash; harmless, remove if unwanted.
- Blockbench MCP plugin: the settings-persistence fix is patched locally (`%APPDATA%\Blockbench\mcp-local\mcp.js`) and in `C:\dev\blockbench-mcp-plugin` on `fix/persist-settings-on-unload`, pending the upstream PR. Until it is merged, a plugin update from the URL will bring the port bug back.
