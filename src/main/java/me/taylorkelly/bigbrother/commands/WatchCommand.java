package me.taylorkelly.bigbrother.commands;

import java.util.List;

import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WatchCommand implements CommandExecutor {
    private BigBrother plugin;
    
    public WatchCommand(BigBrother bigBrother) {
        plugin = bigBrother;
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2,
            String[] split) {
    	player.sendMessage(ChatColor.RED + "Watch/unwatch is not used anymore");
        
        return true;
    }
    
}
