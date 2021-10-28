package ninekothecat.catconomy.integrations;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import ninekothecat.catconomy.Catconomy;
import ninekothecat.catconomy.defaultImplementations.CatTransaction;
import ninekothecat.catconomy.enums.TransactionResult;
import ninekothecat.catconomy.enums.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CatVaultIntegration implements Economy {

    public static final Plugin PLUGIN = Bukkit.getPluginManager().getPlugin("Vault");

    @Override
    public boolean isEnabled() {
        return Catconomy.getBalanceHandler() != null;
    }

    @Override
    public String getName() {
        return Catconomy.getProvidingPlugin(Catconomy.class).getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return MessageFormat.format("{0}{1}", amount, Catconomy.prefix.getShortPrefix());
    }

    @Override
    public String currencyNamePlural() {
        return Catconomy.prefix.getShortPrefix();
    }

    @Override
    public String currencyNameSingular() {
        return Catconomy.prefix.getLongPrefix();
    }

    @Override
    public boolean hasAccount(String playerName) {
        Player player = Catconomy.getPlayerFromName(playerName);
        if (player != null) {
            return Catconomy.getBalanceHandler().userExists(player.getUniqueId());
        } else {
            return false;
        }

    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return Catconomy.getBalanceHandler().userExists(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return Catconomy.getBalanceHandler().getBalance(Objects.requireNonNull(Catconomy.getPlayerFromName(playerName)).getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return Catconomy.getBalanceHandler().getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return Catconomy.getBalanceHandler().getBalance(player.getUniqueId()) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        CatTransaction transaction = new CatTransaction(TransactionType.SUBTRACT_CURRENCY,
                true,
                amount,
                null,
                new ArrayList<>(Collections.singleton(Objects.requireNonNull(Catconomy.getPlayerFromName(playerName)).getUniqueId()))
                , String.format("Withdrawn %s from %s", amount, playerName),PLUGIN);
        TransactionResult result = Catconomy.getBalanceHandler().doTransaction(transaction);
        return new EconomyResponse(amount, getBalance(playerName), TransactionResult.toEconomyResponseType(result), result.toString());
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        CatTransaction transaction = new CatTransaction(TransactionType.SUBTRACT_CURRENCY,
                true,
                amount,
                null,
                new ArrayList<>(Collections.singleton(player.getUniqueId())),
                String.format("Withdrawn %s from Offline player %s",
                amount,
                player.getName()),PLUGIN);
        TransactionResult result = Catconomy.getBalanceHandler().doTransaction(transaction);
        return new EconomyResponse(amount, getBalance(player), TransactionResult.toEconomyResponseType(result), result.toString());
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Catconomy.getPlayerFromName(playerName);
        assert player != null;
        CatTransaction transaction = new CatTransaction(TransactionType.GIVE_CURRENCY,
                true,
                amount,
                null,
                new ArrayList<>(Collections.singleton(player.getUniqueId())),
                String.format("Deposited %s to %s", amount, playerName),
                PLUGIN);
        TransactionResult result = Catconomy.getBalanceHandler().doTransaction(transaction);
        return new EconomyResponse(amount, getBalance(player), TransactionResult.toEconomyResponseType(result), result.toString());
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        CatTransaction transaction = new CatTransaction(TransactionType.GIVE_CURRENCY,
                true,
                amount,
                null,
                new ArrayList<>(Collections.singleton(player.getUniqueId())),
                "Deposited " + amount + " to Offline Player " + player.getName(),
                PLUGIN
                );
        TransactionResult result = Catconomy.getBalanceHandler().doTransaction(transaction);
        return new EconomyResponse(amount, getBalance(player), TransactionResult.toEconomyResponseType(result), result.toString());
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        Player player = Catconomy.getPlayerFromName(playerName);
        assert player != null;
        CatTransaction transaction = new CatTransaction(TransactionType.CREATE_USER,
                true,
                0,
                null,
                new ArrayList<>(Collections.singleton(player.getUniqueId())),
                "Created Account for " + playerName + " With 0 currency",
                PLUGIN
        );
        TransactionResult result = Catconomy.getBalanceHandler().doTransaction(transaction);
        return result == TransactionResult.SUCCESS;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        CatTransaction transaction = new CatTransaction(TransactionType.CREATE_USER,
                true,
                0,
                null,
                new ArrayList<>(Collections.singleton(player.getUniqueId())),
                "Created Account for Offline player " + player.getName() + " With 0 currency",
                PLUGIN);
        TransactionResult result = Catconomy.getBalanceHandler().doTransaction(transaction);
        return result == TransactionResult.SUCCESS;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
