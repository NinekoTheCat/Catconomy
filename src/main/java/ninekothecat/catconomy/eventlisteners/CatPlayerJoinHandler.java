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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Catconomy.getBalanceHandler().syncPlayerOnJoin(event.getPlayer().getUniqueId());
        if (!Catconomy.getBalanceHandler().userExists(event.getPlayer().getUniqueId())) {
            CatTransaction transaction = new CatTransaction(TransactionType.CREATE_USER, true,
                    100d, null, new ArrayList<>(Collections.singletonList(event.getPlayer().getUniqueId())));
            Catconomy.getBalanceHandler().doTransaction(transaction);
        }
    }
}
