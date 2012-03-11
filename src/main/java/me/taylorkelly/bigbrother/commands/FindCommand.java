package me.taylorkelly.bigbrother.commands;

import java.util.List;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;
import me.taylorkelly.bigbrother.finder.Finder;
import me.taylorkelly.util.Numbers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    
    public FindCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender send, final Command arg1, final String arg2, final String[] split) {
        final Player player = (Player) send;
        if (player.hasPermission(Permissions.INFO.id)) {
            if ((split.length == 4) && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3])) {
                final World currentWorld = player.getWorld();
                final Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                final Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.addReciever(player);
                finder.find();
            } else if ((split.length == 5) && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3]) && Numbers.isNumber(split[4])) {
                final World currentWorld = player.getWorld();
                final Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                final Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.setRadius(Double.parseDouble(split[4]));
                finder.addReciever(player);
                finder.find();
            } else if ((split.length == 5) && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3])) {
                final World currentWorld = player.getWorld();
                final Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                final Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.addReciever(player);
                final List<Player> targets = plugin.getServer().matchPlayer(split[4]);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                finder.find((findee == null) ? split[4] : findee.getName());
            } else if ((split.length == 6) && Numbers.isNumber(split[1]) && Numbers.isNumber(split[2]) && Numbers.isNumber(split[3]) && Numbers.isNumber(split[5])) {
                final World currentWorld = player.getWorld();
                final Location loc = new Location(currentWorld, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                final Finder finder = new Finder(loc, plugin.getServer().getWorlds(), plugin.worldManager, plugin, null);
                finder.setRadius(Double.parseDouble(split[5]));
                finder.addReciever(player);
                final List<Player> targets = plugin.getServer().matchPlayer(split[4]);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                finder.find((findee == null) ? split[4] : findee.getName());
            } else {
                player.sendMessage(BigBrother.premessage + "usage is " + ChatColor.RED + "/bb find <x> <y> <z>");
                player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <radius>");
                player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name>");
                player.sendMessage("or " + ChatColor.RED + "/bb find <x> <y> <z> <name> <radius>");
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return true;
    }
    
}
