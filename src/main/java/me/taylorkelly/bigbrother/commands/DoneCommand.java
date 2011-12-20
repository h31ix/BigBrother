package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public DoneCommand(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender send, Command arg1, String arg2, String[] arg3) {
        
        Player player = (Player) send;
        if (player.hasPermission(Permissions.INFO.id)) {
            plugin.sticker.removeLog(player);
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
