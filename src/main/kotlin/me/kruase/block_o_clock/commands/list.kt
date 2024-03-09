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

    assert(args.size in 1..2)

    val sorting = ListSorting.valueOf(args[1].uppercase())
    val page = args.getOrNull(2)?.toInt() ?: 1

    val baseLocation =
        when (sender) {
            is Player -> sender.location
            else -> instance.server.worlds[0].spawnLocation
        }

    val (clockList, totalPages) = BOCClockManager.list(sorting, page, baseLocation)

    sender.sendMessage(
        "${
            userConfig.messages.info["clock-list-header"]
                ?.replace("{current}", page.toString())
                ?.replace("{total}", totalPages.toString())
        }\n${clockList.joinToString("\n")}\n${
            userConfig.messages.info["clock-list-footer"]
        }"
    )
}
