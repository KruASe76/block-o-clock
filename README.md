# block-o-clock

A Spigot (Bukkit) Minecraft plugin that...


## Features [WIP]

- [ ] Some useful stuff
- [ ] Another useful stuff


## Usage

### Commands

`/blockoclock` is the main plugin command, which has the alias `/boc`.

| Command               | Description                                                   |
|-----------------------|---------------------------------------------------------------|
| `/boc help [command]` | Show help for given command, for available commands otherwise |
| `/boc reload`         | Reload config                                                 |


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

- [Legitimoose](https://www.youtube.com/c/Legitimoose) for amazing Paper (Bukkit) plugin (in Kotlin) project setup [tutorial](https://youtu.be/5DBJcz0ceaw)
- [BeBr0](https://www.youtube.com/c/BeBr0) for Spigot (Bukkit) plugin development [tutorial [RU]](https://youtube.com/playlist?list=PLlLq-eYkh0bB_uyZN4NdzkxLBs9glZmIT) with very clear API explanation


## Copyright

The project itself is distributed under [GNU GPLv3](./LICENSE).
