package ninekothecat.catconomy.interfaces;

import ninekothecat.catconomy.enums.TransactionResult;

import java.util.logging.Logger;

/**
 * The interface Cat logger.
 */
public interface ICatLogger {
    /**
     * logs a successful transaction.
     *
     * @param transaction the transaction
     */
    void success(ITransaction transaction);

    /**
     * logs a failed transaction
     *
     * @param transaction the transaction
     * @param result      the result
     */
    void fail(ITransaction transaction, TransactionResult result);
}
