package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMoney(val plugin: Main) : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(sender is Player){
            val player = sender as Player
            /*
            if(args.size >= 2){
                if(args[0] == "give") try{
                    plugin.playersMoney.set(player, plugin.playersMoney[player]!! + args[1].toInt())
                }catch (e: Exception){
                    player.sendMessage("Veuillez rentrer un nombre entier valide")
                }
            }
             */
            player.sendMessage("Money: " + plugin.playersMoney[player])
        }
        return false
    }
}