package ninekothecat.catconomy.commands.catconomycommand

import ninekothecat.catconomy.Catconomy
import org.bukkit.command.*
import java.util.*

class CatEconomyCommandHandlerAutoCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        return if (args.size == 1) {
            val strings: MutableSet<String> =
                HashSet<String>(Catconomy.catEconomyCommandHandler!!.commands.keys)
            strings.removeIf { name: String ->
                !sender.hasPermission(
                    Catconomy.catEconomyCommandHandler!!.commands[name]!!.permission
                )
            }
            ArrayList(strings)
        } else if (args.size > 1 && Catconomy.catEconomyCommandHandler!!.commands
                .containsKey(args[0]) && Catconomy.catEconomyCommandHandler!![args[0]]!!
                .hasAutoComplete()
        ) {
            val arg2: MutableList<String> = ArrayList(listOf(*args))
            arg2.removeAt(0)
            val args3 = arg2.toTypedArray()
            Catconomy.catEconomyCommandHandler!![args[0]]!!.autoComplete!!.onTabComplete(sender, command, alias, args3)
        } else {
            null
        }
    }
}