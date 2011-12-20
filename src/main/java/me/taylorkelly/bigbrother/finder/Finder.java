package me.taylorkelly.bigbrother.finder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.ActionProvider.ActionData;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Finder {
    
    private final Location location;
    private int radius;
    private final ArrayList<Player> players;
    private final WorldManager manager;
    private final Plugin plugin;
    private static List<Integer> allowedActions;
    
    public Finder(Location location, List<World> worlds, WorldManager manager, Plugin plugin, Collection<Integer> actions) {
        this.manager = manager;
        this.location = location;
        radius = BBSettings.defaultSearchRadius;
        players = new ArrayList<Player>();
        this.plugin = plugin;
        if (actions != null) {
            allowedActions = new ArrayList<Integer>(actions);
        } else {
            allowedActions = ActionProvider.getDefaultActions();
        }
    }
    
    public void setRadius(double radius) {
        this.radius = (int) radius;
    }
    
    public void addReciever(Player player) {
        players.add(player);
    }
    
    public void find() {
        for (Player player : players) {
            player.sendMessage(ChatColor.AQUA + "Searching...");
        }
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new FinderRunner(plugin, location, radius, manager, players));
    }
    
    public void find(String player) {
        for (Player reciept : players) {
            reciept.sendMessage(ChatColor.AQUA + "Searching...");
        }
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new FinderRunner(plugin, player, location, radius, manager, players));
    }
    
    public void find(ArrayList<String> players) {
        // TODO find around multiple players
    }
    
    private class FinderRunner implements Runnable {
        
        private final Location location;
        private final int radius;
        private final ArrayList<Player> players;
        private final WorldManager manager;
        private final Plugin plugin;
        private final String player;
        
        public FinderRunner(final Plugin plugin, final String player, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
            this.player = player;
            this.plugin = plugin;
            this.radius = radius;
            this.location = location;
            this.manager = manager;
            this.players = players;
        }
        
        public FinderRunner(Plugin plugin, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
            this(plugin, null, location, radius, manager, players);
        }
        
        public void run() {
            if (player == null) {
                mysqlFind(plugin, location, radius, manager, players);
                
            } else {
                mysqlFind(plugin, player, location, radius, manager, players);
            }
        }
    }
    
    private static final void mysqlFind(final Plugin plugin, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<BBPlayerInfo, Integer> modifications = new HashMap<BBPlayerInfo, Integer>();
        try {
            // TODO maybe more customizable actions?
            
            /*
             * org.h2.jdbc.JdbcSQLException: Column "ID" must be in the GROUP BY list; SQL statement:
             */
            if (BBDB.usingDBMS(DBMS.POSTGRES)) {
                ps = BBDB.prepare("SELECT player, count(player) AS modifications FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + getActionString() + " AND rbacked = " + (BBDB.usingDBMS(DBMS.POSTGRES) ? "false" : "0") + " AND x < ? AND x > ? AND y < ? AND y > ? AND z < ? AND z > ? AND world = ? GROUP BY player ORDER BY player DESC");
            } else {
                ps = BBDB.prepare("SELECT player, count(player) AS modifications FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + getActionString() + " AND rbacked = '0' AND x < ? AND x > ? AND y < ? AND y > ? AND z < ? AND z > ? AND world = ? GROUP BY player ORDER BY id DESC");
            }
            ps.setInt(1, location.getBlockX() + radius);
            ps.setInt(2, location.getBlockX() - radius);
            ps.setInt(3, location.getBlockY() + radius);
            ps.setInt(4, location.getBlockY() - radius);
            ps.setInt(5, location.getBlockZ() + radius);
            ps.setInt(6, location.getBlockZ() - radius);
            ps.setInt(7, manager.getWorld(location.getWorld().getName()));
            rs = ps.executeQuery();
            BBDB.commit();
            
            int size = 0;
            while (rs.next()) {
                BBPlayerInfo player = BBUsersTable.getInstance().getUserByID(rs.getInt("player"));
                int mods = rs.getInt("modifications");
                modifications.put(player, mods);
                size++;
            }
            if (size > 0) {
                StringBuilder playerList = new StringBuilder();
                for (Entry<BBPlayerInfo, Integer> entry : modifications.entrySet()) {
                    if (entry.getKey() != null) {
                        playerList.append(entry.getKey().getName());
                        playerList.append(" (");
                        playerList.append(entry.getValue());
                        playerList.append("), ");
                    }
                }
                if (playerList.indexOf(",") != -1) {
                    playerList.delete(playerList.lastIndexOf(","), playerList.length());
                }
                //TODO Put into sync'd runnable
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + playerList.length() + " player(s) have modified this area:");
                    player.sendMessage(playerList.toString());
                }
            } else {
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + "No modifications in this area.");
                }
                
            }
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
        } finally {
            BBDB.cleanup("Find SQL", ps, rs);
        }
    }
    
    /**
     * @return
     */
    private static String getActionString() {
        String act = "action IN(";
        boolean first = true;
        for (int actID : allowedActions) {
            if (first) {
                first = false;
            } else {
                act += ",";
            }
            act += Integer.toString(actID);
        }
        return act + ")";
    }
    
    private static final void mysqlFind(final Plugin plugin, final String playerName, final Location location, final int radius, final WorldManager manager, final ArrayList<Player> players) {
        
        BBPlayerInfo hunted = BBUsersTable.getInstance().getUserByName(playerName);
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<ActionCategory, HashMap<Integer, Integer>> mods = new HashMap<ActionCategory, HashMap<Integer, Integer>>();
        for (ActionCategory ac : ActionCategory.values()) {
            mods.put(ac, new HashMap<Integer, Integer>());
        }
        
        try {
            // TODO Centralize action list SQL generation.
            
            if (BBDB.usingDBMS(DBMS.POSTGRES)) {
                ps = BBDB.prepare("SELECT action, type FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + getActionString() + " AND rbacked = false AND x < ? AND x > ? AND y < ? AND y > ?  AND z < ? AND z > ? AND player = ? AND world = ? order by date desc");
            } else {
                ps = BBDB.prepare("SELECT action, type FROM " + BBDataTable.getInstance().getTableName() + " WHERE " + getActionString() + " AND rbacked = 0 AND x < ? AND x > ? AND y < ? AND y > ?  AND z < ? AND z > ? AND player = ? AND world = ? order by date desc");
            }
            
            ps.setInt(1, location.getBlockX() + radius);
            ps.setInt(2, location.getBlockX() - radius);
            ps.setInt(3, location.getBlockY() + radius);
            ps.setInt(4, location.getBlockY() - radius);
            ps.setInt(5, location.getBlockZ() + radius);
            ps.setInt(6, location.getBlockZ() - radius);
            ps.setInt(7, hunted.getID());
            ps.setInt(8, manager.getWorld(location.getWorld().getName()));
            rs = ps.executeQuery();
            BBDB.commit();
            
            int size = 0;
            while (rs.next()) {
                ActionData dat = ActionProvider.Actions.get(rs.getInt("action"));
                int type = rs.getInt("type");
                HashMap<Integer, Integer> wc = mods.get(dat.category);
                if (wc.containsKey(type)) {
                    wc.put(type, wc.get(type) + 1);
                    size++;
                } else {
                    wc.put(type, 1);
                    size++;
                }
                mods.remove(dat.category);
                mods.put(dat.category, wc);
            }
            if (size > 0) {
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + playerName + " has made " + size + " modifications");
                    for (ActionCategory category : ActionCategory.values()) {
                        if (mods.get(category).size() == 0) {
                            continue;
                        }
                        StringBuilder list = new StringBuilder();
                        list.append(ChatColor.AQUA.toString());
                        list.append(category + ": ");
                        list.append(ChatColor.WHITE.toString());
                        for (Entry<Integer, Integer> entry : mods.get(category).entrySet()) {
                            list.append(Material.getMaterial(entry.getKey()));
                            list.append(" (");
                            list.append(entry.getValue());
                            list.append("), ");
                        }
                        if (list.toString().contains(",")) {
                            list.delete(list.lastIndexOf(","), list.length());
                        }
                        player.sendMessage(list.toString());
                    }
                }
            } else {
                for (Player player : players) {
                    player.sendMessage(BigBrother.premessage + playerName + " has no modifications in this area.");
                }
                
            }
        } catch (SQLException ex) {
            BBLogging.severe("Find SQL Exception", ex);
        } finally {
            BBDB.cleanup("Find SQL", ps, rs);
        }
    }
}
