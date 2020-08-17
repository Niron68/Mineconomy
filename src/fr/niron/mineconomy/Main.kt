package fr.niron.mineconomy

import fr.niron.mineconomy.commands.*
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
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
    private val itemList = mutableListOf<MarketItem>()
    val playersData = mutableMapOf<Player, YamlConfiguration>()
    val playersFile = mutableMapOf<Player, File>()
    val playersMoney = mutableMapOf<Player, Double>()

    override fun onEnable() {
        saveDefaultConfig()
        loadMoney()
        loadMarketItem()
        loadChest()
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            playersMoney.forEach {
                it.key.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("§lMoney: " + it.value))
            }
        }, 0, 20)
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
                val f = File(dataFolder, player!!.uniqueId.toString()+".yml")
                val config = YamlConfiguration.loadConfiguration(f)
                config.set("money", allPlayersMoney[player.name])
                config.save(f)
            }catch (e:Exception){}
        }
    }

    fun addMoney(player: Player? = null, amount: Double, name: String? = player?.name){
        name ?: return
        allPlayersMoney[name] = allPlayersMoney[name]!! + amount
        if(player != null){
            playersMoney[player] = playersMoney[player]!! + amount
        }else{
            val pl: Player = Bukkit.getPlayer(name) ?: return
            playersMoney[pl] = playersMoney[pl]!! + amount
            pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("§lMoney: " + playersMoney[pl]))
        }
    }

    fun removeMoney(player: Player? = null, amount: Double, name: String? = player?.name){
        name ?: return
        allPlayersMoney[name] = allPlayersMoney[name]!! - amount
        if(player != null){
            playersMoney[player] = playersMoney[player]!! - amount
        }else{
            val pl: Player = Bukkit.getPlayer(name) ?: return
            playersMoney[pl] = playersMoney[pl]!! - amount
            pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("§lMoney: " + playersMoney[pl]))
        }
    }

    private fun loadMoney(){
        Bukkit.getOfflinePlayers().forEach {
            val config: YamlConfiguration
            val f = File(dataFolder, it.uniqueId.toString()+".yml")
            config = YamlConfiguration.loadConfiguration(f)
            allPlayersMoney.putIfAbsent(it.name!!, config.getDouble("money"))
            println("load : " + it.name!! + " : " + config.getDouble("money").toString())
        }
    }

    private fun loadMarketItem(){
        val market = config.getConfigurationSection("market")?.getKeys(false)
        market?.forEach{ cat ->
            config.getConfigurationSection("market.$cat")?.getKeys(false)?.forEach {
                itemList.add(MarketItem(Material.valueOf(it.toUpperCase()), config.getInt("market.$cat.$it.sellingPrice"), cat))
            }
        }
    }

    private fun loadChest(){
        val fSelling = File(dataFolder, "sellingChest.yml")
        if(!fSelling.exists())
            fSelling.createNewFile()
        val configSelling = YamlConfiguration.loadConfiguration(fSelling)
        configSelling.getKeys(false).forEach {player ->
            configSelling.getConfigurationSection(player)?.getKeys(false)?.forEach {id ->
                val location = Location(server.getWorld("world"), configSelling.getInt("$player.$id.x").toDouble(), configSelling.getInt("$player.$id.y").toDouble(), configSelling.getInt("$player.$id.z").toDouble())
                server.getWorld("world")?.getBlockAt(location)?.setMetadata("player", FixedMetadataValue(this, player))
                server.getWorld("world")?.getBlockAt(location)?.setMetadata("id", FixedMetadataValue(this, id))
                println("Chest $id loaded")
            }
        }
    }

}