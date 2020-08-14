package fr.niron.mineconomy

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.io.File

class MagasinListener(val list: MutableList<MarketItem>, val plugin: Main) : Listener {

    var quantity = mutableMapOf<Player, Int>()
    var actualMenu = mutableMapOf<Player, String>()
    var mode = mutableMapOf<Player, String>()
    var loaded = mutableMapOf<Player, Boolean>()

    @EventHandler
    fun onClick(event: InventoryClickEvent){
        val inv = event.view
        val realInv = event.inventory
        val player = event.whoClicked as Player
        val current = event.currentItem ?: return
        quantity.putIfAbsent(player, 1)
        actualMenu.putIfAbsent(player, "")
        mode.putIfAbsent(player, "")
        loaded.putIfAbsent(player, true)
        if(inv.title.equals("§8Magasin", true)){
            event.isCancelled = true
            if(current.hasItemMeta() && current.itemMeta?.displayName?.equals("§5Achat", true)!!){
                mode[player] = "achat"
                changeToMenu(realInv)
            }else if(current.hasItemMeta() && current.itemMeta?.displayName?.equals("§5Vente", true)!!){
                mode[player] = "vente"
                changeToMenu(realInv)
            }
            if(current.hasItemMeta()){
                if(current.itemMeta?.displayName?.equals("§5Mode")!!){
                    changeToMainMenu(realInv)
                    mode[player] = ""
                }else if(current.itemMeta?.displayName?.equals("§5Menu")!!){
                    changeToMenu(realInv)
                    actualMenu[player] = ""
                }else if(current.itemMeta?.displayName?.equals("§5Quantity")!!){
                    if(quantity[player] != 1)
                        quantity[player] = (quantity[player]!! + 16)%79
                    else
                        quantity[player] = quantity[player]!! + 15
                    changeToSpecMenu(realInv, actualMenu[player]!!, player)
                }else if(current.itemMeta?.displayName?.contains("§b")!!){
                    actualMenu[player] = current.itemMeta?.displayName!!
                    changeToSpecMenu(realInv, actualMenu[player]!!, player)
                }
            }
            if(current.hasItemMeta() && actualMenu[player] != "" && !current.itemMeta?.displayName?.contains("§")!!){
                if(mode[player] == "achat" && loaded[player]!!){
                    buyItem(current, player, quantity[player]!!)
                }else if(mode[player] == "vente" && loaded[player]!!){
                    sellItem(current, player, quantity[player]!!)
                }
            }
        }
    }

    @EventHandler
    fun onDrag(e: InventoryMoveItemEvent){
        val inv = e.destination
        val item = e.item
        if(inv.holder is Chest){
            val chest = inv.holder as Chest
            if(chest.hasMetadata("player")){
                if(!plugin.allPlayersMoney.containsKey(chest.getMetadata("player").first().asString()))
                    return
                sellItem(item, name = chest.getMetadata("player").first().asString(), quantity = item.amount, inv = inv)
            }
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent){
        val block = e.block
        val player = e.player
        if(block.hasMetadata("player")){
            val f = File(plugin.dataFolder, "sellingChest.yml")
            if(!f.exists())
                return
            val config = YamlConfiguration.loadConfiguration(f)
            config.getConfigurationSection(player.name) ?: return
            config.getConfigurationSection(player.name)!!.getKeys(false).forEach{
                val id = it
                if(config.getInt(player.name + ".$id.x") == block.x && config.getInt(player.name + ".$id.y") == block.y && config.getInt(player.name + ".$id.z") == block.z){
                    config.set(player.name + ".$id", null)
                    config.save(f)
                    block.removeMetadata("player", plugin)
                    player.sendMessage("§bCoffre de vente detruis")
                }
            }
        }
    }

//    @EventHandler
//    fun onMarketing(event: InventoryClickEvent){
//        val current = event.currentItem ?: return
//        val inv = event.view
//        val realInv = event.inventory
//        val player = event.whoClicked as Player
//        loaded.putIfAbsent(player, true)
//        quantity.putIfAbsent(player, 1)
//        actualMenu.putIfAbsent(player, "")
//        mode.putIfAbsent(player, "")
//
//        if(inv.title.equals("§8Magasin", true)){
//            event.isCancelled = true
//            if(current.hasItemMeta() && actualMenu[player] != "" && !current.itemMeta?.displayName?.contains("§")!!){
//                if(mode[player] == "achat" && loaded[player]!!){
//                    buyItem(current, player, quantity[player]!!)
//                }else if(mode[player] == "vente" && loaded[player]!!){
//                    sellItem(current, player, quantity[player]!!)
//                }
//            }
//        }
//    }

    fun changeToMenu(inv: Inventory){
        inv.clear();
        inv.setItem(0, createItem("§5Mode", Material.COMPASS))
        inv.setItem(18, createItem("§bTools", Material.IRON_AXE))
        inv.setItem(20, createItem("§bBlocks", Material.COBBLESTONE))
        inv.setItem(22, createItem("§bMinerals", Material.IRON_INGOT))
        inv.setItem(24, createItem("§bVegetals", Material.WHEAT))
        inv.setItem(26, createItem("§bAnimals", Material.BEEF))
    }

    fun changeToMainMenu(inv: Inventory){
        inv.clear()
        inv.setItem(21, createItem("§5Achat", Material.DIAMOND_BLOCK))
        inv.setItem(23, createItem("§5Vente", Material.EMERALD_BLOCK))
    }

    fun changeToSpecMenu(inv: Inventory, type: String, player: Player){
        loaded[player] = false
        inv.clear()
        inv.setItem(0, createItem("§5Menu", Material.COMPASS))
        inv.setItem(8, createItem("§5Quantity", Material.FEATHER, quantity[player]!!))
        loadMenu(inv, type, player)
    }

    fun loadMenu(inv: Inventory, type: String, player: Player){
        if(type == "")
            return
        var itemList: List<MarketItem> = list.filter { it.cat == "aucun"}
        if(type.contains("tools", true)){
            itemList = list.filter { it.cat == "tools" }
        }else if(type.contains("minerals", true)){
            itemList = list.filter { it.cat == "minerals" }
        }else if(type.contains("blocks", true)){
            itemList = list.filter { it.cat == "blocks" }
        }else if(type.contains("vegetals", true)){
            itemList = list.filter { it.cat == "vegetals" }
        }else if(type.contains("animals", true)){
            itemList = list.filter { it.cat == "animals" }
        }
        var i: Int = 9
        itemList.forEach {
            inv.setItem(i, it.getItemStack(quantity[player]!!, mode[player]!!))
            i++
        }
        player.closeInventory()
        player.openInventory(inv)
        loaded[player] = true
    }

    fun createItem(name: String, type: Material, nb: Int = 1): ItemStack {
        val it = ItemStack(type, nb)
        val itM = it.itemMeta
        itM?.setDisplayName(name)
        it.itemMeta = itM
        return it
    }

    fun buyItem(item: ItemStack, player: Player, quantity: Int){
        val marketItem = list.find { it.type == item.type }!!
        val price = marketItem.buyingPrice * quantity
        if(plugin.playersMoney[player]!! >= price){
            if(player.inventory.firstEmpty() != -1){
                player.inventory.addItem(ItemStack(marketItem.type, quantity))
                plugin.removeMoney(player, price.toDouble())
                player.sendMessage("Money: " + plugin.playersMoney[player])
            }else {
                player.sendMessage("Votre inventaire est plein !")
            }
        }else{
            player.sendMessage("Il vous manque " + (price - plugin.playersMoney[player]!!) + " pour effectuer cette opération")
        }
    }

    fun sellItem(item: ItemStack, player: Player? = null, quantity: Int, inv: Inventory? = player?.inventory, name: String? = player?.name){
        name ?: return
        inv ?: return
        val marketItem = list.find { it.type == item.type }
        marketItem ?: return
        val price = marketItem.sellingPrice * quantity
        if(inv.first(marketItem.type) != -1){
            if((inv.all(marketItem.type).values.fold(0) { acc, itemStack -> acc+itemStack.amount }) >= quantity){
                val stackIndex = mutableListOf<Int>()
                var total = 0
                val stackMap = inv.all(marketItem.type)
                for(el in stackMap){
                    stackIndex.add(el.key)
                    total += el.value.amount
                    if(total >= quantity){
                        break
                    }
                }
                if(stackIndex.size == 1){
                    inv.getItem(stackIndex.first())?.amount = inv.getItem(stackIndex.first())?.amount!! - quantity;
                    if(player != null)
                        plugin.addMoney(player, price.toDouble())
                    else
                        plugin.addMoney(name = name, amount = price.toDouble())
                }else{
                    var reste = quantity
                    for(index in stackIndex){
                        if(index != stackIndex.last()){
                            reste -= inv.getItem(index)?.amount!!
                            inv.setItem(index, ItemStack(Material.AIR))
                        }
                    }
                    val amount = inv.getItem(stackIndex.last())?.amount!! - reste
                    inv.getItem(stackIndex.last())?.amount = amount;
                    if(player != null)
                        plugin.addMoney(player, price.toDouble())
                    else
                        plugin.addMoney(name = name, amount = price.toDouble())
                }
                if(player != null && inv == player.inventory )
                    player.sendMessage("Money: " + plugin.playersMoney[player])
            }else{
                if(player != null && inv == player.inventory )
                    player.sendMessage("Vous n'avez pas assez d'exemplaire de cet item")
            }
        }else{
            if(player != null && inv == player.inventory )
                player.sendMessage("Vous ne posseder pas cet item")
        }
    }
}