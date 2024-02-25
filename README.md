# block-o-clock

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

`/blockoclock` is the main plugin command, which has the alias `/boc`.

| Command               | Description                                                   |
|-----------------------|---------------------------------------------------------------|
| `/boc help [command]` | Show help for given command, for available commands otherwise |
| `/boc reload`         | Reload config                                                 |

### `/boc create` syntax

> `/boc create <xyz> <+|-><x|y|z> <+|-><x|y|z> <hour|min|sec|tick> <hour|min|sec|tick> <(timezone)|none> <block> <block> <digital|minecraft> [size=3]`

- `<xyz>` - coordinates of top left corner of the display area
- `<+|-><x|y|z>` _(2 times)_ - directions of the horizontal and vertical axes respectively (example: `-x +z`)
- `<hour|min|sec|tick>` _(2 times)_ - largest and smallest time units to display (example: `min tick` will show `MM:SS.TT`)
- `<(timezone)|none>` - timezone to sync with (examples: `-5`, `+3`, `+10:45`) or `none` for manual control
- `<block>` _(2 times)_ - foreground and background block IDs respectively
- `<digital|minecraft>` - font type: `digital` stands for usual seven-segment-like font
- `[size=3]` - `digital` font size, default is `3` (`minecraft` font is not sizeable)


## Configuration ([default](/src/main/resources/config.yml))

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
