package me.kruase.block_o_clock

import org.bukkit.command.TabExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.ChatColor
import me.kruase.block_o_clock.commands.*
import me.kruase.block_o_clock.util.hasPluginPermission


class BOCCommands : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        val fullArgs = args.dropLast(1)
        return when (fullArgs.getOrNull(0)) {
            null -> BlockOClock.userConfig.messages.help.keys
                .filter { sender.hasPluginPermission(it.replace("-", ".")) } - "header"
            "help" -> when {
                sender.hasPluginPermission("help") -> when (fullArgs.getOrNull(1)) {
                    null -> BlockOClock.userConfig.messages.help.keys
                        .filter { sender.hasPluginPermission(it.replace("-", ".")) } - "header"
                    else -> listOf()
                }
                else -> listOf()
            }
            else -> listOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try {
            when (args.getOrNull(0)) {
                null -> help(sender, arrayOf())
                "help" -> help(sender, args.drop(1).toTypedArray())
                "reload" -> {
                    if (!sender.hasPluginPermission("reload")) throw UnsupportedOperationException()
                    BlockOClock.userConfig = BlockOClock.instance.getUserConfig()
                }
            }
        } catch (e: UnsupportedOperationException) {
            sender.sendMessage(
                "${ChatColor.RED}${BlockOClock.userConfig.messages.error["no-permission"] ?: "Error: no-permission"}"
            )
        } catch (e: IllegalArgumentException) {
            sender.sendMessage(
                "${ChatColor.RED}${BlockOClock.userConfig.messages.error["invalid-command"] ?: "Error: invalid-command"}"
            )
        } catch (e: IllegalStateException) {
            // "Unknown error" should never happen
            sender.sendMessage("${ChatColor.RED}${e.message ?: "Unknown error"}")
        }

        return true
    }

    private fun playerOnly(sender: CommandSender, command: (Player, Array<out String>) -> Unit, args: Array<out String>) {
        when (sender) {
            is Player -> command(sender, args)
            else -> sender.sendMessage(
                "${ChatColor.RED}${BlockOClock.userConfig.messages.error["player-only"] ?: "Error: player-only"}"
            )
        }
    }
}
