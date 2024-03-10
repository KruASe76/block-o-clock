package me.kruase.block_o_clock.commands

import me.kruase.block_o_clock.BOCClockManager
import me.kruase.block_o_clock.BlockOClock.Static.instance
import me.kruase.block_o_clock.BlockOClock.Static.userConfig
import me.kruase.block_o_clock.ListSorting
import me.kruase.block_o_clock.hasPluginPermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


fun list(sender: CommandSender, args: List<String>) {
    if (!sender.hasPluginPermission("list")) throw UnsupportedOperationException()

    require(args.size <= 2)

    val sorting = args.getOrNull(0)?.uppercase()?.let { ListSorting.valueOf(it) } ?: ListSorting.ORDERED
    val page = args.getOrNull(1)?.toInt() ?: 1

    val baseLocation =
        when (sender) {
            is Player -> sender.location
            else -> instance.server.worlds[0].spawnLocation
        }

    BOCClockManager.listClocks(sorting, page, baseLocation)
        .let { (clockList, totalPages) ->
            listOf(
                userConfig.messages.info["clock-list-header"]
                    ?.replace("{current}", page.toString())
                    ?.replace("{total}", totalPages.toString()),
                clockList.joinToString("\n"),
                userConfig.messages.info["clock-list-footer"]
            )
                .filter { !it.isNullOrEmpty() }
                .let { strings ->
                    if (strings.isNotEmpty())
                        sender.sendMessage(strings.joinToString("\n"))
                }
        }
}
