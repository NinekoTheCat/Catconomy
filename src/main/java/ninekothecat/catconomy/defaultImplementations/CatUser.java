package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.interfaces.IUser;

import java.util.UUID;

public class CatUser implements IUser {
    private final UUID uuid;
    boolean dirty = false;
    private double money;

    public CatUser(double money, UUID uuid) {
        this.money = money;
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public double getMoney() {
        return money;
    }

    @Override
    public void setMoney(double money) {
        dirty = true;
        this.money = money;

    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
