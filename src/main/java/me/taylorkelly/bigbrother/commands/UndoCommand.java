package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;
import me.taylorkelly.bigbrother.rollback.Rollback;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    
    public UndoCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        if (player.hasPermission(Permissions.ROLLBACK.id)) {
            if (split.length == 1) {
                if (Rollback.canUndo()) {
                    final int size = Rollback.undoSize();
                    player.sendMessage(BigBrother.premessage + "Undo-ing last rollback of " + size + " blocks");
                    Rollback.undo(plugin.getServer(), (Player) player);
                    player.sendMessage(BigBrother.premessage + "Undo successful");
                } else {
                    player.sendMessage(BigBrother.premessage + "No rollback to undo");
                }
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is " + ChatColor.RED + "/bb undo");
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
