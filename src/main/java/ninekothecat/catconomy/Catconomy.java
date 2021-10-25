package ninekothecat.catconomy;

import net.milkbowl.vault.economy.Economy;
import ninekothecat.catconomy.commands.balance.BalanceCommandExecutor;
import ninekothecat.catconomy.commands.balance.BalanceTabAutocomplete;
import ninekothecat.catconomy.commands.deposit.DepositCommandExecutor;
import ninekothecat.catconomy.commands.give.GiveCommandExecutor;
import ninekothecat.catconomy.commands.take.TakeCommandExecutor;
import ninekothecat.catconomy.defaultImplementations.CatBalanceHandler;
import ninekothecat.catconomy.defaultImplementations.database.CatMapDBDatabase;
import ninekothecat.catconomy.defaultImplementations.CatPermissionGuard;
import ninekothecat.catconomy.defaultImplementations.CatPrefix;
import ninekothecat.catconomy.enums.DefaultDatabaseType;
import ninekothecat.catconomy.eventlisteners.CatPlayerJoinHandler;
import ninekothecat.catconomy.eventlisteners.CatPlayerLeaveHandler;
import ninekothecat.catconomy.integrations.CatVaultIntegration;
import ninekothecat.catconomy.interfaces.IBalanceHandler;
import ninekothecat.catconomy.interfaces.ICurrencyPrefix;
import ninekothecat.catconomy.interfaces.IDatabase;
import ninekothecat.catconomy.interfaces.IPermissionGuard;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public final class Catconomy extends JavaPlugin {
    public static final IPermissionGuard permissionGuard = new CatPermissionGuard();
    public static IDatabase database;
    public static ICurrencyPrefix prefix;
    private static IBalanceHandler balanceHandler = new CatBalanceHandler();

    public static IBalanceHandler getBalanceHandler() {
        return balanceHandler;
    }

    public static void setBalanceHandler(IBalanceHandler balanceHandler) {
        if (balanceHandler != null) {
            Catconomy.balanceHandler.saveAll();
        }
        Catconomy.balanceHandler = balanceHandler;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }

        setDatabase();
        setPrefix();

        Objects.requireNonNull(this.getCommand("balance")).setTabCompleter(new BalanceTabAutocomplete());
        Objects.requireNonNull(this.getCommand("balance")).setExecutor(new BalanceCommandExecutor());
        Objects.requireNonNull(this.getCommand("deposit")).setExecutor(new DepositCommandExecutor());
        Objects.requireNonNull(this.getCommand("give")).setExecutor(new GiveCommandExecutor());
        Objects.requireNonNull(this.getCommand("take")).setExecutor(new TakeCommandExecutor());
        this.getServer().getPluginManager().registerEvents(new CatPlayerJoinHandler(), this);
        this.getServer().getPluginManager().registerEvents(new CatPlayerLeaveHandler(),this);
        if (getServer().getPluginManager().getPlugin("Vault") != null){
            this.getLogger().info("Found vault plugin! Enabling");
            enableVaultIntegration();
        }
    }
    private void enableVaultIntegration(){
        this.getServer().getServicesManager().register(Economy.class,
                 new CatVaultIntegration(),
                this, ServicePriority.High);
        this.getLogger().info("Registered Vault Integration");
    }

    private void setPrefix() {
        if (this.getConfig().contains("Sprefix") && this.getConfig().contains("Lprefix")) {
            prefix = new CatPrefix(this.getConfig().getString("Sprefix"), this.getConfig().getString("Lprefix"));
        } else {
            prefix = new CatPrefix(this.getConfig().getString("Sprefix"));
        }
    }

    private void setDatabase() {
        try {
            if (DefaultDatabaseType.valueOf(this.getConfig().getString("database")) == DefaultDatabaseType.MapDBFile) {
                database = new CatMapDBDatabase();
            } else {
                throw new IllegalStateException("Unexpected value: " + DefaultDatabaseType.valueOf(this.getConfig().getString("database")));
            }
        } catch (IllegalArgumentException exception) {
            database = null;
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (balanceHandler != null) {
            balanceHandler.saveAll();
        }
    }

    @Nullable
    public static Player getPlayerFromName(String playerName) {
        Player player = null;
        for (Player player1 : Bukkit.getServer().getOnlinePlayers())
            if (player1.getDisplayName().toUpperCase(Locale.ROOT).equals(playerName.toUpperCase(Locale.ROOT)))
                player = player1;
            if (player == null){
                for (OfflinePlayer player1 : Bukkit.getServer().getOfflinePlayers())
                    if (Objects.requireNonNull(player1.getName()).toUpperCase(Locale.ROOT).equals(playerName.toUpperCase(Locale.ROOT)))
                        player = player1.getPlayer();
            }
        return player;
    }
}
