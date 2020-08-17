package fr.niron.mineconomy

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MarketItem(val type: Material, val sellingPrice: Int, val cat: String) {

    val buyingPrice: Int
        get() { return sellingPrice*2 }


    fun getItemStack(quantity: Int, mode: String): ItemStack{
        val it = ItemStack(type, quantity)
        val itM = it.itemMeta!!
        val lore = mutableListOf<String>()
        if(mode == "achat"){
            lore.add("§5Acheter")
            lore.add("§5Prix: " + quantity*buyingPrice)
        }else{
            lore.add("§5Vendre")
            lore.add("§5Prix: " + quantity*sellingPrice)
        }
        itM.lore = lore
        it.itemMeta = itM
        return it
    }

}