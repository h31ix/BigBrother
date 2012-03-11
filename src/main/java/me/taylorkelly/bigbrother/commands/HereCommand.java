package me.taylorkelly.bigbrother.commands;

import java.util.List;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;
import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HereCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    
    public HereCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender send, final Command arg1, final String arg2, final String[] split) {
        final Player player = (Player) send;
        if (player.hasPermission(Permissions.INFO.id)) {
            if (split.length == 1) {
                final Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.addReciever(player);
                finder.find();
            } else if (Numbers.isNumber(split[1]) && (split.length == 2)) {
                final Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.setRadius(Double.parseDouble(split[1]));
                finder.addReciever(player);
                finder.find();
            } else if (split.length == 2) {
                final Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.addReciever(player);
                final List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                finder.find((findee == null) ? split[1] : findee.getName());
            } else if (Numbers.isNumber(split[2]) && (split.length == 3)) {
                final Finder finder = new Finder(player.getLocation(), plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.setRadius(Double.parseDouble(split[2]));
                finder.addReciever(player);
                final List<Player> targets = plugin.getServer().matchPlayer(split[1]);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                finder.find((findee == null) ? split[1] : findee.getName());
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb here");
                player.sendMessage("or " + ChatColor.RED + "/bb here <radius>");
                player.sendMessage("or " + ChatColor.RED + "/bb here <name>");
                player.sendMessage("or " + ChatColor.RED + "/bb here <name> <radius>");
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
