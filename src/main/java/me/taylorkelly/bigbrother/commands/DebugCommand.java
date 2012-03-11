/**
 * (c)2011 BigBrother Contributors
 *
 */
package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BBCommand;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author Rob
 * 
 */
public class DebugCommand extends BBCommand {
    
    /**
     * @param plugin
     */
    public DebugCommand(final BigBrother plugin) {
        super(plugin);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] split) {
        if (player.hasPermission(Permissions.ROLLBACK.id)) {
            if (split.length == 2) {
                BBSettings.debugMode = (split[1].equalsIgnoreCase("on"));
                player.sendMessage(BigBrother.premessage + " DEBUG " + ((BBSettings.debugMode) ? "ON" : "OFF"));
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
}
