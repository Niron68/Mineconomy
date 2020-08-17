package fr.niron.mineconomy

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.io.IOException

class MoneyListener(private val plugin: Main) : Listener {

    @EventHandler
    fun onLogin(event: PlayerLoginEvent){
        val player = event.player
        val config: YamlConfiguration
        val f = File(plugin.dataFolder, player.uniqueId.toString()+".yml")
        if(f.exists()){
            config = YamlConfiguration.loadConfiguration(f)
            plugin.playersData[player] = config
            if(plugin.allPlayersMoney[player.name] != null){
                println("chargement de la money de " + player.name + " avec allMoney")
                plugin.playersMoney[player] = plugin.allPlayersMoney[player.name]!!
            }else{
                plugin.playersMoney[player] = config.getDouble("money")
            }
            plugin.playersFile[player] = f
            println(player.displayName + " data loaded")
        }else try{
            f.createNewFile()
            config = YamlConfiguration.loadConfiguration(f)
            config.createSection("money")
            config.set("money", 0)
            plugin.playersData[player] = config
            plugin.playersMoney[player] = config.getDouble("money")
            plugin.playersFile[player] = f
            println(player.displayName + " data created")
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    @EventHandler
    fun onLogout(event: PlayerQuitEvent){
        val player = event.player
        if(plugin.playersMoney[player] != null){
            plugin.playersData[player]?.set("money", plugin.playersMoney[player])
            plugin.playersData[player]?.save(plugin.playersFile[player]!!)
        }
        plugin.playersData.remove(player)
        plugin.playersFile.remove(player)
        plugin.playersMoney.remove(player)
    }

}