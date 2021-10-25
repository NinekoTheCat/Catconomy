package ninekothecat.catconomy.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface ICatEconomyCommandExecutor {
    boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
}
