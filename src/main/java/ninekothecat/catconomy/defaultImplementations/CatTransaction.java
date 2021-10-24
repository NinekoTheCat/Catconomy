package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.enums.TransactionType;
import ninekothecat.catconomy.interfaces.ITransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class CatTransaction implements ITransaction {
    private final TransactionType transactionType;
    private final boolean isConsole;
    private final double amount;
    private final UUID initiator;
    private final ArrayList<UUID> usersInvolved;

    public CatTransaction(TransactionType transactionType, boolean isConsole, double amount, UUID initiator, ArrayList<UUID> usersInvolved) {
        this.transactionType = transactionType;
        this.isConsole = isConsole;
        this.amount = amount;
        this.initiator = initiator;
        this.usersInvolved = usersInvolved;
    }

    @Override
    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    @Override
    public boolean isConsole() {
        return this.isConsole;
    }

    @Override
    public UUID getInitiator() {
        return this.initiator;
    }

    @Override
    public Collection<UUID> getUsersInvolved() {
        return this.usersInvolved;
    }

    @Override
    public Double getAmount() {
        return this.amount;
    }
}
