package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    
    public LogCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender send, final Command arg1, final String arg2, final String[] split) {
        final Player player = (Player) send;
        if (player.hasPermission(Permissions.INFO.id)) {
            if (split.length == 1) {
                plugin.sticker.giveLog(player);
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb log");
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
