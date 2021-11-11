package ninekothecat.catconomy.interfaces

import java.util.*

/**
 * basic database interface, use this if you'd like to make a database compatible with this plugin
 */
interface IDatabase {
    /**
     * returns true if the user exists
     *
     * @param user the user
     * @return userExists boolean
     */
    fun userExists(user: UUID): Boolean

    /**
     * Removes a user.
     *
     * @param user the user
     */
    fun removeUser(user: UUID)

    /**
     * Sets user balance.
     *
     * @param user    the user
     * @param balance the balance
     */
    fun setUserBalance(user: UUID, balance: Double)

    /**
     * Gets user balance.
     *
     * @param user the user
     * @return the user balance
     */
    fun getUserBalance(user: UUID): Double

    /**
     * Sets a batch of users' balance.
     *
     * @param userBalances the user balances
     */
    fun setUsersBalance(userBalances: MutableMap<UUID, Double>)

    /**
     * Gets a batch of users' balance.
     *
     * @param users the users
     * @return the users balance
     */
    fun getUsersBalance(users: Collection<UUID>): Map<UUID, Double?>
}