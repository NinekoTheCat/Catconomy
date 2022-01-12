package ninekothecat.catconomy.defaultImplementations.database

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.IDatabase
import org.bukkit.plugin.java.JavaPlugin
import org.mapdb.DB
import org.mapdb.DBMaker.fileDB
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentMap

@Suppress("UNCHECKED_CAST")
class CatMapDBDatabase : IDatabase {
    companion object {
        var db : DB? = null
    }


    override fun userExists(user: UUID): Boolean {
        val userMap = db?.hashMap("accounts")?.createOrOpen() as ConcurrentMap<UUID, Double>
        return userMap.containsKey(user)
    }

    override fun removeUser(user: UUID) {
        val userMap = db?.hashMap("accounts")?.createOrOpen() as ConcurrentMap<UUID, Double>
        userMap.remove(user)
        db!!.commit()
    }

    override fun setUserBalance(user: UUID, balance: Double) {
        val userMap = db?.hashMap("accounts")?.createOrOpen() as ConcurrentMap<UUID, Double>
        userMap[user] = balance
        db!!.commit()
    }

    override fun getUserBalance(user: UUID): Double {
        val userMap = db?.hashMap("accounts")?.createOrOpen() as ConcurrentMap<UUID, Double>
        val amount = userMap.getOrDefault(user, Catconomy.startingAmount)
        return amount
    }

    override fun setUsersBalance(userBalances: MutableMap<UUID, Double>) {
        val userMap = db?.hashMap("accounts")?.createOrOpen() as ConcurrentMap<UUID, Double?>
        for (user in userBalances.keys) {
            userMap[user] = userBalances[user]
        }
        db!!.commit()
    }

    override fun getUsersBalance(users: Collection<UUID>): Map<UUID, Double?> {
        val userMap = db?.hashMap("accounts")?.createOrOpen() as ConcurrentMap<UUID, Double>
        val gottenUsers: MutableMap<UUID, Double?> = HashMap()
        for (user in users) {
            gottenUsers[user] = userMap[user]
        }
        return gottenUsers
    }

    init {
        val dbFile = File(JavaPlugin.getProvidingPlugin(Catconomy::class.java).dataFolder, "accounts.db")
        if (db == null){
            db = fileDB(dbFile).make()
        }
    }

}