# Fresh Cookies (FreshCAA)

A Minecraft mod for **NeoForge on Minecraft 26.3**. Five new cookies with their doughs and ingredients,
a Sun Drying Table that turns grapes into raisins using nothing but sunlight, peanut and grape crops,
pecan and macadamia trees, and the Factory Worker. Cookies Are Amazing.

The original 2014 version targeted Minecraft 1.6.4 / Forge 9.11. That code is kept under `legacy/`
and tagged `v1.0-mc1.6.4`.

## Playing

1. Install [NeoForge](https://neoforged.net/) for Minecraft 26.3.
2. Drop `freshcaa-<version>.jar` into your `mods` folder.
3. Launch with the NeoForge profile.

Config lives at `config/freshcaa-common.toml` (or via Mods > Fresh Cookies > Config).

## Content

**Cookies**: Chocolate Chip, Oatmeal Raisin, Peanut Butter, Pecan, White Macadamia.
Smelt the matching dough in a furnace. Plain cookie dough smelts into a vanilla cookie.

**Dough**: Egg + Sugar + Wheat gives 4 Cookie Dough. Combine dough with cocoa beans, raisin + oats,
peanuts, pecan, or white macadamia for the flavoured doughs. Raw dough is edible, but may poison you.

**Sun Drying Table**: place grapes in it under open daytime sky and they dry into raisins.
It needs no fuel, only direct sunlight. Rain, night, or a roof stop it.

    Stick | Gold    | Stick
    Gold  | Diamond | Gold
    Stick | Gold    | Stick

**Crops**: Peanuts and Grapes plant on farmland like wheat seeds. Oats come from wheat.

**Trees**: Pecan and Macadamia trees generate in forests, plains and savannas. Leaves drop saplings
and nuts. Logs craft into planks and smelt into charcoal.

**Factory Worker**: spawns in deserts, plains, rivers and forests. Wanders in the sun and
heads for the nearest chest during the day.

**Advancements**: a "Fresh Cookies" tab with seven advancements from Getting Started to Macadamia Dance.

## Building

Requires JDK 25 on the path to run Gradle (the toolchain downloads it if missing).

    ./gradlew build          # jar lands in build/libs
    ./gradlew runClient      # dev client
    ./gradlew runServer      # dev server

## History

| Tag | Minecraft | Notes |
|---|---|---|
| `v0.8-mc1.6.4` | 1.6.4 | 2014 initial import |
| `v0.9-mc1.6.4` | 1.6.4 | Sun Drying Table fixes, refactor |
| `v1.0-mc1.6.4` | 1.6.4 | Final legacy release with the Factory Worker |
| `wip-mc1.8` | 1.8 | Unfinished 2015 port, `archive/port-1.8` branch |
| `v2.0.0` | 26.3 | NeoForge rewrite |
