package ninekothecat.catconomy.enums;

import net.milkbowl.vault.economy.EconomyResponse;

/**
 * The Transaction result.
 */
public enum TransactionResult{
    /**
     * Insufficient amount of currency.
     */
    INSUFFICIENT_AMOUNT_OF_CURRENCY,
    /**
     * User does not exist.
     */
    USER_DOES_NOT_EXIST,
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

    public static EconomyResponse.ResponseType toEconomyResponseType(TransactionResult result){
        if (result == TransactionResult.SUCCESS) {
            return EconomyResponse.ResponseType.SUCCESS;
        }
        return EconomyResponse.ResponseType.FAILURE;
    }

}
