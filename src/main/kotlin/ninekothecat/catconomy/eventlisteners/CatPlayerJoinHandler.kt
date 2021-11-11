package ninekothecat.catconomy.eventlisteners

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.defaultImplementations.CatTransaction
import ninekothecat.catplugincore.money.enums.TransactionType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class CatPlayerJoinHandler(private var amount: Double) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!Catconomy.getBalanceHandler()!!.userExists(event.player.uniqueId) && amount != 0.0) {
            val transaction = CatTransaction(
                TransactionType.CREATE_USER,
                true,
                amount,
                null,
                ArrayList(listOf(event.player.uniqueId)),
                String.format("Create account for %s With starting currency = %s", event.player.displayName, amount),
                JavaPlugin.getProvidingPlugin(
                    Catconomy::class.java
                )
            )
            Catconomy.getBalanceHandler()!!.doTransaction(transaction)
        }
    }
}