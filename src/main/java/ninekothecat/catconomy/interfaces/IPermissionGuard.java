package ninekothecat.catconomy.interfaces;

/**
 * The interface Permission guard.
 */
public interface IPermissionGuard {
    /**
     * checks if the transaction is permitted.
     *
     * @param transaction the transaction
     * @return the boolean
     */
    boolean isPermitted(ITransaction transaction);
}
