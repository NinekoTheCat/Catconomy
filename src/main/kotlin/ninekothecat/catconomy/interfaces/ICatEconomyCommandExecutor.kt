package ninekothecat.catconomy.interfaces

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

interface ICatEconomyCommandExecutor {
    fun onCommand(sender: CommandSender, cmd: Command, commandLabel: String, args: Array<String>): Boolean
}