package ninekothecat.catconomy.logging;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.enums.TransactionResult;
import ninekothecat.catconomy.interfaces.ICatLogger;
import ninekothecat.catconomy.interfaces.ITransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.w3c.dom.DOMConfiguration;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public class CatLogger implements ICatLogger {
    private Logger logger;

    public CatLogger() {
        try {
            LoggerContext context = new LoggerContext("CCContext");

            context.setConfigLocation(Objects.requireNonNull(Catconomy.class.getClassLoader().getResource("loggerSettings/log4j2.xml")).toURI());
            logger = context.getLogger("CCTransactionLogger");
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    public void success(ITransaction transaction) {
        Marker marker = new MarkerManager.Log4jMarker(
                String.format("%s V%s", transaction.getPlugin().getName(),
                        transaction.getPlugin().getDescription().getVersion()));
        logger.info(marker, transaction.getMessage());
    }

    @Override
    public void fail(ITransaction transaction, TransactionResult result) {
        Marker marker = new MarkerManager.Log4jMarker(
                String.format("%s V%s", transaction.getPlugin().getName(),
                        transaction.getPlugin().getDescription().getVersion()));
        logger.warn(marker,
                MessageFormat.format("Transaction with type {0} Failed because {1} with amount {2} initiated by {3} with users {4}",
                transaction.getTransactionType().toString(), result.toString(), transaction.getAmount(), transaction.getInitiator(),
                Arrays.toString(transaction.getUsersInvolved().toArray())));
    }
    public void error(ITransaction transaction,TransactionResult result, Exception exception){
        Marker marker = new MarkerManager.Log4jMarker(
                String.format("%s V%s", transaction.getPlugin().getName(),
                        transaction.getPlugin().getDescription().getVersion()));
        logger.error(marker,
                MessageFormat.format("Transaction with type {0} failed because {1} with amount {2} initiated by {3} with users {4}",
                        transaction.getTransactionType().toString(),
                        result.toString(),
                        transaction.getAmount(),
                        transaction.getInitiator(),
                        Arrays.toString(transaction.getUsersInvolved().toArray())
                        ),exception);

    }
}
