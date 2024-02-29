# block-o-clock

![](https://img.shields.io/badge/MINECRAFT-1.20-966C4A?style=for-the-badge&labelColor=53AC56)
![](https://img.shields.io/badge/JAVA-17-5283A2?style=for-the-badge&labelColor=E86F00)

A Spigot (Bukkit) Minecraft plugin that allows to create clocks of blocks in the world


## Features [WIP]

- [ ] Clock creation with lots of settings
  - location in the world
  - time format
  - time zone / non-synced (acts like a timer or a stopwatch)
  - foreground and background block types
  - font and its size
- [ ] Clock list display
  - in order
  - sorted by distance
- [ ] Clock deletion
- [ ] Clock starting and stopping (freezing)
- [ ] Non-synced clock settings
  - time
  - direction
- [ ] Clock info serialization into custom config to keep running after server restart
- [ ] Handy tab-completion


## Usage

### Commands

`/blockoclock` is the main plugin command, which has an alias `/boc`.

| Command                                 | Description                                                                                            |
|-----------------------------------------|--------------------------------------------------------------------------------------------------------|
| `/boc help [command]`                   | Show help for given command, for available commands otherwise                                          |
| `/boc reload`                           | Reload config                                                                                          |
| `/boc create <*settings>`               | Create new clock in the world (and print its ID)                                                       |
| `/boc list <nearest\|ordered> [page=1]` | List all clocks in the world (with IDs) with given sorting                                             |
| `/boc delete <id>`                      | Delete the clock (and fills its blocks with air)                                                       |
| `/boc start <id>`                       | Start stopped clock                                                                                    |
| `/boc stop <id>`                        | Stop the clock (it won't update until `/boc start`)                                                    |
| `/boc set time <id> <hh:mm:ss.tt>`      | Set given time on **non-synced** clock (if the time unit is not displayed its value can be just zeros) |
| `/boc set direction <id> <up\|down>`    | Set given direction on **non-synced** clock                                                            |

### `/boc create` syntax

> `/boc create <dim> <xyz> ±<x|y|z> ±<x|y|z> <hour|min|sec|tick> <hour|min|sec|tick> <±hh[:mm]|none> <block|none> <block|none> <font> [size=3]`

- `<dim>` - minecraft dimension: `overworld`, `nether`, `end`
- `<xyz>` - coordinates of top left corner of the display area
- `±<x|y|z>` _(2 times)_ - directions of the horizontal and vertical axes respectively (example: `-x +z`)
- `<hour|min|sec|tick>` _(2 times)_ - largest and smallest time units to display (example: `min tick` will show `mm:ss.tt`)
- `<±hh[:mm]|none>` - timezone to sync with (examples: `+03`, `-10`, `+06:45`) or `none` for manual control
- `<block|none>` _(2 times)_ - foreground and background block IDs respectively (`none` will not fill, while `air` will fill with air)
- `<font>` - font type: `minecraft`, `digital` (seven-segment-like)
- `[size=3]` - `digital` font size, default is `3` (ignored if `minecraft` font is specified)


## Configuration ([default](/src/main/resources/config.yml))

- `/boc list` page size
- Plugin messages [WIP]
  - info
  - error
  - help


## Permissions

| Permission node      | Default | Description                                               |
|----------------------|---------|-----------------------------------------------------------|
| `blockoclock.help`   | true    | Allows to use `/boc help` (lists only available commands) |
| `blockoclock.reload` | op      | Allows to use `/boc reload`                               |
| `blockoclock.admin`  | op      | Refers to `blockoclock.reload` by default                 |


## Special thanks to:

- [Legitimoose](https://youtube.com/c/Legitimoose) for amazing Paper (Bukkit) plugin (in Kotlin) project setup [tutorial](https://youtu.be/5DBJcz0ceaw)
- [BeBr0](https://youtube.com/c/BeBr0) for Spigot (Bukkit) plugin development [tutorial [RU]](https://youtube.com/playlist?list=PLlLq-eYkh0bB_uyZN4NdzkxLBs9glZmIT) with very clear API explanation


## Copyright

The project itself is distributed under [GNU GPLv3](./LICENSE).
