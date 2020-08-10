package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.NumberFormatException

class CommandPay(val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, p2: String, args: Array<out String>): Boolean {
        if(sender is Player){
            val player = sender as Player
            if(args.size == 2){
                if(Bukkit.getPlayer(args[0]) != null){
                    val dest: Player = Bukkit.getPlayer(args[0])!!
                    var amount: Double
                    try{
                        amount = args[1].toDouble()
                        plugin.removeMoney(player, amount)
                        plugin.addMoney(dest, amount)
                        player.sendMessage("Vous avez payer " + amount + " a " + dest.displayName)
                        dest.sendMessage("Vous avez recu " + amount + " de la part de " + player.displayName)
                    }catch (e: NumberFormatException){
                        player.sendMessage("Le montant est incorrect")
                    }
                }else{
                    player.sendMessage("Le joueur n'existe pas ou n'est pas connecter")
                }
            }else{
                player.sendMessage("La commande valide est /pay <joueur> <montant>")
            }
        }
        return false
    }

}