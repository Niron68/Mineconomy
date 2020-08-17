package fr.niron.mineconomy.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class CommandMagasin : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, msg: String, args: Array<out String>): Boolean {
        if(sender is Player){
            val inv: Inventory = Bukkit.createInventory(null, 54, "§8Magasin")
            inv.setItem(21, createItem("§5Achat", Material.DIAMOND_BLOCK))
            inv.setItem(23, createItem("§5Vente", Material.EMERALD_BLOCK))
            sender.openInventory(inv)
        }
        return false
    }

    private fun createItem(name: String, type: Material): ItemStack{
        val it = ItemStack(type, 1)
        val itM = it.itemMeta
        itM?.setDisplayName(name)
        it.itemMeta = itM
        return it
    }

}