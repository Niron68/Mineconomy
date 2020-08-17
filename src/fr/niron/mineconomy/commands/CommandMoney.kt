package fr.niron.mineconomy.commands

import fr.niron.mineconomy.Main
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class CommandMoney(private val plugin: Main) : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(sender is Player){
            sender.sendMessage("Money: " + plugin.playersMoney[sender])
            sender.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("Money: " + plugin.playersMoney[sender]))
        }
        return false
    }
}