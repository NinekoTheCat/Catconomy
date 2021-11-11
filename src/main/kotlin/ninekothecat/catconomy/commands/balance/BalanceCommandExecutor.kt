package ninekothecat.catconomy.commands.balance

import ninekothecat.catconomy.Catconomy
import ninekothecat.catplugincore.utils.player.getPlayerFromName
import org.bukkit.ChatColor
import org.bukkit.command.*
import org.bukkit.entity.Player
import java.text.MessageFormat

class BalanceCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val shortPrefix: String = Catconomy.prefix!!.shortPrefix
        if (!sender.hasPermission("catconomy.balance")) {
            sender.sendMessage(ChatColor.RED.toString() + "You are not authorised to do that")
            return false
        }
        return when (args.size) {
            0 -> {
                getSelfMoneyAmount(sender, shortPrefix)
                true
            }
            1 -> {
                getOtherPlayersMoneyAmount(sender, args, shortPrefix)
                true
            }
            else -> false
        }
    }

    private fun getSelfMoneyAmount(sender: CommandSender, shortPrefix: String) {
        val player = sender as Player
        val moneyAmount =
            Catconomy.getBalanceHandler()!!.getBalance(player.uniqueId).toString()
        sender.sendMessage(
            MessageFormat.format(
                "{0}Your balance is {1} {2}",
                ChatColor.YELLOW, ChatColor.GOLD.toString() + moneyAmount, ChatColor.AQUA.toString() + shortPrefix
            )
        )
    }

    private fun getOtherPlayersMoneyAmount(sender: CommandSender, args: Array<String>, shortPrefix: String) {
        val player = getPlayerFromName(args[0])
        if (player != null && Catconomy.getBalanceHandler()!!.userExists(player.uniqueId)) {
            val moneyAmount =
                Catconomy.getBalanceHandler()!!.getBalance(player.uniqueId).toString()
            sender.sendMessage(
                MessageFormat.format(
                    "{0}{1}s balance is : {2}{3} {4}{5}",
                    ChatColor.YELLOW,
                    player.displayName,
                    ChatColor.GOLD,
                    moneyAmount,
                    ChatColor.AQUA,
                    shortPrefix
                )
            )
        } else {
            sender.sendMessage(MessageFormat.format("{0} Does not exist", ChatColor.GOLD.toString() + args[0]))
        }
    }
}