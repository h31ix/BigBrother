package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand implements CommandExecutor {
    
    public DeleteCommand(final BigBrother plugin) {
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        if (player.hasPermission(Permissions.ROLLBACK.id)) {
            if (split.length == 1) {
                if (RollbackConfirmation.hasRI((Player) player)) {
                    RollbackConfirmation.deleteRI((Player) player);
                    player.sendMessage(BigBrother.premessage + "You have deleted your rollback.");
                } else {
                    player.sendMessage(BigBrother.premessage + "You have no rollback to delete.");
                }
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb delete");
            }
            // Undo rollback.
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
