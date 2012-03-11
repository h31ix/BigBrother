package me.taylorkelly.bigbrother.rollback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.util.TimeParser;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RollbackInterpreter {
    
    private Rollback rollback;
    private Calendar dateSearch;
    private final ArrayList<Integer> blockTypes;
    private final ArrayList<String> playerList;
    private boolean all = false;
    private final Server server;
    private final Player player;
    private final WorldManager manager;
    private int radius = 0;
    private final Plugin plugin;
    private List<Integer> allowedActions;
    
    public RollbackInterpreter(final Player player, final String[] split, final Server server, final WorldManager manager, final Plugin plugin) {
        this.manager = manager;
        this.player = player;
        this.server = server;
        this.plugin = plugin;
        playerList = new ArrayList<String>();
        blockTypes = new ArrayList<Integer>();
        allowedActions = ActionProvider.getDefaultActions();
        for (int i = 1; i < split.length; i++) {
            final String argument = split[i].trim();
            if (argument.equals("") || argument.equals(" ")) {
                continue;
            }
            if ((argument.length() > 2) && argument.substring(0, 2).equalsIgnoreCase("t:")) {
                dateSearch = TimeParser.parseTime(argument.substring(2), player);
            } else if ((argument.length() > 3) && argument.substring(0, 3).equalsIgnoreCase("id:")) {
                parseId(argument.substring(3));
            } else if ((argument.length() > 2) && argument.substring(0, 2).equalsIgnoreCase("r:")) {
                parseRadius(argument.substring(2));
            } else if ((argument.length() > 2) && argument.substring(0, 2).equalsIgnoreCase("a:")) {
                allowedActions = ActionProvider.parseActionSwitch(null, argument.substring(2));
            } else if (argument.equalsIgnoreCase("*")) {
                all = true;
            } else {
                final List<Player> targets = server.matchPlayer(argument);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                playerList.add((findee == null) ? argument : findee.getName());
            }
        }
    }
    
    private void parseRadius(final String radius) {
        try {
            final int radInt = Integer.parseInt(radius);
            if (radInt <= 0) {
                player.sendMessage(ChatColor.RED + "Ignoring invalid radius: " + radius);
            } else {
                this.radius = radInt;
            }
        } catch (final Exception e) {
            player.sendMessage(ChatColor.RED + "Ignoring invalid radius: " + radius);
        }
    }
    
    private void parseId(final String id) {
        if (id.contains(",")) {
            final String[] ids = id.split(",");
            for (final String actId : ids) {
                if (actId.equals("")) {
                    continue;
                }
                final Material m = Material.matchMaterial(actId);
                if (m != null) {
                    blockTypes.add(m.getId());
                } else {
                    player.sendMessage(ChatColor.RED + "Ignoring invalid block id: " + actId);
                }
            }
        } else {
            final Material m = Material.matchMaterial(id);
            if (m != null) {
                blockTypes.add(m.getId());
            } else {
                player.sendMessage(ChatColor.RED + "Ignoring invalid block id: " + id);
            }
        }
    }
    
    public Boolean interpret() {
        rollback = new Rollback(server, manager, plugin);
        rollback.addReciever(player);
        if (all) {
            rollback.rollbackAll();
        } else {
            if (playerList.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No players marked for rollback. Cancelling rollback.");
                player.sendMessage(ChatColor.RED + "Use * for all players");
                return null;
            }
            rollback.addPlayers(playerList);
        }
        if (dateSearch != null) {
            rollback.setTime(dateSearch.getTimeInMillis() / 1000);
        }
        if (!blockTypes.isEmpty()) {
            rollback.addTypes(blockTypes);
        }
        rollback.allowedActions = allowedActions;
        rollback.setRadius(radius, player.getLocation());
        if ((radius == 0) && (dateSearch == null))
            return false;
        else
            return true;
    }
    
    // public Rollback getAndInitializeRollback() {
    // rollback.prepareRollback();
    // return rollback;
    // }
    
    public void send() {
        rollback.rollback();
    }
}
