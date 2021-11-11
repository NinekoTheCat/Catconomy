package ninekothecat.catconomy.interfaces

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

interface ICatEconomyCommandAutoComplete {
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>?): List<String>?
}