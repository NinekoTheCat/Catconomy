package ninekothecat.catconomy.commands.info

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class InfoCommandExecutor : ICatEconomyCommandExecutor {
    companion object {
        private val listOfMessages = buildList {
            val plugin = JavaPlugin.getProvidingPlugin(Catconomy::class.java)
            add("${ChatColor.RESET}Catconomy version = ${ChatColor.GOLD}${plugin.description.version}")
            add("${ChatColor.RESET}Vault integration active = ${ChatColor.GOLD}${Catconomy.vaultActive}")
            add("${ChatColor.RESET}Database active = ${ChatColor.GOLD}${Catconomy.database!!.javaClass.simpleName}")
            add("${ChatColor.RESET}Prefix active = long: ${Catconomy.prefix!!.longPrefix} short: ${Catconomy.prefix!!.shortPrefix}")
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, commandLabel: String, args: Array<String>): Boolean {
        listOfMessages.forEachIndexed { _, message -> sender.sendMessage(message) }
        return true
    }
}