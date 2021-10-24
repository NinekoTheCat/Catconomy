package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.interfaces.IDatabase;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unchecked")
public class CatMapDBDatabase implements IDatabase {
    private final DBMaker.Maker fileDB;

    public CatMapDBDatabase() {
        File dbFile = new File(Catconomy.getProvidingPlugin(Catconomy.class).getDataFolder(), "accounts.db");
        fileDB = DBMaker.fileDB(dbFile);
    }

    @Override
    public boolean userExists(UUID user) {
        DB db = fileDB.make();
        ConcurrentMap<UUID, Double> user_map = (ConcurrentMap<UUID, Double>) db.hashMap("accounts").createOrOpen();
        boolean exists = user_map.containsKey(user);
        db.close();
        return exists;
    }

    @Override
    public void removeUser(UUID user) {
        DB db = fileDB.make();
        ConcurrentMap<UUID, Double> user_map = (ConcurrentMap<UUID, Double>) db.hashMap("accounts").createOrOpen();
        user_map.remove(user);
        db.commit();
        db.close();
    }

    @Override
    public void setUsersBalance(Map<UUID, Double> userBalances) {
        DB db = fileDB.make();
        ConcurrentMap<UUID, Double> user_map = (ConcurrentMap<UUID, Double>) db.hashMap("accounts").createOrOpen();

        for (UUID user : userBalances.keySet()) {
            user_map.put(user, userBalances.get(user));
        }
        db.commit();
        db.close();
    }

    @Override
    public Map<UUID, Double> getUsersBalance(Collection<UUID> users) {
        DB db = fileDB.make();
        ConcurrentMap<UUID, Double> user_map = (ConcurrentMap<UUID, Double>) db.hashMap("accounts").createOrOpen();
        Map<UUID, Double> gottenUsers = new HashMap<>();
        for (UUID user : users) {
            gottenUsers.put(user, user_map.get(user));
        }
        db.close();
        return gottenUsers;
    }
}
