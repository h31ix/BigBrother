package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Cleanser;
import me.taylorkelly.bigbrother.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CleanseCommand implements CommandExecutor {
    
    public CleanseCommand(final BigBrother plugin) {
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        if (player.hasPermission(Permissions.CLEANSE.id)) {
            if (Cleanser.needsCleaning()) {
                Cleanser.clean((Player) player);
            } else {
                player.sendMessage(ChatColor.RED + "No need to cleanse. Check your settings.");
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
