package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.enums.TransactionResult;
import ninekothecat.catconomy.interfaces.IBalanceHandler;
import ninekothecat.catconomy.interfaces.ITransaction;

import java.util.*;

public class CatBalanceHandler implements IBalanceHandler {
    private final HashMap<UUID, Double> usersCache = new HashMap<>();
    private final HashMap<UUID,Double> usersGetCache = new HashMap<>();
    private Boolean isDirty = false;

    @Override
    public TransactionResult doTransaction(ITransaction transaction) {
        try {
            if (Catconomy.permissionGuard.isPermitted(transaction)) {
                Collection<UUID> usersInvolved = transaction.getUsersInvolved();
                Iterator<UUID> uuidIterator = usersInvolved.iterator();
                Double transactionAmount = transaction.getAmount();
                getUsersIntoCache(transaction.getUsersInvolved());
                UUID firstUser = uuidIterator.next();
                TransactionResult result;
                switch (transaction.getTransactionType()) {
                    case DELETE_USER:
                        result = deleteUser(firstUser);
                        break;
                    case CREATE_USER:
                        result = createUser(firstUser, transactionAmount);
                        break;
                    case TRANSFER_CURRENCY:
                        UUID SecondUser = uuidIterator.next();
                        result = transferCurrency(firstUser, SecondUser, transactionAmount);
                        break;
                    case SUBTRACT_CURRENCY:
                        result = subtractCurrency(firstUser, transactionAmount);
                        break;
                    case GIVE_CURRENCY:
                        result = giveCurrency(firstUser, transactionAmount);
                        break;
                    default:
                        result = TransactionResult.INTERNAL_ERROR;
                }
                if (isDirty){
                    saveAll();
                }
                return result;
            } else {
                return TransactionResult.ILLEGAL_TRANSACTION;
            }
        } catch (Exception e) {
            return TransactionResult.INTERNAL_ERROR;
        }

    }

    @Override
    public boolean userExists(UUID user) {
        getUsersIntoCache(Collections.singleton(user));
        return usersCache.containsKey(user);
    }

    private void getUsersIntoCache(Collection<UUID> userCollection) {
        List<UUID> userArray = new ArrayList<>(userCollection);
        if (isDirty) {
            saveAll();
        }
        Map<UUID, Double> users = Catconomy.database.getUsersBalance(userArray);
        users.forEach((user, userMoney) -> {
            if (Catconomy.database.userExists(user)) {
                usersCache.put(user, userMoney);
            }
        });

    }

    @Override
    public double getBalance(UUID user) {
        if (usersGetCache.containsKey(user)){
            return usersGetCache.get(user);
        }else {
            if (userExists(user)){
                usersGetCache.put(user,Catconomy.database.getUsersBalance(Collections.singleton(user)).get(user));
                return usersGetCache.get(user);
            }
        }
        return 0;
    }

    private TransactionResult deleteUser(UUID user) {
        if (this.userExists(user)) {
            usersCache.remove(user);
            Catconomy.database.removeUser(user);
            return TransactionResult.SUCCESS;
        } else {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }

    }

    private TransactionResult createUser(UUID user, double amount) {
        if (usersCache.containsKey(user)) {
            return TransactionResult.USER_ALREADY_EXISTS;
        }
        usersCache.put(user, amount);
        isDirty = true;
        return TransactionResult.SUCCESS;
    }

    private TransactionResult transferCurrency(UUID fromUser, UUID toUser, double amount) {
        if (fromUser == toUser){
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (amount <= 0) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (!usersCache.containsKey(fromUser) || !usersCache.containsKey(toUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        double fromUserMoney = usersCache.get(fromUser);
        if ((fromUserMoney - amount) < 0) {
            return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY;
        }

        double toUserMoney = usersCache.get(toUser) + amount;
        fromUserMoney -= amount;
        usersCache.put(toUser, toUserMoney);
        usersCache.put(fromUser, fromUserMoney);
        isDirty = true;
        return TransactionResult.SUCCESS;
    }

    private TransactionResult subtractCurrency(UUID fromUser, double amount) {
        if (!usersCache.containsKey(fromUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        double fromUserMoney = usersCache.get(fromUser);
        if ((fromUserMoney - amount) < 0) {
            return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY;
        }
        fromUserMoney -= amount;
        usersCache.put(fromUser, fromUserMoney);
        isDirty = true;
        return TransactionResult.SUCCESS;
    }

    private TransactionResult giveCurrency(UUID toUser, double amount) {
        if (!usersCache.containsKey(toUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        double toUserMoney = usersCache.get(toUser) + amount;
        usersCache.put(toUser, toUserMoney);
        isDirty = true;
        return TransactionResult.SUCCESS;
    }

    @Override
    public void saveAll() {
        Catconomy.database.setUsersBalance(usersCache);
        usersGetCache.clear();
        usersCache.clear();
        isDirty = false;
    }

    @Override
    public void syncPlayerOnJoin(UUID user) {
        if (Catconomy.database.userExists(user)){
            double balance = Catconomy.database.getUsersBalance(Collections.singleton(user)).get(user);
            usersCache.put(user, balance);
        }
    }

    @Override
    public void syncPlayerOnLeave(UUID user) {
        if (Catconomy.database.userExists(user)){
            if (!this.usersCache.get(user).isNaN()){
                Catconomy.database.setUserBalance(user,this.usersCache.get(user));
            }
            usersCache.remove(user);
        }
    }
}
