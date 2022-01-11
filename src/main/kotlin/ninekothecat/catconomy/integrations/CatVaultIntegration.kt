package ninekothecat.catconomy.integrations

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import ninekothecat.catconomy.Catconomy
import ninekothecat.catconomy.defaultImplementations.CatTransaction
import ninekothecat.catplugincore.money.enums.TransactionResult
import ninekothecat.catplugincore.money.enums.TransactionResult.Companion.toEconomyResponseType
import ninekothecat.catplugincore.money.enums.TransactionType
import ninekothecat.catplugincore.utils.player.getPlayerFromName
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class CatVaultIntegration : Economy {
    override fun isEnabled(): Boolean = Catconomy.getBalanceHandler() != null

    override fun getName(): String = JavaPlugin.getProvidingPlugin(Catconomy::class.java).name

    override fun hasBankSupport(): Boolean = false

    override fun fractionalDigits(): Int = -1


    override fun format(amount: Double): String = "$amount${Catconomy.prefix!!.shortPrefix}"

    override fun currencyNamePlural(): String = Catconomy.prefix!!.shortPrefix

    override fun currencyNameSingular(): String = Catconomy.prefix!!.longPrefix

    override fun hasAccount(playerName: String): Boolean {
        val player = getPlayerFromName(playerName)
        return if (player != null) {
            Catconomy.getBalanceHandler()!!.userExists(player.uniqueId)
        } else {
            false
        }
    }

    override fun hasAccount(player: OfflinePlayer): Boolean =
        Catconomy.getBalanceHandler()!!.userExists(player.uniqueId)

    override fun hasAccount(playerName: String, worldName: String): Boolean = hasAccount(playerName)

    override fun hasAccount(player: OfflinePlayer, worldName: String): Boolean = hasAccount(player)

    override fun getBalance(playerName: String): Double = Catconomy.getBalanceHandler()!!
        .getBalance(Objects.requireNonNull(getPlayerFromName(playerName))!!.uniqueId)

    override fun getBalance(player: OfflinePlayer): Double =
        Catconomy.getBalanceHandler()!!.getBalance(player.uniqueId)

    override fun getBalance(playerName: String, world: String): Double = getBalance(playerName)

    override fun getBalance(player: OfflinePlayer, world: String): Double = getBalance(player)

    override fun has(playerName: String, amount: Double): Boolean = getBalance(playerName) >= amount

    override fun has(player: OfflinePlayer, amount: Double): Boolean =
        Catconomy.getBalanceHandler()!!.getBalance(player.uniqueId) >= amount

    override fun has(playerName: String, worldName: String, amount: Double): Boolean = has(playerName, amount)

    override fun has(player: OfflinePlayer, worldName: String, amount: Double): Boolean = has(player, amount)

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val transaction = CatTransaction(
            TransactionType.SUBTRACT_CURRENCY,
            true,
            amount,
            null,
            ArrayList(setOf(Objects.requireNonNull(getPlayerFromName(playerName))!!.uniqueId)),
            String.format("Withdrawn %s from %s", amount, playerName),
            PLUGIN
        )
        val result: TransactionResult = Catconomy.getBalanceHandler()!!
            .doTransaction(transaction)
        return EconomyResponse(amount, getBalance(playerName), toEconomyResponseType(result), result.toString())
    }

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val transaction = CatTransaction(
            TransactionType.SUBTRACT_CURRENCY,
            true,
            amount,
            null,
            ArrayList(setOf(player.uniqueId)), String.format(
                "Withdrawn %s from Offline player %s",
                amount,
                player.name
            ), PLUGIN
        )
        val result: TransactionResult = Catconomy.getBalanceHandler()!!
            .doTransaction(transaction)
        return EconomyResponse(amount, getBalance(player), toEconomyResponseType(result), result.toString())
    }

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse =
        withdrawPlayer(playerName, amount)

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse =
        withdrawPlayer(player, amount)

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = getPlayerFromName(playerName)!!
        val transaction = CatTransaction(
            TransactionType.GIVE_CURRENCY,
            true,
            amount,
            null,
            ArrayList(setOf(player.uniqueId)), String.format("Deposited %s to %s", amount, playerName),
            PLUGIN
        )
        val result: TransactionResult = Catconomy.getBalanceHandler()!!
            .doTransaction(transaction)
        return EconomyResponse(amount, getBalance(player), toEconomyResponseType(result), result.toString())
    }

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val transaction = CatTransaction(
            TransactionType.GIVE_CURRENCY,
            true,
            amount,
            null,
            ArrayList(setOf(player.uniqueId)),
            "Deposited " + amount + " to Offline Player " + player.name,
            PLUGIN
        )
        val result: TransactionResult = Catconomy.getBalanceHandler()!!
            .doTransaction(transaction)
        return EconomyResponse(amount, getBalance(player), toEconomyResponseType(result), result.toString())
    }

    override fun depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse =
        depositPlayer(playerName, amount)

    override fun depositPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse =
        depositPlayer(player, amount)

    override fun createBank(name: String, player: String): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")
    }

    override fun createBank(name: String, player: OfflinePlayer): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")
    }

    override fun deleteBank(name: String): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")
    }

    override fun bankBalance(name: String): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")
    }

    override fun bankHas(name: String, amount: Double): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun bankWithdraw(name: String, amount: Double): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun bankDeposit(name: String, amount: Double): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun isBankOwner(name: String, playerName: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun isBankOwner(name: String, player: OfflinePlayer): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun isBankMember(name: String, playerName: String): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun isBankMember(name: String, player: OfflinePlayer): EconomyResponse =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BANKS NOT SUPPORTED")

    override fun getBanks(): List<String>? = null

    override fun createPlayerAccount(playerName: String): Boolean {
        val player = getPlayerFromName(playerName)!!
        val transaction = CatTransaction(
            TransactionType.CREATE_USER,
            true,
            0.0,
            null,
            ArrayList(setOf(player.uniqueId)),
            "Created Account for $playerName With 0 currency",
            PLUGIN
        )
        val result: TransactionResult = Catconomy.getBalanceHandler()!!
            .doTransaction(transaction)
        return result == TransactionResult.SUCCESS
    }

    override fun createPlayerAccount(player: OfflinePlayer): Boolean {
        val transaction = CatTransaction(
            TransactionType.CREATE_USER,
            true,
            0.0,
            null,
            ArrayList(setOf(player.uniqueId)),
            "Created Account for Offline player " + player.name + " With 0 currency",
            PLUGIN
        )
        val result: TransactionResult = Catconomy.getBalanceHandler()!!
            .doTransaction(transaction)
        return result == TransactionResult.SUCCESS
    }

    override fun createPlayerAccount(playerName: String, worldName: String): Boolean = createPlayerAccount(playerName)

    override fun createPlayerAccount(player: OfflinePlayer, worldName: String): Boolean = createPlayerAccount(player)

    companion object {
        val PLUGIN: Plugin = Bukkit.getPluginManager().getPlugin("Vault")
    }
}