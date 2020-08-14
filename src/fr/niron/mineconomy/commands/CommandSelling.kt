package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import java.io.File
import java.util.*

class CommandSelling(val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if(sender is Player){
            val player = sender as Player
            if(player.getTargetBlockExact(10)?.type == Material.CHEST){
                val chest = player.getTargetBlockExact(10)?.state as Chest
                if(player.getTargetBlockExact(10)?.hasMetadata("player")!!){
                    player.sendMessage("§bCe coffre vend déjà pour " + chest.getMetadata("player").first().asString())
                }else{
                    chest.setMetadata("player", FixedMetadataValue(plugin, player.name))
                    val f: File = File(plugin.dataFolder, "sellingChest.yml")
                    val id = Date().time
                    if(!f.exists()){
                        f.createNewFile()
                    }
                    val config = YamlConfiguration.loadConfiguration(f)
                    config.getConfigurationSection(player.name) ?: config.createSection(player.name)
                    config.createSection(id.toString())
                    config.createSection("x")
                    config.createSection("y")
                    config.createSection("z")
                    config.set(player.name + "." + id + ".x", chest.location.blockX)
                    config.set(player.name + "." + id + ".y", chest.location.blockY)
                    config.set(player.name + "." + id + ".z", chest.location.blockZ)
                    config.save(f)
                    player.sendMessage("§bCoffre transformé avec succés")
                }
            }
        }
        return false
    }

}