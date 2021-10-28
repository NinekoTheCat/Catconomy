package ninekothecat.catconomy.interfaces;

import ninekothecat.catconomy.enums.TransactionType;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * The interface Transaction.
 */
public interface ITransaction {

    /**
     * Gets the plugin that initiated the transaction.
     *
     * @return the plugin
     */
    Plugin getPlugin();

    /**
     * Gets the transaction message.
     *
     * @return the message
     */
    String getMessage();

    /**
     * Gets transaction type.
     *
     * @return the transaction type
     */
    TransactionType getTransactionType();

    /**
     * if the server console is the issuer of the transaction returns true.
     *
     * @return isConsole boolean
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

    /**
     * Gets amount.
     *
     * @return the amount
     */
    Double getAmount();
}
