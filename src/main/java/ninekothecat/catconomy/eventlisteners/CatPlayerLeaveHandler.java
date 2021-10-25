package ninekothecat.catconomy.eventlisteners;

import ninekothecat.catconomy.Catconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CatPlayerLeaveHandler implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Catconomy.getBalanceHandler().syncPlayerOnLeave(event.getPlayer().getUniqueId());
    }
}
