package ninekothecat.catconomy.commands.deposit;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.defaultImplementations.CatTransaction;
import ninekothecat.catconomy.enums.TransactionType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class DepositCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            Player player = Catconomy.getPlayerFromName(args[0]);
            if (player != null && sender instanceof Player) {
                double amount;
                try {
                    amount = Double.parseDouble(args[1]);
                } catch (NumberFormatException exception) {
                    sender.sendMessage(ChatColor.RED + "Not a valid Number");
                    return false;
                }
                if (Double.isNaN(amount)) {
                    return false;
                }
                if (amount > 0) {
                    ArrayList<UUID> involved = new ArrayList<>(Arrays.asList(
                            Objects.requireNonNull(((Player) sender).getPlayer()).getUniqueId(),
                            player.getUniqueId()));

                    CatTransaction catTransaction = new CatTransaction(TransactionType.TRANSFER_CURRENCY,
                            false,
                            amount,
                            ((Player) sender).getUniqueId(),
                            involved);
                    switch (Catconomy.getBalanceHandler().doTransaction(catTransaction)) {
                        case INSUFFICIENT_AMOUNT_OF_CURRENCY:
                            sender.sendMessage(ChatColor.DARK_RED + "FAILED TRANSACTION DUE TO INSUFFICIENT AMOUNT OF CURRENCY");
                            return false;
                        case USER_DOES_NOT_EXIST:
                            sender.sendMessage(ChatColor.DARK_RED + "FAILED TRANSACTION DUE TO THE USER NOT EXISTING");
                            return false;
                        case USER_ALREADY_EXISTS:
                            sender.sendMessage(ChatColor.DARK_RED + "FAILED TRANSACTION DUE TO USER ALREADY EXISTING");
                            return false;
                        case ILLEGAL_TRANSACTION:
                            sender.sendMessage(ChatColor.DARK_RED + "FAILED TRANSACTION DUE TO THE TRANSACTION BEING ILLEGAL");
                            return false;
                        case INTERNAL_ERROR:
                            sender.sendMessage(ChatColor.DARK_RED + "FAILED TRANSACTION DUE TO INTERNAL ERROR");
                            return false;
                        case SUCCESS:
                            sender.sendMessage(MessageFormat.format("{0} Transferred {1} to {2}", ChatColor.GREEN, amount, player.getDisplayName()));
                            return true;
                    }
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
