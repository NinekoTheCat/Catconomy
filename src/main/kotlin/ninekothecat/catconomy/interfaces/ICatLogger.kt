package ninekothecat.catconomy.interfaces

import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.interfaces.ITransaction

/**
 * The interface Cat logger.
 */
interface ICatLogger {
    /**
     * logs a successful transaction.
     *
     * @param transaction the transaction
     */
    fun success(transaction: ITransaction)

    /**
     * logs a failed transaction
     *
     * @param transaction the transaction
     * @param result      the result
     */
    fun fail(transaction: ITransaction, result: TransactionResult)

    /**
     * logs an error transaction
     *
     * @param transaction the transaction
     * @param result      the result
     * @param exception   the exception
     */
    fun error(transaction: ITransaction, result: TransactionResult, exception: Exception?)
}