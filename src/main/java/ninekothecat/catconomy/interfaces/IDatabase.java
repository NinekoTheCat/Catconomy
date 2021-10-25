package ninekothecat.catconomy.interfaces;


import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * basic database interface, use this if you'd like to make a database compatible with this plugin
 */
public interface IDatabase {

    /**
     * returns true if the user exists
     *
     * @param user the user
     * @return userExists
     */
    boolean userExists(UUID user);

    /**
     * Removes a user.
     *
     * @param user the user
     */
    void removeUser(UUID user);

    void setUserBalance(UUID user, double balance);

    /**
     * Sets a batch of users' balance.
     *
     * @param userBalances the user balances
     */
    void setUsersBalance(Map<UUID, Double> userBalances);

    /**
     * Gets a batch of users' balance.
     *
     * @param users the users
     * @return the users balance
     */
    Map<UUID, Double> getUsersBalance(Collection<UUID> users);
}
