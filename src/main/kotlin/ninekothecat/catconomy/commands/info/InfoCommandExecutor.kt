package ninekothecat.catconomy.commands.info

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class InfoCommandExecutor : ICatEconomyCommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, commandLabel: String, args: Array<String>): Boolean {
        sender.sendMessage(
            ChatColor.RESET.toString() + "Catconomy version = " + ChatColor.GOLD + JavaPlugin.getProvidingPlugin(
                Catconomy::class.java
            ).description.version
        )
        sender.sendMessage(ChatColor.RESET.toString() + "Vault integration active = " + ChatColor.GOLD + Catconomy.vaultActive)
        sender.sendMessage(ChatColor.RESET.toString() + "Database active = " + ChatColor.GOLD + Catconomy.database!!.javaClass.simpleName)
        sender.sendMessage(ChatColor.RESET.toString() + "Prefix active = long: " + Catconomy.prefix!!.longPrefix + " short: " + Catconomy.prefix!!.shortPrefix)
        return true
    }
}