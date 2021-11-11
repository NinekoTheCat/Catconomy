package ninekothecat.catconomy.commands.give;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.defaultImplementations.CatTransaction;
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor;
import ninekothecat.catplugincore.money.enums.TransactionType;
import ninekothecat.catplugincore.utils.player.PlayerFinderKt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.*;

public class GiveCommandExecutor implements ICatEconomyCommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length == 2) {
            ArrayList<UUID> usersInvolved = new ArrayList<>(Collections.singleton(Objects.requireNonNull(PlayerFinderKt.getPlayerFromName(args[0])).getUniqueId()));
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
            if (amount <= 0) {
                return false;
            }
            Set<String> strings = new HashSet<>();
            for (UUID user:usersInvolved) {
                strings.add(Bukkit.getPlayer(user).getDisplayName());
            }
            CatTransaction transaction = new CatTransaction(TransactionType.GIVE_CURRENCY,
                    false,
                    amount,
                    ((Player) sender).getUniqueId(),
                    usersInvolved,
                    String.format("%s Gave %s to %s (%s)", ((Player) sender).getDisplayName(), amount, Arrays.toString(strings.toArray()),Arrays.toString(usersInvolved.toArray())),
                    Catconomy.getProvidingPlugin(Catconomy.class));
            switch (Catconomy.getBalanceHandler().doTransaction(transaction)) {
                case LACK_OF_PERMS:
                    sender.sendMessage(ChatColor.DARK_RED + "YOU ARE NOT AUTHORISED TO DO THIS");
                    return false;
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
                    sender.sendMessage(MessageFormat.format("{0}given {1} to {2}", ChatColor.GREEN, amount,
                            args[0]));
                    return true;
            }
        }

        return false;
    }
}
