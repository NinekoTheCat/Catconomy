package ninekothecat.catconomy.commands;

import ninekothecat.catconomy.Catconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Locale;

public class BalanceCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String shortPrefix = Catconomy.prefix.getShortPrefix();
        switch (args.length) {
            case 0:
                getSelfMoneyAmount(sender, shortPrefix);
                return true;
            case 1:
                getOtherPlayersMoneyAmount(sender, args, shortPrefix);
                return true;
            default:
                return false;
        }
    }

    private void getSelfMoneyAmount(CommandSender sender, String shortPrefix) {
        Player player = (Player) sender;
        final String MONEY_AMOUNT = Double.toString(Catconomy.getBalanceHandler().getBalance(player.getUniqueId()));
        sender.sendMessage(MessageFormat.format("{0}Your balance is {1} {2}",
                ChatColor.YELLOW,
                ChatColor.GOLD + MONEY_AMOUNT,
                ChatColor.AQUA + shortPrefix));
    }

    private void getOtherPlayersMoneyAmount(CommandSender sender, String[] args, String shortPrefix) {
        final Player[] player = new Player[]{null};
        for (Player player1 : Bukkit.getServer().getOnlinePlayers())
            if (player1.getDisplayName().toUpperCase(Locale.ROOT).equals(args[0].toUpperCase(Locale.ROOT)))
                player[0] = player1;

        if (player[0] != null && Catconomy.getBalanceHandler().userExists(player[0].getUniqueId())) {
            final String MONEY_AMOUNT = Double.toString(Catconomy.getBalanceHandler().getBalance(player[0].getUniqueId()));
            sender.sendMessage(MessageFormat.format("{0}{1}s balance is : {2}{3} {4}{5}",
                    ChatColor.YELLOW,
                    player[0].getDisplayName(),
                    ChatColor.GOLD,
                    MONEY_AMOUNT,
                    ChatColor.AQUA,
                    shortPrefix));
        } else {
            sender.sendMessage(MessageFormat.format("{0} Does not exist", ChatColor.GOLD + args[0]));
        }
    }
}
