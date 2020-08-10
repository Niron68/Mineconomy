package fr.niron.mineconomy

import fr.niron.mineconomy.commands.CommandBalanceTop
import fr.niron.mineconomy.commands.CommandMagasin
import fr.niron.mineconomy.commands.CommandMoney
import fr.niron.mineconomy.commands.CommandPay
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {

    val allPlayersMoney = mutableMapOf<String, Double>()
    val itemList = mutableListOf<MarketItem>()
    val playersData = mutableMapOf<Player, YamlConfiguration>()
    val playersFile = mutableMapOf<Player, File>()
    val playersMoney = mutableMapOf<Player, Double>()

    override fun onEnable() {
        saveDefaultConfig()
        print("Coucou je suis le plugin qui marche peut etre")
        Bukkit.getOfflinePlayers().forEach {
            var config: YamlConfiguration
            val f: File = File(dataFolder, it.uniqueId.toString()+".yml")
            config = YamlConfiguration.loadConfiguration(f)
            allPlayersMoney.put(it.name!!, config.getDouble("money"))
        }
        var market = config.getConfigurationSection("market")?.getKeys(false)
        market?.forEach{
            var cat = it
            config.getConfigurationSection("market.$cat")?.getKeys(false)?.forEach {
                itemList.add(MarketItem(Material.valueOf(it.toUpperCase()), config.getInt("market.$cat.$it.sellingPrice"), cat))
            }
        }
        getCommand("magasin")?.setExecutor(CommandMagasin())
        getCommand("money")?.setExecutor(CommandMoney(this))
        getCommand("pay")?.setExecutor(CommandPay(this))
        getCommand("balancetop")?.setExecutor(CommandBalanceTop(this))
        server.pluginManager.registerEvents(MagasinListener(itemList, this), this)
        server.pluginManager.registerEvents(MoneyListener(this), this)
    }

    override fun onDisable() {
        for(el in playersData){
            val player = el.key
            if(playersMoney[player] != null){
                playersData[player]?.set("money", playersMoney[player])
                playersData[player]?.save(playersFile[player]!!)
            }
            playersData.remove(player)
            playersFile.remove(player)
            playersMoney.remove(player)
        }
    }

    fun addMoney(player: Player, amount: Double){
        playersMoney.set(player, playersMoney[player]!! + amount)
        allPlayersMoney.set(player.name, playersMoney[player]!!)
    }

    fun removeMoney(player: Player, amount: Double){
        playersMoney.set(player, playersMoney[player]!! - amount)
        allPlayersMoney.set(player.name, playersMoney[player]!!)
    }

}