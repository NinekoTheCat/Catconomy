package ninekothecat.catconomy.eventlisteners;

import ninekothecat.catconomy.Catconomy;
import org.bukkit.scheduler.BukkitRunnable;

public class CatBalanceHandlerMaintnanceTask extends BukkitRunnable {
    @Override
    public void run() {
        Catconomy.logger.info("Maintaining Balance Handler...");
        Catconomy.getBalanceHandler().maintainSelf();
        Catconomy.logger.info("Maintaining Balance Handler done!");
    }
}
