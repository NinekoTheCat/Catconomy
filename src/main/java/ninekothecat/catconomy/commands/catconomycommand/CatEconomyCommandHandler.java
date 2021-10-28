package ninekothecat.catconomy.commands.catconomycommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CatEconomyCommandHandler implements CommandExecutor {
    private final HashMap<String, CatEconomyCommand> commands = new HashMap<>();

    public HashMap<String, CatEconomyCommand> getCommands() {
        return commands;
    }

    public CatEconomyCommand get(String name) {
        return commands.get(name);
    }

    public void put(String name, CatEconomyCommand catEconomyCommand) {
        commands.put(name, catEconomyCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (commands.containsKey(args[0]) && commands.get(args[0]).hasExecutor()) {
                List<String> arg2 = new ArrayList<>(Arrays.asList(args));
                arg2.remove(0);
                String[] args3 = arg2.toArray(new String[args.length - 1]);
                return commands.get(args[0]).getExecutor().onCommand(sender, command, label, args3);
            }
        }
        return false;
    }
}
