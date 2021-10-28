package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.enums.TransactionResult;
import ninekothecat.catconomy.interfaces.IBalanceHandler;
import ninekothecat.catconomy.interfaces.ITransaction;
import ninekothecat.catconomy.interfaces.IUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CatBalanceHandler implements IBalanceHandler {
    private final HashMap<UUID, IUser> userMoney = new HashMap<>();
    private boolean dirty = false;
    private boolean doLogs;

    public CatBalanceHandler(boolean doLogs) {
        this.doLogs = doLogs;
    }

    @Override
    public TransactionResult doTransaction(ITransaction transaction) {
        if (Catconomy.permissionGuard.isPermitted(transaction)) {
            if (transaction.getInitiator() != null && !userMoney.containsKey(transaction.getInitiator())) {
                userMoney.put(transaction.getInitiator()
                        , new CatUser(Catconomy.database.getUserBalance(transaction.getInitiator())
                                , transaction.getInitiator()));
            }
            for (UUID user : transaction.getUsersInvolved()) {
                if (!userMoney.containsKey(user)) {
                    userMoney.put(user, new CatUser(Catconomy.database.getUserBalance(user), user));
                }
            }
            Iterator<UUID> uuidIterator = transaction.getUsersInvolved().iterator();
            UUID firstUser = uuidIterator.next();
            Double transactionAmount = transaction.getAmount();
            TransactionResult result;
            switch (transaction.getTransactionType()) {
                case DELETE_USER:
                    result = deleteUser(firstUser);
                    break;
                case CREATE_USER:
                    result = createUser(firstUser, transactionAmount);
                    break;
                case TRANSFER_CURRENCY:
                    result = transferCurrency(firstUser, uuidIterator.next(), transactionAmount);
                    break;
                case SUBTRACT_CURRENCY:
                    result = subtractCurrency(firstUser, transactionAmount);
                    break;
                case GIVE_CURRENCY:
                    result = giveCurrency(firstUser, transactionAmount);
                    break;
                default:
                    result = TransactionResult.ILLEGAL_TRANSACTION;
                    break;
            }
            if (result == TransactionResult.SUCCESS) {
                dirty = true;
                if (doLogs){
                    Catconomy.iCatLogger.success(transaction);
                }

            }else if (doLogs){
                Catconomy.iCatLogger.fail(transaction,result);
            }
            return result;
        }
        return TransactionResult.ILLEGAL_TRANSACTION;
    }

    private TransactionResult transferCurrency(UUID fromUser, UUID toUser, double amount) {
        if (amount <= 0) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (fromUser == toUser) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (!userExists(fromUser) || !userExists(toUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        IUser fromIUser = this.userMoney.get(fromUser);
        double fromUserMoney = fromIUser.getMoney();
        IUser toIUser = this.userMoney.get(toUser);
        double toUserMoney = toIUser.getMoney();
        if (fromUserMoney - amount < 0) {
            return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY;
        }
        fromUserMoney -= amount;
        toUserMoney += amount;
        fromIUser.setMoney(fromUserMoney);
        toIUser.setMoney(toUserMoney);
        return TransactionResult.SUCCESS;

    }

    private TransactionResult deleteUser(UUID user) {
        if (!userExists(user)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        Catconomy.database.removeUser(user);
        this.userMoney.remove(user);
        return TransactionResult.SUCCESS;
    }

    private TransactionResult createUser(UUID user, double amount) {
        if (userExists(user)) {
            return TransactionResult.USER_ALREADY_EXISTS;
        }
        Catconomy.database.setUserBalance(user, amount);
        syncPlayerOnJoin(user);
        return TransactionResult.SUCCESS;
    }

    private TransactionResult giveCurrency(UUID toUser, double amount) {
        if (!userExists(toUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }

        if (!Double.isNaN(amount) && Double.isFinite(amount)) {
            IUser user = userMoney.get(toUser);
            double money = user.getMoney();
            if (amount < 1) {
                return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY;
            }
            user.setMoney(money + amount);
            userMoney.put(toUser, user);
            return TransactionResult.SUCCESS;
        } else {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
    }

    private TransactionResult subtractCurrency(UUID fromUser, double amount) {
        if (amount <= 0) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (!userExists(fromUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        IUser fromIUser = userMoney.get(fromUser);
        double newAmount = fromIUser.getMoney() - amount;
        if (newAmount < 0) {
            return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY;
        }
        fromIUser.setMoney(newAmount);
        return TransactionResult.SUCCESS;
    }

    @Override
    public boolean userExists(UUID user) {
        if (this.userMoney.containsKey(user)) {
            return true;
        } else {
            return Catconomy.database.userExists(user);
        }
    }

    @Override
    public void maintainSelf() {
        saveAll();
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        Set<UUID> uuidSet = players.stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        this.userMoney.values().stream().filter(user -> !uuidSet.contains(user.getUUID())).forEach(user -> this.userMoney.remove(user.getUUID()));
    }

    @Override
    public double getBalance(UUID user) {
        if (this.userMoney.containsKey(user)) {
            return this.userMoney.get(user).getMoney();
        } else {
            double money = Catconomy.database.getUserBalance(user);
            this.userMoney.put(user, new CatUser(money, user));
            return money;
        }
    }

    @Override
    public void saveAll() {
        if (!this.dirty) {
            return;
        }
        for (IUser user : userMoney.values()) {
            if (user.isDirty()) {
                Catconomy.database.setUserBalance(user.getUUID(), user.getMoney());
            } else {
                double userBalance = Catconomy.database.getUserBalance(user.getUUID());
                if (Double.isInfinite(userBalance) || !Double.isNaN(userBalance)) {
                    user.setMoney(0);
                    continue;
                }
                if (userBalance != user.getMoney()) {
                    user.setMoney(userBalance);
                    user.setDirty(false);
                }
            }
        }
    }


    @Override
    public void syncPlayerOnJoin(UUID user) {
        double userBalance = Catconomy.database.getUserBalance(user);
        userMoney.put(user, new CatUser(userBalance, user));
    }

    @Override
    public void syncPlayerOnLeave(UUID user) {
        IUser userData = userMoney.get(user);

        if (userData.isDirty()) {
            Catconomy.database.setUserBalance(user, userData.getMoney());
        }
        userMoney.remove(user);

    }
}
