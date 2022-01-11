package ninekothecat.catconomy.logging

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.ICatLogger
import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.interfaces.ITransaction
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager.Log4jMarker
import org.apache.logging.log4j.core.LoggerContext
import org.bukkit.plugin.Plugin
import java.util.*

class CatLogger : ICatLogger {
    private var logger: Logger? = null
    override fun success(transaction: ITransaction) {
        val marker: Marker = generateLog4J2Marker(transaction.plugin)
        logger!!.info(marker, transaction.message)
    }

    override fun fail(transaction: ITransaction, result: TransactionResult) {
        val marker: Marker = generateLog4J2Marker(transaction.plugin)
        logger!!.warn(
            marker,
            "Transaction with type ${transaction.transactionType} Failed because $result with " +
                    "amount ${transaction.amount} initiated by ${transaction.initiator} " +
                    "with users ${transaction.usersInvolved!!.toTypedArray().contentToString()}"
        )
    }

    override fun error(transaction: ITransaction, result: TransactionResult, exception: Exception?) {
        val marker: Marker = generateLog4J2Marker(transaction.plugin)
        logger!!.error(
            marker,
            "Transaction with type ${transaction.transactionType} failed because $result with " +
                    "amount ${transaction.amount} initiated by ${transaction.initiator} " +
                    "with users ${transaction.usersInvolved!!.toTypedArray().contentToString()}", exception
        )
    }

    private fun generateLog4J2Marker(plugin: Plugin): Log4jMarker =
        Log4jMarker("${plugin.name} V${plugin.description.version}")

    init {
        try {
            val context = LoggerContext("CCContext")
            context.configLocation =
                Objects.requireNonNull(Catconomy::class.java.classLoader.getResource("loggerSettings/log4j2.xml"))
                    .toURI()
            logger = context.getLogger("CCTransactionLogger")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}