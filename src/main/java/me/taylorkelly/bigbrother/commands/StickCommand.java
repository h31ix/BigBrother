package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StickCommand implements CommandExecutor {
    
    public StickCommand(BigBrother plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender player, Command arg1, String arg2,
            String[] split) {
        
        player.sendMessage(BigBrother.premessage + "/bb stick is no longer used.  Please use /bb log.");
        
        return true;
    }
    
}
