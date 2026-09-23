# Phase 5: The Factory Worker — plan (2026-09-22)

Goal: turn the Factory Worker from a chest-sniffing wanderer into a hireable AI worker. The player hires
one, opens its task screen, picks a job, and keeps its supply chest stocked; the worker does the rest,
from hauling dough into furnaces to raising a whole factory building from a blueprint. Several workers
run side by side, each with its own job. Shipped in three releases so each step is playable on its own.

## Current state (what exists)

- `entity/FactoryWorker` is a `PathfinderMob` with the vanilla `VillagerModel` and the mod's 64x64 skin,
  villager sounds, never despawns. Goals: float, panic, `FindChestGoal` (walk to nearest chest/barrel in
  daylight), `WanderInSunGoal`, look at player, random look.
- Spawn egg, natural spawn in deserts/plains/rivers/forests, creative tab entry.
- Crafting chain to automate: egg+sugar+wheat -> 4 dough (crafting), dough + flavour -> flavoured dough
  (crafting), dough -> cookie (furnace smelting), grapes -> raisins (Sun Drying Table, sunlight only),
  wheat -> oats (crafting).
- Menu/screen pattern to copy: `menu/SunDryingTableMenu` + `client/SunDryingTableScreen` with a
  `ContainerData`, `addStandardInventorySlots`, 256x256 GUI sheet drawn with PIL.
- NeoForge 26.3.0.8: item transfer is the new API, `Capabilities.Item.BLOCK` gives a
  `ResourceHandler<ItemResource>` (sided), `ResourceHandlerUtil.move/moveFirst/insertStacking` inside a
  `Transaction`. `WorldlyContainerWrapper` wraps our block entity. Menus open on entities with
  `player.openMenu(provider, buf -> buf.writeVarInt(entityId))` and `IMenuTypeExtension.create`.

## Design decisions

- **Hiring.** Wild workers keep spawning as today and just wander. Right-click one while holding 8 or
  more cookies (new item tag `freshcaa:cookies`: all five plus vanilla cookie) to hire it: cookies are
  consumed, heart particles, `HIRED` flag set, persistence forced. Only hired workers open the task
  screen or accept a Clipboard. A "Dismiss" button in the screen hands its carried items back and makes
  it wild again. Config `hireCost` (default 8, 0 = free).
- **Work Post block** (`freshcaa:work_post`): a sign-post-sized block, planks over a fence, with facing.
  Its position is the worker's **home**: the centre of its work area (radius `workerWorkRange`, default
  16), where it idles between jobs, and the anchor + facing for the Builder task. Assign by handing the
  worker a Clipboard that has a post recorded, or by placing the post while sneaking within 4 blocks of a
  hired worker with no post. A worker with no post uses the spot where it was hired. One post can serve
  any number of workers.
- **Clipboard item** (`freshcaa:clipboard`): how the player points at blocks. Right-click a block to
  record its position (tooltip lists them in order with block names, max 8; sneak-right-click air to
  clear). Right-click a hired worker with it to hand the list over: the worker's screen opens with the
  recorded blocks filled into the task's binding slots in order (Supply, Machine, Output...). Bindings
  are by position, so a moved chest breaks the binding and the status line says so. Every binding row
  also has a "Nearest" button that scans the work area for the first block that fits (a container with
  a matching item, a furnace, a Sun Drying Table), so the Clipboard is optional for small setups.
- **Task model.** One assigned task per worker, stored on the entity: a `WorkerTask` record with a
  `TaskType` enum (`NONE, HAUL, TEND, MIX, BUILD`), up to four `BlockPos` bindings, an item filter, and
  per-type options (recipe id, blueprint id). Codec-serialised into the entity save data
  (`addAdditionalSaveData(ValueOutput)` / `readAdditionalSaveData(ValueInput)`). One goal,
  `DoAssignedTaskGoal` (priority 2, replaces `FindChestGoal` while hired), runs the task as a state
  machine: `PLAN -> WALK_TO(target) -> WORK(ticks, arm swing) -> TRANSFER -> PLAN`. Each `TaskType` has a
  `TaskRunner` that supplies the next step. Idle: `WanderInSunGoal` re-scoped to the post. Wild workers
  keep the old goals.
- **Item transfer** goes only through `Capabilities.Item.BLOCK`, so chests, barrels, furnaces (top =
  input, sides = fuel, bottom = output) and any modded inventory all work. The Sun Drying Table needs
  its capability registered in `RegisterCapabilitiesEvent` with `WorldlyContainerWrapper` (NeoForge only
  auto-registers vanilla block entities); its sided faces already hide the lens slots.
- **Carrying.** 4-slot carry inventory (`SimpleContainer`, saved with the entity, dropped on death), one
  filter-matching item type per trip, up to a full stack. Exposed as `Capabilities.Item.ENTITY`. Render:
  first carried stack floats in front of the chest, Allay-style, because the villager model's arms are
  crossed.
- **Reach.** Target reached within 2.5 blocks, no line of sight needed. Three failed paths in a row ->
  `BLOCKED` with block name and position in the status line, retry every 10 s. Navigation can open doors.
- **Status.** Synched `TaskStatus` (`IDLE, WORKING, WAITING_SUPPLY, OUTPUT_FULL, BLOCKED,
  MISSING_BINDING, NEEDS_MATERIALS`) plus a synched detail string, shown in the screen and as villager
  happy/angry particles when a job completes or blocks. `NEEDS_MATERIALS` carries the Builder shortfall
  ("12 Oak Planks, 3 Glass").
- **Schedule.** Hired workers work day and night. Config `workerRestsAtNight` (default false) makes them
  idle at the post from dusk to dawn.
- **Task screen** (`FactoryWorkerMenu` + `FactoryWorkerScreen`). Left: one button per `TaskType`, the
  current one highlighted. Right: the task's binding rows (bound block icon, name, position, "Nearest",
  "Clear"), a ghost filter slot (empty = anything the target accepts), and a recipe or blueprint picker
  where the task has one. Below: status line, the 4 carry slots, player inventory. Every button is a
  `clickMenuButton` id (task select, nearest/clear per binding, picker prev/next, dismiss), so no custom
  packets; the client reads state through a `ContainerData` (task type, status, picker index, four
  bound flags) and the synched detail string. Title = the worker's name.
- **Multiple workers** need no coordination: each loop re-checks the container before moving, and two
  workers on one chest simply share it.

## Task types

1. **Haul** (`HAUL`): Supply -> Output, optional filter. Per trip: extract up to a stack of the filter
   item (or the first extractable stack) from Supply, walk, insert into Output honouring sides (hauling
   into a furnace puts dough on top). Covers dough chest -> furnace, furnace -> storage, table ->
   chest, coal -> furnace, one task each.
2. **Tend** (`TEND`): keep one machine running from Supply into Output. Machine = furnace / smoker /
   blast furnace or Sun Drying Table (grapes in slot 0, raisins out of slot 1). Per trip: empty the
   output side into Output, top up the input side from Supply (filter or anything the machine accepts),
   and for furnaces keep at least 8 fuel in the fuel side from anything in Supply with a burn time
   (`FuelValues`). This is "bake my cookies" and "dry my grapes" in one.
3. **Mix** (`MIX`): craft a chosen recipe at a bound crafting table. Picker lists every crafting recipe
   whose result is tagged `freshcaa:doughs` or `freshcaa:cookies` (new tags), so it follows recipe
   changes. Per trip: confirm Supply holds a full ingredient set (`RecipeManager` match on a
   `CraftingInput` built from the supply contents), carry up to 16 sets, walk to the table, work 40
   ticks per craft with crafting sounds, put results in Output.
4. **Build** (`BUILD`): raise a blueprint at the Work Post. Blueprint = vanilla `StructureTemplate` under
   `data/freshcaa/structure/blueprints/*.nbt`, listed by an index `data/freshcaa/blueprints/*.json`
   (template id, name, icon item, size) loaded with a `SimpleJsonResourceReloadListener`. Anchor = the
   post, rotated to its facing so the post stands at the entrance. Expand the template once into an
   ordered block list (cached on the entity, progress index saved): bottom layer first, then by distance
   from the post. Skip positions already holding the right block; only replace air/replaceable blocks,
   never the player's own. Item per block via `Block.asItem()` with a small table for two-part blocks
   (doors and beds place from the lower/foot half, upper half skipped; fluids and item-less blocks
   skipped and reported). Take up to a stack of the next needed item from Supply, walk within 4 blocks,
   place one block per `builderTicksPerBlock` (default 10) with arm swing and place sound. Bottom-up
   order lets the worker stand on its own work; doors go last per wall so it never seals itself out;
   leftovers unreachable after a pass are retried, then reported. Missing materials -> `NEEDS_MATERIALS`
   with totals. Done -> `IDLE`, happy particles; the task stays selected so it repairs damage.
   Shipped blueprints (hand-built in game, exported with a structure block): **Bakery** (7x5x7: four
   furnaces, supply + output chests, crafting table), **Drying Yard** (9x1x9: six Sun Drying Tables on an
   open deck), **Warehouse** (9x5x9: barrels and double chests), **Cookie Factory** (15x7x15: all three
   under one roof, pecan planks, macadamia trim, chimney).
   Custom blueprints (5c): Clipboard records two corners; sneak-right-click a Work Post with it to save
   the region as `freshcaa:blueprints/custom/<name>` through the level's `StructureTemplateManager`
   (name typed in a text box in the screen). Lives in the world save's `generated` folder and shows in
   the picker like a shipped one.

## New content

| Kind | Id | Notes |
|---|---|---|
| Block | `work_post` | planks over fence, facing; home + build anchor |
| Item | `clipboard` | records up to 8 positions, hands them to a worker |
| Item tags | `freshcaa:cookies`, `freshcaa:doughs` | hire cost, Mix recipe filter |
| Menu | `factory_worker` | task screen |
| GUI sheet | `textures/gui/factory_worker.png` | 256x256, PIL-drawn like the table sheet |
| Structures | `structure/blueprints/{bakery,drying_yard,warehouse,cookie_factory}.nbt` | plus index JSON |
| Config | `hireCost`, `workerWorkRange`, `workerRestsAtNight`, `builderTicksPerBlock`, `workerWorkTicks` | |
| Lang | task names, statuses, tooltips, blueprint names | |

Textures start as 16x16 PIL placeholders (work post, clipboard; blueprint icons reuse existing items),
polished later with the Phase 3 backlog.

## Steps

**5a — Foundation + Haul (release 2.3.0, branch `worker-tasks`)**
1. `FactoryWorker`: `HIRED` synched flag, home pos, carry container, `WorkerTask` with codec save/load,
   `TaskStatus` + detail synched data, `mobInteract` for hire / Clipboard / open screen, drop carry on
   death, door-opening navigation.
2. `WorkerTask`, `TaskType`, `TaskStatus`, `Binding` records + codecs; `DoAssignedTaskGoal` state machine;
   `TaskRunner` interface with `HaulRunner`; `WorkerTransfers` helper over `ResourceHandlerUtil`.
3. Work Post block + item, Clipboard item, `freshcaa:cookies` tag, hire flow.
4. `FactoryWorkerMenu` / `FactoryWorkerScreen`, button ids, `ContainerData`, GUI sheet.
5. Register the Sun Drying Table item capability.
6. Carried-item render layer on `FactoryWorkerRenderer`.
7. Verify. RCON: summon, `/data modify` to set hired + a Haul task between two chests, count items moved
   per minute, remove the target chest -> `BLOCKED`, restore -> resumes, world reload keeps the task.
   Client: hire with cookies, Clipboard flow, every screen button, carried item visible.

**5b — Tend + Mix (release 2.4.0)**
8. `TendRunner` for the furnace family and the Sun Drying Table (fuel top-up with `FuelValues`).
9. `MixRunner`, recipe picker, `freshcaa:doughs` tag, crafting-table binding.
10. Verify: furnace stays lit and cookies land in Output over a full day; grapes -> raisins loop with
    lenses; Mix crafts oatmeal raisin dough from a mixed chest and stops cleanly when raisins run out.

**5c — Build (release 2.5.0)**
11. Blueprint index loader, four shipped structures, `BuildRunner` with cached block list, placement
    rules, shortfall report.
12. Custom blueprints from Clipboard corners; name box in the screen.
13. Verify: build each blueprint on flat ground and on a slope from a stocked chest; starve the chest
    mid-build and check the shortfall list; break a wall after completion and check the repair; custom
    blueprint round trip.

**Later, out of scope**: Farmer (harvest/replant grapes and peanuts), Forester (saplings), sleeping in
beds, a worker model with the 1.6.4 hard hat, earmuffs and glasses, worker levelling, wages.

## Risks and checks
- NeoForge 26.3 is beta; `ResourceHandler`/`Transaction` names were checked against the 26.3.0.8 sources
  jar, re-check once more when 5a starts.
- The villager model cannot hold items; if the floating stack looks wrong, fall back to a head-tilt
  "carrying" pose and show the stack only in the screen.
- Rotated placement: use `StructureTemplate` with `StructurePlaceSettings` rotation rather than hand
  rotation so stairs, doors and logs come out right.
- Enclosed interiors: verify on the Cookie Factory that bottom-up order plus doors-last keeps the
  worker from sealing itself out.
