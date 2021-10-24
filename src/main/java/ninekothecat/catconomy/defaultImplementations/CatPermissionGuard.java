package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.interfaces.IPermissionGuard;
import ninekothecat.catconomy.interfaces.ITransaction;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public class CatPermissionGuard implements IPermissionGuard {
    @Override
    public boolean isPermitted(ITransaction transaction) {
        if (transaction.isConsole()) {
            return true;
        }
        UUID transactionInitiator = transaction.getInitiator();
        switch (transaction.getTransactionType()) {
            case DELETE_USER:
                return canDeleteUser(transactionInitiator);
            case CREATE_USER:
                return canCreateUser(transactionInitiator);
            case TRANSFER_CURRENCY:
                return canTransferCurrency(transaction.getUsersInvolved().iterator().next(), transactionInitiator);
            case SUBTRACT_CURRENCY:
                return canSubtractCurrency(transactionInitiator);
            case GIVE_CURRENCY:
                return canGiveCurrency(transactionInitiator);
        }
        return false;
    }

    private boolean canTransferCurrency(UUID fromUser, UUID initiator) {
        if (Bukkit.getServer().getBannedPlayers().contains(Bukkit.getServer().getPlayer(fromUser))) {
            return false;
        }
        if (fromUser == initiator) {
            return true;
        } else {
            return isOp(initiator);
        }
    }

    private boolean isOp(UUID initiator) {
        return Objects.requireNonNull(Bukkit.getServer().getPlayer(initiator)).hasPermission("op");
    }

    private boolean canSubtractCurrency(UUID user) {
        return isOp(user);
    }

    private boolean canGiveCurrency(UUID user) {
        return isOp(user);
    }

    private boolean canCreateUser(UUID user) {
        return isOp(user);
    }

    private boolean canDeleteUser(UUID user) {
        return isOp(user);
    }
}
