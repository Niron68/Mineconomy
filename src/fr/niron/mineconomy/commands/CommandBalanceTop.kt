package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBalanceTop(private val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        var count = 1
        var message = ""
        val sortedMap = plugin.allPlayersMoney.toList().sortedBy { (_, value) -> -value }.toMap()
        sortedMap.forEach{
            if(count <= 10){
                message += "§a" + count + ". §b" + it.key + ": §5" + it.value + "\n"
                count++
            }
        }
        if(sender is Player){
            sender.sendMessage(message)
        }else{
            println(message)
        }
        return false
    }

}