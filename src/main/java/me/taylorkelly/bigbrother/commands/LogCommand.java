package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public LogCommand(BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender send, Command arg1, String arg2, String[] split) {
        Player player = (Player) send;
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
