package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;
import me.taylorkelly.bigbrother.rollback.RollbackConfirmation;
import me.taylorkelly.bigbrother.rollback.RollbackInterpreter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollbackCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    
    public RollbackCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        if (player.hasPermission(Permissions.ROLLBACK.id)) {
            if (split.length > 1) {
                final RollbackInterpreter interpreter = new RollbackInterpreter((Player) player, split, plugin.getServer(), plugin.worldManager, plugin);
                final Boolean passed = interpreter.interpret();
                if (passed != null) {
                    if (passed) {
                        interpreter.send();
                    } else {
                        player.sendMessage(BigBrother.premessage + ChatColor.RED + "Warning: " + ChatColor.WHITE + "You are rolling back without a time or radius argument.");
                        player.sendMessage("Use " + ChatColor.RED + "/bb confirm" + ChatColor.WHITE + " to confirm the rollback.");
                        player.sendMessage("Use " + ChatColor.RED + "/bb delete" + ChatColor.WHITE + " to delete it.");
                        RollbackConfirmation.setRI((Player) player, interpreter);
                    }
                }
            } else {
                player.sendMessage(BigBrother.premessage + "Usage is: " + ChatColor.RED + "/bb rollback name1 [name2] [options]");
                player.sendMessage(BigBrother.premessage + "Please read the full command documentation at https://github.com/tkelly910/BigBrother/wiki/Commands");
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
