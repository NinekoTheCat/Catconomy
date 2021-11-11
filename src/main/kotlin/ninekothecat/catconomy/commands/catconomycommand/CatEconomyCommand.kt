package ninekothecat.catconomy.commands.catconomycommand

import ninekothecat.catconomy.interfaces.ICatEconomyCommandAutoComplete
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.permissions.Permission

class CatEconomyCommand {
    var autoComplete: ICatEconomyCommandAutoComplete? = null
    var executor: ICatEconomyCommandExecutor? = null
    var permission: Permission = Bukkit.getPluginManager().getPermission("catconomy.*")
    fun hasAutoComplete(): Boolean {
        return autoComplete != null
    }

    fun hasExecutor(): Boolean {
        return executor != null
    }
}