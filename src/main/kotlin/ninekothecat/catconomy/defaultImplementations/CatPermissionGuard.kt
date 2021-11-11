package ninekothecat.catconomy.defaultImplementations

import ninekothecat.catconomy.interfaces.IPermissionGuard
import ninekothecat.catplugincore.money.enums.TransactionType
import ninekothecat.catplugincore.money.interfaces.ITransaction
import org.bukkit.Bukkit
import java.util.*

class CatPermissionGuard : IPermissionGuard {
    override fun isPermitted(transaction: ITransaction): Boolean {
        if (transaction.isConsole) {
            return true
        }
        val transactionInitiator = transaction.initiator
        return when (transaction.transactionType) {
            TransactionType.DELETE_USER -> canDeleteUser(transactionInitiator)
            TransactionType.CREATE_USER -> canCreateUser(transactionInitiator)
            TransactionType.TRANSFER_CURRENCY -> canTransferCurrency(
                transaction.usersInvolved!!.iterator().next()!!, transactionInitiator
            )
            TransactionType.SUBTRACT_CURRENCY -> canSubtractCurrency(
                transactionInitiator
            )
            TransactionType.GIVE_CURRENCY -> canGiveCurrency(transactionInitiator)
        }
    }

    private fun canTransferCurrency(fromUser: UUID, initiator: UUID?): Boolean {
        if (Bukkit.getServer().bannedPlayers.contains(Bukkit.getServer().getPlayer(fromUser))) {
            return false
        }
        return if (fromUser === initiator) {
            true
        } else {
            isPermitted(initiator, "catconomy.transfer")
        }
    }

    private fun isPermitted(initiator: UUID?, permissionName: String): Boolean {
        return Objects.requireNonNull(Bukkit.getServer().getPlayer(initiator)).hasPermission(permissionName)
    }

    private fun canSubtractCurrency(user: UUID?): Boolean {
        return isPermitted(user, "catconomy.subtract")
    }

    private fun canGiveCurrency(user: UUID?): Boolean {
        return isPermitted(user, "catconomy.give")
    }

    private fun canCreateUser(user: UUID?): Boolean {
        return isPermitted(user, "catconomy.create.user")
    }

    private fun canDeleteUser(user: UUID?): Boolean {
        return isPermitted(user, "catconomy.delete.user")
    }
}