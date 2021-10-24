package ninekothecat.catconomy;

import net.milkbowl.vault.economy.Economy;
import ninekothecat.catconomy.commands.BalanceCommandExecutor;
import ninekothecat.catconomy.commands.BalanceTabAutocomplete;
import ninekothecat.catconomy.defaultImplementations.CatBalanceHandler;
import ninekothecat.catconomy.defaultImplementations.CatMapDBDatabase;
import ninekothecat.catconomy.defaultImplementations.CatPermissionGuard;
import ninekothecat.catconomy.defaultImplementations.CatPrefix;
import ninekothecat.catconomy.enums.DefaultDatabaseType;
import ninekothecat.catconomy.eventlisteners.CatPlayerJoinHandler;
import ninekothecat.catconomy.integrations.CatVaultIntegration;
import ninekothecat.catconomy.interfaces.IBalanceHandler;
import ninekothecat.catconomy.interfaces.ICurrencyPrefix;
import ninekothecat.catconomy.interfaces.IDatabase;
import ninekothecat.catconomy.interfaces.IPermissionGuard;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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
        this.getServer().getPluginManager().registerEvents(new CatPlayerJoinHandler(), this);

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
}
