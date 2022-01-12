package ninekothecat.catconomy

import net.milkbowl.vault.economy.Economy
import ninekothecat.catconomy.commands.balance.BalanceCommandExecutor
import ninekothecat.catconomy.commands.balance.BalanceTabAutocomplete
import ninekothecat.catconomy.commands.catconomycommand.CatEconomyCommand
import ninekothecat.catconomy.commands.catconomycommand.CatEconomyCommandHandler
import ninekothecat.catconomy.commands.catconomycommand.CatEconomyCommandHandlerAutoCompleter
import ninekothecat.catconomy.commands.deposit.DepositCommandExecutor
import ninekothecat.catconomy.commands.give.GiveCommandExecutor
import ninekothecat.catconomy.commands.info.InfoCommandExecutor
import ninekothecat.catconomy.commands.take.TakeCommandExecutor
import ninekothecat.catconomy.defaultImplementations.CatBalanceHandler
import ninekothecat.catconomy.defaultImplementations.CatPermissionGuard
import ninekothecat.catconomy.defaultImplementations.CatPrefix
import ninekothecat.catconomy.defaultImplementations.database.CatMapDBDatabase
import ninekothecat.catconomy.defaultImplementations.database.SQL.CatSQLDatabase
import ninekothecat.catconomy.enums.DefaultDatabaseType
import ninekothecat.catconomy.eventlisteners.CatPlayerJoinHandler
import ninekothecat.catconomy.integrations.CatVaultIntegration
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor
import ninekothecat.catconomy.interfaces.ICatLogger
import ninekothecat.catconomy.interfaces.IDatabase
import ninekothecat.catconomy.interfaces.IPermissionGuard
import ninekothecat.catconomy.logging.CatLogger
import ninekothecat.catplugincore.exceptions.CatServiceLoadException
import ninekothecat.catplugincore.money.interfaces.IBalanceHandler
import ninekothecat.catplugincore.money.interfaces.ICurrencyPrefix
import ninekothecat.catplugincore.utils.config.loadConfigurationFromDataFolder
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.permissions.Permission
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.sql.SQLException
import java.util.*
import java.util.logging.Logger
import kotlin.reflect.KClass

class Catconomy : JavaPlugin() {
    override fun onLoad() {
        vaultActive = false
        if (server.pluginManager.getPlugin("Vault") != null) {
            this.logger.info("Found vault plugin! Enabling")
            enableVaultIntegration()
        }
        setDatabase()
        setPrefix()
        val servicesManager = server.servicesManager
        servicesManager.register(
            IBalanceHandler::class.java,
            CatBalanceHandler(this.config.getBoolean("do_logs", true)),
            this,
            ServicePriority.Low
        )
        servicesManager.register(IPermissionGuard::class.java, CatPermissionGuard(), this, ServicePriority.Low)
        servicesManager.register(ICatLogger::class.java, CatLogger(), this, ServicePriority.Low)
    }

    override fun onEnable() {
        //Bstats
        val pluginID = 13186
        Metrics(this, pluginID)
        // Plugin startup logic
        Companion.logger = this.logger
        this.logger.info("Loading config.yml ...")
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            saveDefaultConfig()
        }
        startingAmount = this.config.getDouble("starting_amount", 1000.0)
        this.logger.info("Loading services...")
        if (loadServices()) return
        catEconomyCommandHandler = CatEconomyCommandHandler()
        val catPlayerJoinHandler = CatPlayerJoinHandler(startingAmount)
        registerBukkitCommands()
        registerCatConomyCommands()
        server.pluginManager.registerEvents(catPlayerJoinHandler, this)
    }

    private fun registerCatConomyCommands() {
        makeCatConomyCommandWithExecutor(
            "give",
            Bukkit.getPluginManager().getPermission("catconomy.give"),
            GiveCommandExecutor()
        )
        makeCatConomyCommandWithExecutor(
            "take",
            Bukkit.getPluginManager().getPermission("catconomy.subtract"),
            TakeCommandExecutor()
        )
        makeCatConomyCommandWithExecutor(
            "info",
            Bukkit.getPluginManager().getPermission("catconomy.info"),
            InfoCommandExecutor()
        )
    }

    private fun makeCatConomyCommandWithExecutor(
        name: String,
        permission: Permission,
        executor: ICatEconomyCommandExecutor
    ) {
        makeCatConomyCommand(name, permission)
        catEconomyCommandHandler!![name]!!.executor = executor
    }

    private fun registerBukkitCommands() {
        Objects.requireNonNull(getCommand("balance")).tabCompleter = BalanceTabAutocomplete()
        Objects.requireNonNull(getCommand("balance")).executor = BalanceCommandExecutor()
        Objects.requireNonNull(getCommand("deposit")).executor = DepositCommandExecutor()
        Objects.requireNonNull(getCommand("catconomy")).executor = catEconomyCommandHandler
        Objects.requireNonNull(getCommand("catconomy")).tabCompleter =
            CatEconomyCommandHandlerAutoCompleter()
    }


    @Throws(CatServiceLoadException::class)
    private fun <T : Any> loadService(clazz: KClass<T>): T =
        server.servicesManager.load(clazz.java) ?: throw CatServiceLoadException(clazz.java)

    private fun loadServices(): Boolean {
        try {
            balanceHandler = loadService(IBalanceHandler::class)
            iCatLogger = loadService(ICatLogger::class)
            permissionGuard = loadService(IPermissionGuard::class)
            database = loadService(IDatabase::class)
            prefix = loadService(ICurrencyPrefix::class)
        } catch (catServiceLoadException: CatServiceLoadException) {
            this.logger.severe(catServiceLoadException.stackTraceToString())
            server.pluginManager.disablePlugin(this)
            return true
        }

        this.logger.info("Loaded all services!")
        return false
    }

    private fun enableVaultIntegration() {
        server.servicesManager.register(
            Economy::class.java,
            CatVaultIntegration(),
            this, ServicePriority.High
        )
        this.logger.info("Registered Vault Integration")
        vaultActive = true
    }

    private fun setPrefix() {
        val servicesManager = server.servicesManager
        if (this.config.contains("Sprefix") && this.config.contains("Lprefix")) {
            servicesManager.register(
                ICurrencyPrefix::class.java,
                CatPrefix(this.config.getString("Sprefix"), this.config.getString("Lprefix")),
                this,
                ServicePriority.Low
            )
        } else {
            servicesManager.register(
                ICurrencyPrefix::class.java,
                CatPrefix(this.config.getString("Sprefix")),
                this,
                ServicePriority.Low
            )
        }
    }

    private fun setDatabase() {
        try {
            when (DefaultDatabaseType.valueOf(this.config.getString("database"))) {
                DefaultDatabaseType.MapDBFile -> server.servicesManager.register(
                    IDatabase::class.java, CatMapDBDatabase(), this, ServicePriority.Low
                )
                DefaultDatabaseType.SQL -> {
                    val yamlConfiguration = loadConfigurationFromDataFolder("Sql.yml", this)
                    val catSQLDatabase = CatSQLDatabase(
                        yamlConfiguration.getString("user"),
                        yamlConfiguration.getString("password"),
                        yamlConfiguration.getString("host"),
                        yamlConfiguration.getString("database_name"),
                        yamlConfiguration.getString("port")
                    )
                    server.servicesManager.register(IDatabase::class.java, catSQLDatabase, this, ServicePriority.Low)
                }
            }
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: SQLException) {
        }
    }

    private fun makeCatConomyCommand(name: String, permission: Permission) {
        val catEconomyCommand = CatEconomyCommand()
        catEconomyCommand.permission = permission
        catEconomyCommandHandler!!.put(name, catEconomyCommand)
    }

    companion object {
        var startingAmount : Double= 1000.0
        var permissionGuard: IPermissionGuard? = null
        var database: IDatabase? = null
        var logger: Logger? = null
        var prefix: ICurrencyPrefix? = null
        var catEconomyCommandHandler: CatEconomyCommandHandler? = null
        private var balanceHandler: IBalanceHandler? = null
        var iCatLogger: ICatLogger? = null
        var vaultActive = false
        fun getBalanceHandler(): IBalanceHandler? {
            return balanceHandler
        }
    }

    override fun onDisable() {
        if (CatMapDBDatabase.db != null){
            CatMapDBDatabase.db!!.close()
            CatMapDBDatabase.db = null
        }
        setDefaults()
        super.onDisable()
    }
    private fun setDefaults(){
         startingAmount = 1000.0
         permissionGuard = null
         database = null
         prefix = null
         catEconomyCommandHandler= null
        balanceHandler = null
         iCatLogger =  null
         vaultActive = false
    }
}