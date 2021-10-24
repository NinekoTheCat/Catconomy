package ninekothecat.catconomy.interfaces;

import ninekothecat.catconomy.enums.TransactionType;

import java.util.Collection;
import java.util.UUID;

/**
 * The interface Transaction.
 */
public interface ITransaction {
    /**
     * Gets transaction type.
     *
     * @return the transaction type
     */
    TransactionType getTransactionType();

    /**
     * if the server console is the issuer of the transaction returns true.
     *
     * @return isConsole
     */
    boolean isConsole();

    /**
     * Gets the initiator of the transaction.
     * can be null
     *
     * @return the initiator
     */
    UUID getInitiator();

    /**
     * Gets the users involved in the transaction.
     * can be null
     *
     * @return the users involved
     */
    Collection<UUID> getUsersInvolved();

    Double getAmount();
}
