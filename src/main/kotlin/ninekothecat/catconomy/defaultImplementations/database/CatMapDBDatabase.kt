package ninekothecat.catconomy.defaultImplementations.database

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.IDatabase
import org.bukkit.plugin.java.JavaPlugin
import org.mapdb.DB
import org.mapdb.DBMaker.Maker
import org.mapdb.DBMaker.fileDB
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentMap

@Suppress("UNCHECKED_CAST")
class CatMapDBDatabase : IDatabase {
    private val fileDB: Maker
    override fun userExists(user: UUID): Boolean {
        val db: DB = fileDB.make()
        val userMap = db.hashMap("accounts").createOrOpen() as ConcurrentMap<UUID, Double>
        val exists = userMap.containsKey(user)
        db.close()
        return exists
    }

    override fun removeUser(user: UUID) {
        val db: DB = fileDB.make()
        val userMap = db.hashMap("accounts").createOrOpen() as ConcurrentMap<UUID, Double>
        userMap.remove(user)
        db.commit()
        db.close()
    }

    override fun setUserBalance(user: UUID, balance: Double) {
        val db: DB = fileDB.make()
        val userMap = db.hashMap("accounts").createOrOpen() as ConcurrentMap<UUID, Double>
        userMap[user] = balance
        db.commit()
        db.close()
    }

    override fun getUserBalance(user: UUID): Double {
        val db: DB = fileDB.make()
        val userMap = db.hashMap("accounts").createOrOpen() as ConcurrentMap<UUID, Double>
        val amount = userMap.getOrDefault(user, 0.0)
        db.close()
        return amount
    }

    override fun setUsersBalance(userBalances: MutableMap<UUID, Double>) {
        val db: DB = fileDB.make()
        val userMap = db.hashMap("accounts").createOrOpen() as ConcurrentMap<UUID, Double?>
        for (user in userBalances.keys) {
            userMap[user] = userBalances[user]
        }
        db.commit()
        db.close()
    }

    override fun getUsersBalance(users: Collection<UUID>): Map<UUID, Double?> {
        val db: DB = fileDB.make()
        val userMap = db.hashMap("accounts").createOrOpen() as ConcurrentMap<UUID, Double>
        val gottenUsers: MutableMap<UUID, Double?> = HashMap()
        for (user in users) {
            gottenUsers[user] = userMap[user]
        }
        db.close()
        return gottenUsers
    }

    init {
        val dbFile = File(JavaPlugin.getProvidingPlugin(Catconomy::class.java).dataFolder, "accounts.db")
        fileDB = fileDB(dbFile)
    }
}