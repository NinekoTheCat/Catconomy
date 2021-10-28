package ninekothecat.catconomy.interfaces;

import java.util.UUID;

public interface IUser {
    UUID getUUID();

    double getMoney();

    void setMoney(double amount);

    boolean isDirty();

    void setDirty(boolean dirty);
}
