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

## Phase 1: Tag history
1. `v0.8-mc1.6.4` at 8af3c8b (2014 initial commit, mcmod.info v0.8)
2. `v0.9-mc1.6.4` at 59e55a2 ("Version 0.9")
3. `v1.0-mc1.6.4` at d133a14 (final 1.6.4 with Factory Worker mob)
4. Import Dropbox `1.8/src/main` (minus pirates + build output) on branch `archive/port-1.8`, tag `wip-mc1.8`
5. Push tags; Dropbox folder becomes cold backup

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

## Phase 3: Art upgrade via ComfyUI (external server)
1. Connect to server, confirm models/custom nodes
2. Target resolution per asset class: blocks/items 32x32 (or keep 16x16), GUI + mob skin stay native size
3. Reusable workflow: img2img low denoise, pixel-art model, tile ControlNet; post: palette quantize, preserve alpha, enforce tiling on logs/leaves
4. Special cases: mob skin per UV region; GUI regenerate background only, slots untouched
5. Contact sheet old vs new, in-game review, iterate; generate real mod logo
6. Commit textures, originals under `legacy/`, tag `v2.1.0`
