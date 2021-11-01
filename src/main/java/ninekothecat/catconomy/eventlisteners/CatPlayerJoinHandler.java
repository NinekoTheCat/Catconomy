package ninekothecat.catconomy.eventlisteners;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.defaultImplementations.CatTransaction;
import ninekothecat.catconomy.enums.TransactionType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collections;

public class CatPlayerJoinHandler implements Listener {
    double amount;

    public CatPlayerJoinHandler(double amount) {
        this.amount = amount;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Catconomy.getBalanceHandler().userExists(event.getPlayer().getUniqueId()) && amount != 0) {
            CatTransaction transaction = new CatTransaction(TransactionType.CREATE_USER, true,
                    amount, null, new ArrayList<>(Collections.singletonList(event.getPlayer().getUniqueId()))
                    , String.format("Create account for %s With starting currency = %s", event.getPlayer().getDisplayName(), amount),Catconomy.getProvidingPlugin(Catconomy.class));
            Catconomy.getBalanceHandler().doTransaction(transaction);
        }
    }

}
