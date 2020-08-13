package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

class CommandSelling(val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if(sender is Player){
            val player = sender as Player
            if(player.getTargetBlockExact(10)?.type == Material.CHEST){
                val chest = player.getTargetBlockExact(10)?.state as Chest
                chest.setMetadata("player", FixedMetadataValue(plugin, player.name))
            }
        }
        return false
    }

}