package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.enums.TransactionResult;
import ninekothecat.catconomy.interfaces.IBalanceHandler;
import ninekothecat.catconomy.interfaces.ITransaction;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class CatBalanceHandler implements IBalanceHandler {
    private final boolean doLogs;
    private final ThreadPoolExecutor threadPoolExecutor;
    public CatBalanceHandler(boolean doLogs) {
        this.doLogs = doLogs;
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Override
    public TransactionResult doTransaction(ITransaction transaction) {
        try {
            if (Catconomy.permissionGuard.isPermitted(transaction)) {
                Future<TransactionResult> resultFuture = threadPoolExecutor.submit(() -> getTransactionResult(transaction));
                TransactionResult result = resultFuture.get();
                logTransaction(transaction, result);
                return result;
            }else {
                Catconomy.iCatLogger.fail(transaction,TransactionResult.LACK_OF_PERMS);
                return TransactionResult.LACK_OF_PERMS;
            }
        }catch (Exception e){
            Catconomy.iCatLogger.error(transaction,TransactionResult.INTERNAL_ERROR,e);
            return TransactionResult.INTERNAL_ERROR;
        }
    }

    @Override
    public boolean userExists(UUID user) {
        return userExistsStatic(user);
    }

    @Override
    public double getBalance(UUID user) {
        return getBalanceStatic(user);
    }


    private void logTransaction(ITransaction transaction, TransactionResult result) {
        if (doLogs){
            if (result == TransactionResult.SUCCESS) {
                    Catconomy.iCatLogger.success(transaction);
            }else {
                Catconomy.iCatLogger.fail(transaction, result);
            }
        }
    }

    private static TransactionResult getTransactionResult(ITransaction transaction) {
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
        return result;
    }

    private static TransactionResult transferCurrency(UUID fromUser, UUID toUser, double amount) {
        if (amount <= 0) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (fromUser == toUser) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (!userExistsStatic(fromUser) || !userExistsStatic(toUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        double fromUserMoney = getBalanceStatic(fromUser);

        return TransactionResult.SUCCESS;

    }

    private static TransactionResult deleteUser(UUID user) {
        if (!userExistsStatic(user)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        Catconomy.database.removeUser(user);
        return TransactionResult.SUCCESS;
    }

    private static TransactionResult createUser(UUID user, double amount) {
        if (userExistsStatic(user)) {
            return TransactionResult.USER_ALREADY_EXISTS;
        }
        Catconomy.database.setUserBalance(user, amount);
        return TransactionResult.SUCCESS;
    }

    private static TransactionResult giveCurrency(UUID toUser, double amount) {
        if (!userExistsStatic(toUser)){
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        if (amount <= 0){
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        double userMoney = getBalanceStatic(toUser) + amount;
        Catconomy.database.setUserBalance(toUser,userMoney);
        return TransactionResult.SUCCESS;
    }

    private static TransactionResult subtractCurrency(UUID fromUser, double amount) {
        if (amount <= 0) {
            return TransactionResult.ILLEGAL_TRANSACTION;
        }
        if (!userExistsStatic(fromUser)) {
            return TransactionResult.USER_DOES_NOT_EXIST;
        }
        double userBalance = getBalanceStatic(fromUser);
        if (userBalance -amount <0){
            return TransactionResult.INSUFFICIENT_AMOUNT_OF_CURRENCY;
        }
        Catconomy.database.setUserBalance(fromUser,userBalance - amount);
        return TransactionResult.SUCCESS;
    }

    public static boolean userExistsStatic(UUID user) {
        return Catconomy.database.userExists(user);
    }

    public static double getBalanceStatic(UUID user) {
        return Catconomy.database.getUserBalance(user);
    }
}
