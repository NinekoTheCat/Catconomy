package ninekothecat.catconomy.logging

import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.interfaces.ICatLogger
import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.interfaces.ITransaction
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager.Log4jMarker
import org.apache.logging.log4j.core.LoggerContext
import java.text.MessageFormat
import java.util.*

class CatLogger : ICatLogger {
    private var logger: Logger? = null
    override fun success(transaction: ITransaction) {
        val marker: Marker = Log4jMarker(
            "${transaction.plugin.name} V${transaction.plugin.description.version}"
        )
        logger!!.info(marker, transaction.message)
    }

    override fun fail(transaction: ITransaction, result: TransactionResult) {
        val marker: Marker = Log4jMarker(
            "${transaction.plugin.name} V${transaction.plugin.description.version}"
        )
        logger!!.warn(
            marker,
            MessageFormat.format(
                "Transaction with type {0} Failed because {1} with amount {2} initiated by {3} with users {4}",
                transaction.transactionType.toString(), result.toString(), transaction.amount, transaction.initiator,
                transaction.usersInvolved!!.toTypedArray().contentToString()
            )
        )
    }

    override fun error(transaction: ITransaction, result: TransactionResult, exception: Exception?) {
        val marker: Marker = Log4jMarker(
            String.format(
                "%s V%s", transaction.plugin.name,
                transaction.plugin.description.version
            )
        )
        logger!!.error(
            marker,
            MessageFormat.format(
                "Transaction with type {0} failed because {1} with amount {2} initiated by {3} with users {4}",
                transaction.transactionType.toString(),
                result.toString(),
                transaction.amount,
                transaction.initiator,
                transaction.usersInvolved!!.toTypedArray().contentToString()
            ), exception
        )
    }

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