package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.io.File
import java.util.*

class CommandSelling(private val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if(sender is Player){
            if(sender.getTargetBlockExact(10)?.type == Material.CHEST){
                val chest = sender.getTargetBlockExact(10)?.state as Chest
                if(sender.getTargetBlockExact(10)?.hasMetadata("player")!!){
                    sender.sendMessage("§bCe coffre vend déjà pour " + chest.getMetadata("player").first().asString())
                }else{
                    chest.setMetadata("player", FixedMetadataValue(plugin, sender.name))
                    val f = File(plugin.dataFolder, "sellingChest.yml")
                    val id = Date().time
                    chest.setMetadata("id", FixedMetadataValue(plugin, id))
                    if(!f.exists()){
                        f.createNewFile()
                    }
                    val config = YamlConfiguration.loadConfiguration(f)
                    config.getConfigurationSection(sender.name) ?: config.createSection(sender.name)
//                    config.createSection(player.name + "." + id.toString())
//                    config.createSection(player.name + "." + id.toString() + ".x")
//                    config.createSection(player.name + "." + id.toString() + ".y")
//                    config.createSection(player.name + "." + id.toString() + ".z")
                    config.set(sender.name + "." + id + ".x", chest.location.blockX)
                    config.set(sender.name + "." + id + ".y", chest.location.blockY)
                    config.set(sender.name + "." + id + ".z", chest.location.blockZ)
                    config.save(f)
                    sender.sendMessage("§bCoffre transformé avec succés")
                }
            }
        }
        return false
    }

}