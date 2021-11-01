package ninekothecat.catconomy.interfaces;

import ninekothecat.catconomy.enums.TransactionResult;

import java.util.UUID;

/**
 * the balance handler interface
 */
public interface IBalanceHandler {
    /**
     * does a transaction and returns the result
     *
     * @param transaction the transaction
     * @return the transaction result
     */
    @SuppressWarnings("UnusedReturnValue")
    TransactionResult doTransaction(ITransaction transaction);

     boolean userExists(UUID user);

     double getBalance(UUID user);

}
