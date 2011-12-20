package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnwatchedCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public UnwatchedCommand(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender player, Command arg1, String arg2, String[] split) {
        player.sendMessage(ChatColor.RED + "Watch/unwatch is not used anymore");
        return true;
    }
    
}
