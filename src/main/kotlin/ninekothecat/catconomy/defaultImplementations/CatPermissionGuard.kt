package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.interfaces.IPermissionGuard;
import ninekothecat.catplugincore.money.interfaces.ITransaction;
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
            return isPermitted(initiator,"catconomy.transfer");
        }
    }

    private boolean isPermitted(UUID initiator,String permissionName) {
        return Objects.requireNonNull(Bukkit.getServer().getPlayer(initiator)).hasPermission(permissionName);
    }

    private boolean canSubtractCurrency(UUID user) {
        return isPermitted(user,"catconomy.subtract");
    }

    private boolean canGiveCurrency(UUID user) {
        return isPermitted(user,"catconomy.give");
    }

    private boolean canCreateUser(UUID user) {
        return isPermitted(user,"catconomy.create.user");
    }

    private boolean canDeleteUser(UUID user) {
        return isPermitted(user, "catconomy.delete.user");
    }
}
