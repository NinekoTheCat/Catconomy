package ninekothecat.catconomy.commands.take;

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
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class TakeCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }
        if (args.length == 2){
            ArrayList<UUID> usersInvolved = new ArrayList<>(Collections.singleton(Objects.requireNonNull(Catconomy.getPlayerFromName(args[0])).getUniqueId()));
            double amount = Double.parseDouble(args[1]);
            if (amount <= 0){
                return false;
            }
            CatTransaction transaction = new CatTransaction(TransactionType.SUBTRACT_CURRENCY,false, amount,((Player) sender).getUniqueId(), usersInvolved);
            switch (Catconomy.getBalanceHandler().doTransaction(transaction)){
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
                    sender.sendMessage(MessageFormat.format("{0}taken {1} from {2}", ChatColor.GREEN, amount,
                            args[0]));
                    return true;
            }
        }

        return false;
    }
}
