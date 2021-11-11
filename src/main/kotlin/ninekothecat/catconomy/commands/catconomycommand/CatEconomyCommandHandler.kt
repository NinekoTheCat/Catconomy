package ninekothecat.catconomy.commands.catconomycommand

import org.bukkit.ChatColor
import org.bukkit.command.*
import java.util.*

class CatEconomyCommandHandler : CommandExecutor {
    val commands = HashMap<String, CatEconomyCommand>()
    operator fun get(name: String): CatEconomyCommand? {
        return commands[name]
    }

    fun put(name: String, catEconomyCommand: CatEconomyCommand) {
        commands[name] = catEconomyCommand
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            if (commands.containsKey(args[0]) && commands[args[0]]!!.hasExecutor()) {
                val catEconomyCommand = commands[args[0]]
                val arg2: MutableList<String> = ArrayList(listOf(*args))
                arg2.removeAt(0)
                val args3 = arg2.toTypedArray()
                return if (sender.hasPermission(catEconomyCommand!!.permission)) {
                    catEconomyCommand.executor!!.onCommand(sender, command, label, args3)
                } else {
                    sender.sendMessage("${ChatColor.RED}you can't do that")
                    false
                }
            }
        }
        return false
    }
}