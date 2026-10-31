# Challenge Timer

A count-up run timer for Minecraft challenge runs, shown above the hotbar. Client-side Fabric mod for singleplayer.

**Download:** [Modrinth](https://modrinth.com/mod/challenge-timer-by-opniki)

![Challenge Timer in game](https://cdn.modrinth.com/data/19VBJVsr/c3103d5f8c2f3505b5ac32029c63a6b8766bedf1.png)

## Features

- Timer above the hotbar or at the top of the screen
- Format grows with the run: `55s` → `12m 30s` → `22h 29m 55s` → `4d 22h 29m 55s`
- Runs in real time and pauses automatically in the pause menu
- Any colour via hex value, background on or off
- Saved per world, survives restarts

## Commands

| Command | Effect |
| --- | --- |
| `/timer` | Show the current time |
| `/timer start` / `pause` / `resume` / `reset` | Control the timer |
| `/timer set <seconds>` | Set a specific time |
| `/timer color <hex>` | Change the colour, e.g. `FF55FF` |
| `/timer bg on` / `off` | Toggle the background |
| `/timer pos top` / `bottom` | Switch position |
| `/timer hide` / `show` | Hide or show the display |

## Project structure

Each supported Minecraft version has its own folder:

```
26.2/   Fabric project for Minecraft 26.2
26.3/   Fabric project for Minecraft 26.3
```

Main classes in `src/main/java/de/niklas/challengetimer/`:

- `TimerState` keeps the time, handles pausing and saves it per world
- `TimerHud` draws the timer on screen
- `TimerCommands` registers all `/timer` commands

## Build

Requires the JDK version used by the target Minecraft version.

```
cd 26.3
./gradlew build
```

The finished `.jar` is in `build/libs/`.

## Author

Made by [OPNiki](https://github.com/OPNiki) · [Portfolio](https://opniki.github.io)
