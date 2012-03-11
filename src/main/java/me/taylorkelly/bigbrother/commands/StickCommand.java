package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StickCommand implements CommandExecutor {
    
    public StickCommand(final BigBrother plugin) {
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        
        player.sendMessage(BigBrother.premessage + "/bb stick is no longer used.  Please use /bb log.");
        
        return true;
    }
    
}
