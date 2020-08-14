package fr.niron.mineconomy

import fr.niron.mineconomy.commands.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Exception

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
            allPlayersMoney.putIfAbsent(it.name!!, config.getDouble("money"))
            println("load : " + it.name!! + " : " + config.getDouble("money").toString())
        }
        var market = config.getConfigurationSection("market")?.getKeys(false)
        market?.forEach{
            var cat = it
            config.getConfigurationSection("market.$cat")?.getKeys(false)?.forEach {
                itemList.add(MarketItem(Material.valueOf(it.toUpperCase()), config.getInt("market.$cat.$it.sellingPrice"), cat))
            }
        }
        val fSelling = File(dataFolder, "sellingChest.yml")
        if(!fSelling.exists())
            fSelling.createNewFile()
        val configSelling = YamlConfiguration.loadConfiguration(fSelling)
        configSelling.getKeys(false).forEach {
            val player = it
            configSelling.getConfigurationSection(player)?.getKeys(false)?.forEach {
                val id = it
                val location = Location(server.getWorld("overworld"), configSelling.getInt("$player.$id.x").toDouble(), configSelling.getInt("$player.$id.y").toDouble(), configSelling.getInt("$player.$id.z").toDouble())
                server.getWorld("overworld")?.getBlockAt(location)?.setMetadata("player", FixedMetadataValue(this, player))
            }
        }
        getCommand("magasin")?.setExecutor(CommandMagasin())
        getCommand("money")?.setExecutor(CommandMoney(this))
        getCommand("pay")?.setExecutor(CommandPay(this))
        getCommand("balancetop")?.setExecutor(CommandBalanceTop(this))
        getCommand("selling")?.setExecutor(CommandSelling(this))
        server.pluginManager.registerEvents(MagasinListener(itemList, this), this)
        server.pluginManager.registerEvents(MoneyListener(this), this)
    }

    override fun onDisable() {
//        for(el in playersData){
//            try{
//                val player = el.key
//                if(playersMoney[player] != null){
//                    playersData[player]?.set("money", playersMoney[player])
//                    playersData[player]?.save(playersFile[player]!!)
//                }
//                playersData.remove(player)
//                playersFile.remove(player)
//                playersMoney.remove(player)
//            }catch (e: Exception){
//
//            }
//
//        }
        val players = Bukkit.getOfflinePlayers()
        for(el in allPlayersMoney){
            try{
                val player = players.find { it.name == el.key }
                val f: File = File(dataFolder, player!!.uniqueId.toString()+".yml")
                val config = YamlConfiguration.loadConfiguration(f)
                config.set("money", allPlayersMoney[player.name])
                config.save(f)
            }catch (e:Exception){}
        }
    }

    fun addMoney(player: Player? = null, amount: Double, name: String? = player?.name){
        name ?: return
        allPlayersMoney.set(name, allPlayersMoney[name]!! + amount)
        if(player != null){
            playersMoney.set(player, playersMoney[player]!! + amount)
        }else{
            val pl: Player = Bukkit.getPlayer(name) ?: return
            playersMoney.set(pl, playersMoney[pl]!! + amount)
        }
    }

    fun removeMoney(player: Player? = null, amount: Double, name: String? = player?.name){
        name ?: return
        allPlayersMoney.set(name, allPlayersMoney[name]!! - amount)
        if(player != null){
            playersMoney.set(player, playersMoney[player]!! - amount)
        }else{
            val pl: Player = Bukkit.getPlayer(name) ?: return
            playersMoney.set(pl, playersMoney[pl]!! - amount)
        }
    }

}