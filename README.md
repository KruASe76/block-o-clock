# kotlin-plugin-template

A Spigot (Bukkit) Minecraft plugin that...


## Features [WIP]

- [ ] Some useful stuff
- [ ] Another useful stuff


## Usage

### Commands

`/template` is the main plugin command, which has the alias `/tmp`.

| Command               | Description                                                   |
|-----------------------|---------------------------------------------------------------|
| `/tmp help [command]` | Show help for given command, for available commands otherwise |
| `/tmp reload`         | Reload config                                                 |


## Configuration ([default](/src/main/resources/config.yml))

- Plugin messages [WIP]
  - info
  - error
  - help


## Permissions

| Permission node   | Default | Description                                               |
|-------------------|---------|-----------------------------------------------------------|
| `template.help`   | true    | Allows to use `/tmp help` (lists only available commands) |
| `template.reload` | op      | Allows to use `/tmp reload`                               |
| `template.admin`  | op      | Refers to `template.reload` by default                    |


## Special thanks to:

- [Legitimoose](https://www.youtube.com/c/Legitimoose) for amazing Paper (Bukkit) plugin (in Kotlin) project setup [tutorial](https://youtu.be/5DBJcz0ceaw)
- [BeBr0](https://www.youtube.com/c/BeBr0) for Spigot (Bukkit) plugin development [tutorial [RU]](https://youtube.com/playlist?list=PLlLq-eYkh0bB_uyZN4NdzkxLBs9glZmIT) with very clear API explanation


## Copyright

The project itself is distributed under [GNU GPLv3](./LICENSE).
