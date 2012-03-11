package me.taylorkelly.bigbrother.commands;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datasource.BBDB;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VersionCommand implements CommandExecutor {
    
    public VersionCommand(final BigBrother plugin) {
    }
    
    public boolean onCommand(final CommandSender player, final Command arg1, final String arg2, final String[] arg3) {
        player.sendMessage("You're running: " + ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version + " (#" + BigBrother.build + ")");
        player.sendMessage("# open statements: " + BBDB.statements.size());
        return true;
    }
    
}
