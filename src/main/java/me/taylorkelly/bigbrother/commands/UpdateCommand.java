/**
 * 
 */
package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Updatr;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Rob
 * 
 */
public class UpdateCommand implements CommandExecutor {
    
    public UpdateCommand(final BigBrother bigBrother) {
        // TODO Auto-generated constructor stub
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] arg3) {
        Updatr.updateAvailable((Player) player);
        return true;
    }
    
}
