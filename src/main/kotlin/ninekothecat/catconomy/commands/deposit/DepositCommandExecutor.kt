package ninekothecat.catconomy.commands.deposit

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.defaultImplementations.CatTransaction
import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.enums.TransactionType
import ninekothecat.catplugincore.utils.player.getPlayerFromName
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.text.MessageFormat
import java.util.*

class DepositCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size == 2) {
            val player = getPlayerFromName(args[0])
            if (player != null && sender is Player) {
                val amount: Double = try {
                    args[1].toDouble()
                } catch (exception: NumberFormatException) {
                    sender.sendMessage("${ChatColor.RED}Not a valid Number")
                    return false
                }
                if (java.lang.Double.isNaN(amount)) {
                    return false
                }
                if (amount > 0) {
                    val involved = ArrayList(
                        listOf(
                            Objects.requireNonNull(sender.player).uniqueId,
                            player.uniqueId
                        )
                    )
                    val catTransaction = CatTransaction(
                        TransactionType.TRANSFER_CURRENCY,
                        false,
                        amount,
                        sender.uniqueId,
                        involved,
                        "Transferred $amount from ${sender.displayName} to ${player.displayName}"
                        , JavaPlugin.getProvidingPlugin(Catconomy::class.java)
                    )
                    when (Catconomy.getBalanceHandler()!!.doTransaction(catTransaction)) {
                        TransactionResult.LACK_OF_PERMS -> {
                            sender.sendMessage("${ChatColor.DARK_RED}YOU ARE NOT AUTHORISED TO DO THIS")
                            return false
                        }
                        TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY -> {
                            sender.sendMessage("${ChatColor.DARK_RED}FAILED TRANSACTION DUE TO INSUFFICIENT AMOUNT OF CURRENCY")
                            return false
                        }
                        TransactionResult.USER_DOES_NOT_EXIST -> {
                            sender.sendMessage("${ChatColor.DARK_RED}FAILED TRANSACTION DUE TO THE USER NOT EXISTING")
                            return false
                        }
                        TransactionResult.USER_ALREADY_EXISTS -> {
                            sender.sendMessage("${ChatColor.DARK_RED}FAILED TRANSACTION DUE TO USER ALREADY EXISTING")
                            return false
                        }
                        TransactionResult.ILLEGAL_TRANSACTION -> {
                            sender.sendMessage("${ChatColor.DARK_RED}FAILED TRANSACTION DUE TO THE TRANSACTION BEING ILLEGAL")
                            return false
                        }
                        TransactionResult.INTERNAL_ERROR -> {
                            sender.sendMessage("${ChatColor.DARK_RED}FAILED TRANSACTION DUE TO INTERNAL ERROR")
                            return false
                        }
                        TransactionResult.SUCCESS -> {
                            sender.sendMessage("${ChatColor.GREEN} Transferred $amount to ${player.displayName}")
                            return true
                        }
                    }
                }
            } else {
                return false
            }
            return true
        }
        return false
    }
}