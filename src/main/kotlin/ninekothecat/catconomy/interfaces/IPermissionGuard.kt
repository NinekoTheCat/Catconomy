package ninekothecat.catconomy.interfaces

import ninekothecat.catplugincore.money.interfaces.ITransaction

/**
 * The interface Permission guard.
 */
interface IPermissionGuard {
    /**
     * checks if the transaction is permitted.
     *
     * @param transaction the transaction
     * @return the boolean
     */
    fun isPermitted(transaction: ITransaction): Boolean
}