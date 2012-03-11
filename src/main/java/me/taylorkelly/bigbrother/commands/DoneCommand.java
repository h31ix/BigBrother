package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    
    public DoneCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender send, final Command arg1, final String arg2, final String[] arg3) {
        
        final Player player = (Player) send;
        if (player.hasPermission(Permissions.INFO.id)) {
            plugin.sticker.removeLog(player);
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
