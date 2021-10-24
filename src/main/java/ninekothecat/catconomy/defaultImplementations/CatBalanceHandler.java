package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.enums.TransactionResult;
import ninekothecat.catconomy.interfaces.IBalanceHandler;
import ninekothecat.catconomy.interfaces.ITransaction;

import java.util.*;

public class CatBalanceHandler implements IBalanceHandler {
    private final HashMap<UUID, Double> usersCache = new HashMap<>();
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
                switch (transaction.getTransactionType()) {
                    case DELETE_USER:
                        return deleteUser(firstUser);
                    case CREATE_USER:
                        return createUser(firstUser, transactionAmount);
                    case TRANSFER_CURRENCY:
                        UUID SecondUser = uuidIterator.next();
                        return transferCurrency(firstUser, SecondUser, transactionAmount);
                    case SUBTRACT_CURRENCY:
                        return subtractCurrency(firstUser, transactionAmount);
                    case GIVE_CURRENCY:
                        return giveCurrency(firstUser, transactionAmount);
                }
            } else {
                return TransactionResult.ILLEGAL_TRANSACTION;
            }
        } catch (Exception e) {
            return TransactionResult.INTERNAL_ERROR;
        }

        return TransactionResult.INTERNAL_ERROR;
    }

    @Override
    public boolean userExists(UUID user) {
        getUsersIntoCache(Collections.singleton(user));
        return usersCache.containsKey(user);
    }

    private void getUsersIntoCache(Collection<UUID> userCollection) {
        List<UUID> userArray = new ArrayList<>(userCollection);
        if (!isDirty) {
            userArray.removeIf(this.usersCache::containsKey);
        } else {
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
        getUsersIntoCache(Collections.singleton(user));
        if (usersCache.containsKey(user)) {
            return usersCache.get(user);
        } else {
            return 0;
        }

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
        isDirty = false;
    }
}
