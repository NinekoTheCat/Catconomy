package ninekothecat.catconomy.defaultImplementations.database.SQL;

import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.interfaces.IDatabase;

import java.sql.*;
import java.util.*;

public class CatSQLDatabase implements IDatabase {
    private Connection conn = null;

    public CatSQLDatabase(String user, String password, String host, String databaseName, String port) throws SQLException {
        try {
            //Load Drivers
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Properties properties = new Properties();
            properties.put("user", user);
            properties.put("password", password);
            properties.put("autoReconnect", "true");
            properties.put("useSSL", "false");
            properties.put("verifyServerCertificate", "false");
            //Connect to database
            String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
            conn = DriverManager.getConnection(url, properties);

        } catch (ClassNotFoundException e) {
            Catconomy.logger.severe("Could not locate drivers for mysql! Error: " + e.getMessage());
            return;
        } catch (SQLException e) {
            Catconomy.logger.severe("Could not connect to mysql database! Error: " + e.getMessage());
            return;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String data = "CREATE TABLE IF NOT EXISTS ACCOUNTS"
                + "  ("
                + "   CURRENCY            DOUBLE,"
                + "   UUID     VARCHAR(36))";
        PreparedStatement preparedStatement = conn.prepareStatement(data);
        preparedStatement.execute();
    }

    @Override
    public boolean userExists(UUID user) {
        String query = "SELECT * FROM ACCOUNTS WHERE UUID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, user.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    @Override
    public void removeUser(UUID user) {
        String query = "DELETE FROM ACCOUNTS WHERE UUID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, user.toString());
            statement.execute();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void setUserBalance(UUID user, double balance) {
        if (userExists(user)) {
            String query = "UPDATE ACCOUNTS SET CURRENCY = ? WHERE UUID = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setDouble(1, balance);
                statement.setString(2, user.toString());
                statement.execute();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else {
            String query = "INSERT INTO ACCOUNTS (CURRENCY, UUID) " +
                    "VALUES (?, ?);";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setDouble(1, balance);
                statement.setString(2, user.toString());
                statement.execute();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }


    }

    @Override
    public double getUserBalance(UUID user) {
        String query = "SELECT DISTINCT CURRENCY FROM ACCOUNTS WHERE UUID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, user.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getDouble("CURRENCY");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public void setUsersBalance(Map<UUID, Double> userBalances) {
        userBalances.forEach(this::setUserBalance);
    }

    @Override
    public Map<UUID, Double> getUsersBalance(Collection<UUID> users) {
        Map<UUID, Double> uuidDoubleMap = new HashMap<>();
        users.forEach(uuid -> {
            String query = "SELECT CURRENCY FROM ACCOUNTS WHERE UUID = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();
                set.next();
                uuidDoubleMap.put(uuid, set.getDouble("CURRENCY"));
            } catch (SQLException se) {
                Catconomy.logger.warning(se.getMessage());
            }
        });
        return uuidDoubleMap;
    }
}
