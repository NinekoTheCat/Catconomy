package ninekothecat.catconomy.defaultImplementations.database.SQL

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.IDatabase
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.function.Consumer

class CatSQLDatabase(user: String, password: String, host: String, databaseName: String, port: String) : IDatabase {
    private var conn: Connection? = null
    override fun userExists(user: UUID): Boolean {
        val query = "SELECT CURRENCY FROM ACCOUNTS WHERE UUID = ?"
        try {
            conn!!.prepareStatement(query).use { statement ->
                statement.setString(1, user.toString())
                statement.executeQuery().use { resultSet -> return resultSet.next() }
            }
        } catch (se: SQLException) {
            se.printStackTrace()
            return false
        }
    }

    override fun removeUser(user: UUID) {
        val query = "DELETE FROM ACCOUNTS WHERE UUID = ?"
        try {
            conn!!.prepareStatement(query).use { statement ->
                statement.setString(1, user.toString())
                statement.execute()
            }
        } catch (se: SQLException) {
            se.printStackTrace()
        }
    }

    override fun setUserBalance(user: UUID, balance: Double) {
        if (userExists(user)) {
            val query = "UPDATE ACCOUNTS SET CURRENCY = ? WHERE UUID = ?"
            try {
                conn!!.prepareStatement(query).use { statement ->
                    statement.setDouble(1, balance)
                    statement.setString(2, user.toString())
                    statement.execute()
                }
            } catch (se: SQLException) {
                se.printStackTrace()
            }
        } else {
            val query = "INSERT INTO ACCOUNTS (CURRENCY, UUID) " +
                    "VALUES (?, ?);"
            try {
                conn!!.prepareStatement(query).use { statement ->
                    statement.setDouble(1, balance)
                    statement.setString(2, user.toString())
                    statement.execute()
                }
            } catch (se: SQLException) {
                se.printStackTrace()
            }
        }
    }

    override fun getUserBalance(user: UUID): Double {
        val query = "SELECT DISTINCT CURRENCY FROM ACCOUNTS WHERE UUID = ?"
        try {
            conn!!.prepareStatement(query).use { statement ->
                statement.setString(1, user.toString())
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    return resultSet.getDouble("CURRENCY")
                }
            }
        } catch (se: SQLException) {
            se.printStackTrace()
            return 0.0
        }
    }

    override fun setUsersBalance(userBalances: MutableMap<UUID, Double>) {
        userBalances.forEach { (user: UUID, balance: Double) -> setUserBalance(user, balance) }
    }

    override fun getUsersBalance(users: Collection<UUID>): Map<UUID, Double?> {
        val uuidDoubleMap: MutableMap<UUID, Double?> = HashMap()
        users.forEach(Consumer { uuid: UUID ->
            val query = "SELECT CURRENCY FROM ACCOUNTS WHERE UUID = ?"
            try {
                conn!!.prepareStatement(query).use { statement ->
                    statement.setString(1, uuid.toString())
                    val set = statement.executeQuery()
                    set.next()
                    uuidDoubleMap.put(uuid, set.getDouble("CURRENCY"))
                }
            } catch (se: SQLException) {
                Catconomy.logger!!.warning(se.message)
            }
        })
        return uuidDoubleMap
    }

    init {
        try {
            //Load Drivers
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            val properties = Properties()
            properties["user"] = user
            properties["password"] = password
            properties["autoReconnect"] = "true"
            properties["useSSL"] = "false"
            properties["verifyServerCertificate"] = "false"
            //Connect to database
            val url = "jdbc:mysql://$host:$port/$databaseName"
            conn = DriverManager.getConnection(url, properties)
        } catch (e: ClassNotFoundException) {
            Catconomy.logger!!.severe("Could not locate drivers for mysql! Error: " + e.message)

        } catch (e: SQLException) {
            Catconomy.logger!!.severe("Could not connect to mysql database! Error: " + e.message)

        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val data = ("CREATE TABLE IF NOT EXISTS ACCOUNTS"
                + "  ("
                + "   CURRENCY            DOUBLE,"
                + "   UUID     VARCHAR(36))")
        val preparedStatement = conn!!.prepareStatement(data)
        preparedStatement.execute()
    }
}