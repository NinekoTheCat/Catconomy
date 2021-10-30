package ninekothecat.catconomy.enums;

import net.milkbowl.vault.economy.EconomyResponse;

/**
 * The Transaction result.
 */
public enum TransactionResult {
    /**
     * Lack of permissions
     */
    LACK_OF_PERMS,
    /**
     * Insufficient amount of currency.
     */
    INSUFFICIENT_AMOUNT_OF_CURRENCY,
    /**
     * User does not exist.
     */
    USER_DOES_NOT_EXIST,
    /**
     * User already exists transaction result.
     */
    USER_ALREADY_EXISTS,
//    /**
//     * User is banned.
//     */
//    USER_IS_BANNED,
    /**
     * Illegal transaction, something went wrong.
     */
    ILLEGAL_TRANSACTION,
    /**
     * Internal error something went really wrong,
     */
    INTERNAL_ERROR,
    /**
     * the transaction is successful.
     */
    SUCCESS;

    /**
     * To economy response type economy response . response type.
     *
     * @param result the result
     * @return the economy response . response type
     */
    public static EconomyResponse.ResponseType toEconomyResponseType(TransactionResult result) {
        if (result == TransactionResult.SUCCESS) {
            return EconomyResponse.ResponseType.SUCCESS;
        }
        return EconomyResponse.ResponseType.FAILURE;
    }

}
