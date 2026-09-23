# Changelog

## 2.3.0 (unreleased) - Minecraft 26.3, NeoForge

- **Factory Workers can be hired.** Right-click a wild worker while holding 8 cookies of any kind (`hireCost` in the config, item tag `freshcaa:cookies`). A hired worker never wanders off, opens a task screen on right-click, and carries up to four stacks in its arms.
- **Task screen**: pick a job on the left (Haul is available now; Tend, Mix and Build are greyed out until later updates), bind the blocks it works between on the right with "N" (nearest container in the work area) or "X" (clear), put a sample item in the Filter slot to restrict what it moves, and Dismiss it to get everything back.
- **Haul**: moves items from the Supply block to the Output block, one stack per trip. Machines are loaded through their top like a hopper does, fuel goes in through the side, so "dough to furnace", "coal to furnace", "grapes to Sun Drying Table" and "raisins to chest" are each one Haul job. The status line explains what it is doing or why it is waiting.
- **Work Post** block (three planks over two sticks): a worker's home. Placing one near hired workers without a home gives them this one; they idle around it between jobs.
- **Clipboard** item (iron nugget, paper, planks): right-click containers or a Work Post to record them, then right-click a hired worker to fill its bindings in order and open its screen. Sneak-right-click the air to clear it.
- Hoppers and other mods can now reach the Sun Drying Table's grape and raisin slots through the item capability, and a worker's carry inventory through the entity capability.
- New config: `hireCost`, `workerWorkRange`, `workerWorkTicks`, `workerRestsAtNight`.

## 2.2.0 (2026-09-22) - Minecraft 26.3, NeoForge

- Grapes and peanuts can be found again: breaking grass or ferns that would drop wheat seeds now picks wheat seeds, peanuts or grapes by weight, as the 1.6.4 version did. Three config weights (`wheatSeedDropWeight`, `peanutDropWeight`, `grapeDropWeight`, default 10 each; 0 disables one). Implemented as a global loot modifier, so it works with datapacks that change grass loot.

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
