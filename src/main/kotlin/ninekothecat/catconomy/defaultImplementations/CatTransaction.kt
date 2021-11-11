package ninekothecat.catconomy.defaultImplementations

import ninekothecat.catplugincore.money.enums.TransactionType
import ninekothecat.catplugincore.money.interfaces.ITransaction
import org.bukkit.plugin.Plugin
import java.util.*

class CatTransaction(
    override val transactionType: TransactionType,
    override val isConsole: Boolean,
    override val amount: Double,
    override val initiator: UUID?,
    override val usersInvolved: ArrayList<UUID>,
    override val message: String,
    override val plugin: Plugin
) : ITransaction