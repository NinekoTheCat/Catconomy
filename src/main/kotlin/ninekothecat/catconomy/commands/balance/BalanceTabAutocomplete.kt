package ninekothecat.catconomy.commands.balance

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.stream.Collectors

class BalanceTabAutocomplete : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        return if (args.isEmpty()) Bukkit.getServer().onlinePlayers.stream().map { obj: Player -> obj.displayName }
            .collect(Collectors.toList()) else null
    }
}