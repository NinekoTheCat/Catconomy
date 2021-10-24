package ninekothecat.catconomy.enums;

/**
 * The Transaction type.
 */
public enum TransactionType {
    /**
     * Delete a user.
     */
    DELETE_USER,
    /**
     * Create a new user.
     */
    CREATE_USER,
    /**
     * Transfer currency from a user to a user.
     */
    TRANSFER_CURRENCY,
    /**
     * Subtract currency from the user.
     */
    SUBTRACT_CURRENCY,
    /**
     * Give currency to a user.
     */
    GIVE_CURRENCY,
}
