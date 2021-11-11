package ninekothecat.catconomy.commands.take

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.defaultImplementations.CatTransaction
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor
import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.enums.TransactionType
import ninekothecat.catplugincore.utils.player.getPlayerFromName
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.text.MessageFormat
import java.util.*

class TakeCommandExecutor : ICatEconomyCommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            return false
        }
        if (args.size == 2) {
            val usersInvolved = ArrayList(
                setOf(
                    Objects.requireNonNull(
                        getPlayerFromName(
                            args[0]
                        )
                    )!!.uniqueId
                )
            )
            val amount: Double = try {
                args[1].toDouble()
            } catch (exception: NumberFormatException) {
                sender.sendMessage("${ChatColor.RED}Not a valid Number")
                return false
            }
            if (java.lang.Double.isNaN(amount)) {
                return false
            }
            if (amount <= 0) {
                return false
            }
            val strings: MutableSet<String> = HashSet()
            for (user in usersInvolved) {
                strings.add(Bukkit.getPlayer(user).displayName)
            }
            val transaction = CatTransaction(
                TransactionType.SUBTRACT_CURRENCY,
                false,
                amount,
                sender.uniqueId,
                usersInvolved,
                "${sender.displayName} Removed $amount from ${strings.toTypedArray().contentToString()} " +
                        "(${usersInvolved.toTypedArray().contentToString()})", JavaPlugin.getProvidingPlugin(
                    Catconomy::class.java
                )
            )
            when (Catconomy.getBalanceHandler()!!.doTransaction(transaction)) {
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
                    sender.sendMessage("${ChatColor.GREEN}taken $amount from ${args[0]}")
                    return true
                }
            }
        }
        return false
    }
}