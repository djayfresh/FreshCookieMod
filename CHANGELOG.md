# Changelog

## 2.1.1 (2026-09-22) - Minecraft 26.3, NeoForge

- Mod logo in the mods list (a cookie, built from the item texture).
- First release published on GitHub releases and Planet Minecraft; changelog added. No gameplay changes.

## 2.1.0 (2026-09-22) - Minecraft 26.3, NeoForge

- The Sun Drying Table is now a real model: four legs, a frame, and a wire mesh top. Grapes and raisins show on the mesh as they dry.
- New **Lens** item (eight iron nuggets around a glass pane). The table has four upgrade slots, one per side; each lens hovers over its side, tilted toward the mesh.
- While the table is drying, every lens casts a glowing light beam onto the mesh with sparkle particles.
- Each lens adds another 100% drying speed (`lensSpeedBonus` in the config, so four lenses dry five times faster). Direct sunlight is still required.
- Lenses are locked to the upgrade slots; hoppers only touch the grape and raisin slots. Breaking the table drops everything in it.
- Tables and their contents from 2.0.0 worlds carry over.

## 2.0.0 (2026-09-22) - Minecraft 26.3, NeoForge

- Rewritten from scratch for NeoForge 26.3 (Java 25) with the mod id `freshcaa`.
- Five cookies (Chocolate Chip, Oatmeal Raisin, Peanut Butter, Pecan, White Macadamia), their doughs, and the ingredients, all with food properties.
- Peanut and grape crops with per-stage models, pecan and macadamia trees generated through JSON features and biome modifiers, logs, planks, saplings and nuts.
- Sun Drying Table rebuilt as a block entity with a menu and screen. It checks for real, direct daylight every tick, which fixes the old click-to-update bug.
- Factory Worker mob with its own model, renderer, spawn egg and chest-seeking behaviour.
- Recipes moved to JSON, achievements became a "Fresh Cookies" advancement tab, translations live in `en_us.json`.
- Creative tab and a common config (`config/freshcaa-common.toml`).
- The Forge 1.6.4 sources and the unfinished 1.8 port were archived under `legacy/`.

## 1.0 (2016-05) - Minecraft 1.6.4, Forge

- Tagged `v1.0-mc1.6.4`. Adds the Factory Worker mob (earmuffs, hard hat, glasses) with find-the-nearest-chest AI, macadamia and pecan planks, achievements, expanded config options, and the Package script for building the release zip.

## 0.9 (2016-05) - Minecraft 1.6.4, Forge

- Tagged `v0.9-mc1.6.4`. Big refactor of the main mod class, plants registered with the ore dictionary, the Sun Drying Table accepts grapes only, and dedicated-server fixes.

## 0.8 (2014-04) - Minecraft 1.6.4, Forge

- Tagged `v0.8-mc1.6.4`. The original release: five cookies, doughs, peanut and grape crops, pecan and macadamia trees, the Sun Drying Table, and the Cookies creative tab. Fixed the missing Oats texture and the Sun Drying Table GUI.
