package ninekothecat.catconomy.commands.info;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InfoCommandExecutor implements ICatEconomyCommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        sender.sendMessage(ChatColor.RESET + "CatConomy version = " + ChatColor.GOLD + Catconomy.getProvidingPlugin(Catconomy.class).getDescription().getVersion());
        sender.sendMessage(ChatColor.RESET + "Vault integration active = "+ ChatColor.GOLD + Catconomy.vaultActive);
        sender.sendMessage(ChatColor.RESET + "Database active = " + ChatColor.GOLD + Catconomy.database.getClass().getSimpleName());
        sender.sendMessage(ChatColor.RESET + "Prefix active = long: " + Catconomy.prefix.getLongPrefix() + " short: " + Catconomy.prefix.getShortPrefix());
        return true;
    }
}
