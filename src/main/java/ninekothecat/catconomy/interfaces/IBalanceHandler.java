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

    void maintainSelf();

    double getBalance(UUID user);

    void saveAll();

    void syncPlayerOnJoin(UUID user);

    void syncPlayerOnLeave(UUID user);
}
