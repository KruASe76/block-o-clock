package me.kruase.block_o_clock

import me.kruase.block_o_clock.BlockOClock.Static.instance
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.commands.*
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor


class BOCCommands : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): List<String> {
        val fullArgs = args.dropLast(1)
        return when (fullArgs.getOrNull(0)) {
            null -> userConfig.messages.help.keys
                .filter { sender.hasPluginPermission(it.replace("-", ".")) } - "header"
            "help" -> when {
                sender.hasPluginPermission("help") -> when (fullArgs.getOrNull(1)) {
                    null -> userConfig.messages.help.keys
                        .filter { sender.hasPluginPermission(it.replace("-", ".")) } - "header"
                    else -> emptyList()
                }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try {
            when (args.getOrNull(0)) {
                null -> help(sender, emptyList())
                "help" -> help(sender, args.drop(1))
                "list" -> list(sender, args.drop(1))
                "create" -> create(sender, args.drop(1))
                "delete" -> delete(sender, args.drop(1))
                "start" -> start(sender, args.drop(1))
                "stop" -> stop(sender, args.drop(1))
                "set" -> set(sender, args.drop(1))
                "reload" -> {
                    if (!sender.hasPluginPermission("reload")) throw UnsupportedOperationException()
                    userConfig = instance.getUserConfig()
                }
                else -> throw IllegalArgumentException()
            }
        } catch (e: UnsupportedOperationException) {
            sender.sendMessage(
                "${ChatColor.RED}${userConfig.messages.error["no-permission"] ?: "Error: no-permission"}"
            )
        } catch (e: IllegalArgumentException) {
            sender.sendMessage(
                "${ChatColor.RED}${userConfig.messages.error["invalid-command"] ?: "Error: invalid-command"}"
            )
        } catch (e: IllegalStateException) {
            // "Unknown error" should never happen
            sender.sendMessage("${ChatColor.RED}${e.message ?: "Unknown error"}")
        }

        return true
    }
}
