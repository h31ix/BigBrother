package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WatchedCommand implements CommandExecutor {
    public WatchedCommand(final BigBrother bigBrother) {
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        player.sendMessage(ChatColor.RED + "Watch/unwatch is not used anymore");
        return true;
    }
    
}
