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
    private ArrayList<Integer> blockTypes;
    private ArrayList<String> playerList;
    private boolean all = false;
    private Server server;
    private Player player;
    private WorldManager manager;
    private int radius = 0;
    private Plugin plugin;
    private List<Integer> allowedActions;
    
    public RollbackInterpreter(Player player, String[] split, Server server, WorldManager manager, Plugin plugin) {
        this.manager = manager;
        this.player = player;
        this.server = server;
        this.plugin = plugin;
        playerList = new ArrayList<String>();
        blockTypes = new ArrayList<Integer>();
        // Populate list
        allowedActions = new ArrayList<Integer>();
        allowedActions.add(find("BrokenBlock"));
        allowedActions.add(find("PlacedBlock"));
        allowedActions.add(find("DeltaChest"));
        allowedActions.add(find("CreateSignText"));
        allowedActions.add(find("DestroySignText"));
        allowedActions.add(find("LeafDecay"));
        allowedActions.add(find("TNTExplosion"));
        allowedActions.add(find("CreeperExplosion"));
        allowedActions.add(find("MiscExplosion"));
        allowedActions.add(find("BlockBurn"));
        allowedActions.add(find("Flow"));
        
        for (int i = 1; i < split.length; i++) {
            String argument = split[i].trim();
            if (argument.equals("") || argument.equals(" ")) {
                continue;
            }
            if (argument.length() > 2 && argument.substring(0, 2).equalsIgnoreCase("t:")) {
                dateSearch = TimeParser.parseTime(argument.substring(2), player);
            } else if (argument.length() > 3 && argument.substring(0, 3).equalsIgnoreCase("id:")) {
                parseId(argument.substring(3));
            } else if (argument.length() > 2 && argument.substring(0, 2).equalsIgnoreCase("r:")) {
                parseRadius(argument.substring(2));
            } else if (argument.length() > 2 && argument.substring(0, 2).equalsIgnoreCase("a:")) {
                parseAction(argument.substring(2));
            } else if (argument.equalsIgnoreCase("*")) {
                all = true;
            } else {
                List<Player> targets = server.matchPlayer(argument);
                Player findee = null;
                if (targets.size() == 1) {
                    findee = targets.get(0);
                }
                playerList.add((findee == null) ? argument : findee.getName());
            }
        }
    }
    
    /**
     * @param string
     * @return
     */
    private Integer find(String string) {
        return ActionProvider.findActionID(string);
    }

    /**
     * actions to roll back
     * a:10,!BLOCK_PLACE
     * @author N3X15
     */
    private void parseAction(String actstr) {
        for(String act:actstr.split(",")) {
            if(!act.startsWith("!") && allowedActions.size()==0) {
                allowedActions.clear();
            }
            int ca;
            if(act.startsWith("!")) {
                ca = find(act.substring(1));
                if(ca>-1 && allowedActions.contains(ca))
                    allowedActions.remove(ca);
            }else{
                ca = find(act);
                if(ca>-1 && !allowedActions.contains(ca))
                    allowedActions.add(ca);
            }
        }
    }

    private void parseRadius(String radius) {
        try {
            int radInt = Integer.parseInt(radius);
            if (radInt <= 0) {
                player.sendMessage(ChatColor.RED + "Ignoring invalid radius: " + radius);
            } else {
                this.radius = radInt;
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Ignoring invalid radius: " + radius);
        }
    }
    
    private void parseId(String id) {
        if (id.contains(",")) {
            String[] ids = id.split(",");
            for (String actId : ids) {
                if (actId.equals("")) {
                    continue;
                }
                Material m = Material.matchMaterial(actId);
                if (m != null)
                    blockTypes.add(m.getId());
                else {
                    player.sendMessage(ChatColor.RED + "Ignoring invalid block id: " + actId);
                }
            }
        } else {
            Material m = Material.matchMaterial(id);
            if (m != null)
                blockTypes.add(m.getId());
            else {
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
        rollback.allowedActions=allowedActions;
        rollback.setRadius(radius, player.getLocation());
        if (radius == 0 && dateSearch == null) {
            return false;
        } else {
            return true;
        }
    }
    
    // public Rollback getAndInitializeRollback() {
    // rollback.prepareRollback();
    // return rollback;
    // }
    
    public void send() {
        rollback.rollback();
    }
}
