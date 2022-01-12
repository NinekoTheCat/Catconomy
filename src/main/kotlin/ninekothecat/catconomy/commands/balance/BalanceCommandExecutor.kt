package ninekothecat.catconomy.commands.balance

import ninekothecat.catconomy.Catconomy
import ninekothecat.catplugincore.utils.player.getPlayerFromName
import ninekothecat.catplugincore.utils.sendError
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BalanceCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val shortPrefix: String = Catconomy.prefix!!.shortPrefix
        if (!sender.hasPermission("catconomy.balance")) {
            sender.sendMessage("${ChatColor.RED}You are not authorised to do that")
            return false
        }
        return when (args.size) {
            0 -> if (sender is Player){
                    getSelfMoneyAmount(sender, shortPrefix)
                    true
                }else {
                    sendError("You can't check the balance of yourself because you aren't a player", sender)
                    false
                }
            1 -> {
                getOtherPlayersMoneyAmount(sender, args, shortPrefix)
                true
            }
            else -> false
        }
    }

    private fun getSelfMoneyAmount(sender: CommandSender, shortPrefix: String) {
        if (sender is Player) {
            val moneyAmount =
                Catconomy.getBalanceHandler()!!.getBalance(sender.uniqueId).toString()
            sender.sendMessage("${ChatColor.YELLOW}Your balance is ${ChatColor.GOLD}${moneyAmount} ${ChatColor.AQUA}${shortPrefix}")
        }else {
            sendError("You can't check your own balance, if you aren't a player",sender)
        }
    }

    private fun getOtherPlayersMoneyAmount(sender: CommandSender, args: Array<String>, shortPrefix: String) {
        val player = getPlayerFromName(args[0])
        if (player != null && Catconomy.getBalanceHandler()!!.userExists(player.uniqueId)) {
            val moneyAmount =
                Catconomy.getBalanceHandler()!!.getBalance(player.uniqueId).toString()
            sender.sendMessage("${ChatColor.YELLOW}${player.displayName}'s balance is : ${ChatColor.GOLD}${moneyAmount} ${ChatColor.AQUA}$shortPrefix")
        } else {
            sender.sendMessage("${ChatColor.GOLD}${args[0]} Does not exist")
        }
    }
}