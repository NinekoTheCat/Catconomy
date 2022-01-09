package ninekothecat.catconomy.defaultImplementations

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ninekothecat.catconomy.Catconomy
import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.enums.TransactionType
import ninekothecat.catplugincore.money.interfaces.IBalanceHandler
import ninekothecat.catplugincore.money.interfaces.ITransaction
import java.util.*

class CatBalanceHandler(private val doLogs: Boolean) : IBalanceHandler {
    override fun doTransaction(transaction: ITransaction): TransactionResult {
        return try {
            if (Catconomy.permissionGuard!!.isPermitted(transaction)) {
                val resultFuture = runBlocking(Dispatchers.IO) { getTransactionResult(transaction) }
                logTransaction(transaction, resultFuture)
                resultFuture
            } else {
                Catconomy.iCatLogger!!.fail(transaction, TransactionResult.LACK_OF_PERMS)
                TransactionResult.LACK_OF_PERMS
            }
        } catch (e: Exception) {
            Catconomy.iCatLogger!!.error(transaction, TransactionResult.INTERNAL_ERROR, e)
            TransactionResult.INTERNAL_ERROR
        }
    }

    override fun userExists(user: UUID): Boolean {
        return userExistsStatic(user)
    }

    override fun getBalance(user: UUID): Double {
        return getBalanceStatic(user)
    }

    private fun logTransaction(transaction: ITransaction, result: TransactionResult) {
        if (doLogs) {
            if (result == TransactionResult.SUCCESS) {
                Catconomy.iCatLogger!!.success(transaction)
            } else {
                Catconomy.iCatLogger!!.fail(transaction, result)
            }
        }
    }

    companion object {
        private fun getTransactionResult(transaction: ITransaction): TransactionResult {
            val uuidIterator: Iterator<UUID?> = transaction.usersInvolved!!.iterator()
            val firstUser = uuidIterator.next()
            val transactionAmount = transaction.amount
            val result: TransactionResult = when (transaction.transactionType) {
                TransactionType.DELETE_USER -> deleteUser(
                    firstUser!!
                )
                TransactionType.CREATE_USER -> createUser(
                    firstUser!!,
                    transactionAmount
                )
                TransactionType.TRANSFER_CURRENCY -> transferCurrency(
                    firstUser!!,
                    uuidIterator.next()!!,
                    transactionAmount
                )
                TransactionType.SUBTRACT_CURRENCY -> subtractCurrency(
                    firstUser!!,
                    transactionAmount
                )
                TransactionType.GIVE_CURRENCY -> giveCurrency(
                    firstUser!!,
                    transactionAmount
                )
                else -> TransactionResult.ILLEGAL_TRANSACTION
            }
            return result
        }

        private fun transferCurrency(fromUser: UUID, toUser: UUID, amount: Double): TransactionResult {
            if (amount <= 0) {
                return TransactionResult.ILLEGAL_TRANSACTION
            }
            if (fromUser === toUser) {
                return TransactionResult.ILLEGAL_TRANSACTION
            }
            if (!userExistsStatic(fromUser) || !userExistsStatic(toUser)) {
                return TransactionResult.USER_DOES_NOT_EXIST
            }
            val fromUserMoney = getBalanceStatic(fromUser)
            if (fromUserMoney - amount < 0){
                return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY
            }
            val toUserMoney = getBalanceStatic(toUser)
            Catconomy.database!!.setUserBalance(fromUser,fromUserMoney - amount)
            Catconomy.database!!.setUserBalance(toUser, toUserMoney + amount)
            return TransactionResult.SUCCESS
        }

        private fun deleteUser(user: UUID): TransactionResult {
            if (!userExistsStatic(user)) {
                return TransactionResult.USER_DOES_NOT_EXIST
            }
            Catconomy.database!!.removeUser(user)
            return TransactionResult.SUCCESS
        }

        private fun createUser(user: UUID, amount: Double): TransactionResult {
            if (userExistsStatic(user)) {
                return TransactionResult.USER_ALREADY_EXISTS
            }
            Catconomy.database!!.setUserBalance(user, amount)
            return TransactionResult.SUCCESS
        }

        private fun giveCurrency(toUser: UUID, amount: Double): TransactionResult {
            if (!userExistsStatic(toUser)) {
                return TransactionResult.USER_DOES_NOT_EXIST
            }
            if (amount <= 0) {
                return TransactionResult.ILLEGAL_TRANSACTION
            }
            val userMoney = getBalanceStatic(toUser) + amount
            Catconomy.database!!.setUserBalance(toUser, userMoney)
            return TransactionResult.SUCCESS
        }

        private fun subtractCurrency(fromUser: UUID, amount: Double): TransactionResult {
            if (amount <= 0) {
                return TransactionResult.ILLEGAL_TRANSACTION
            }
            if (!userExistsStatic(fromUser)) {
                return TransactionResult.USER_DOES_NOT_EXIST
            }
            val userBalance = getBalanceStatic(fromUser)
            if (userBalance - amount < 0) {
                return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY
            }
            Catconomy.database!!.setUserBalance(fromUser, userBalance - amount)
            return TransactionResult.SUCCESS
        }

        fun userExistsStatic(user: UUID): Boolean {
            return Catconomy.database!!.userExists(user)
        }

        fun getBalanceStatic(user: UUID): Double {
            return Catconomy.database!!.getUserBalance(user)
        }
    }

}