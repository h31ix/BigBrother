package me.taylorkelly.bigbrother.rollback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Rollback {
    
    public final Server server;
    private final ArrayList<Player> recievers;
    public final ArrayList<String> players;
    public boolean rollbackAll;
    public long time;
    public final ArrayList<Integer> blockTypes;
    public int radius;
    public Location center;
    private final LinkedList<Action> listBlocks;
    private static final LinkedList<Action> lastRollback = new LinkedList<Action>();
    private static String undoRollback = null;
    private final WorldManager manager;
    //private int size; // Number of items to roll back
    private final Plugin plugin;
    public List<Integer> allowedActions;
    
    public Rollback(Server server, WorldManager manager, Plugin plugin) {
        this.manager = manager;
        this.rollbackAll = false;
        this.server = server;
        this.plugin = plugin;
        this.time = 0;
        blockTypes = new ArrayList<Integer>();
        players = new ArrayList<String>();
        recievers = new ArrayList<Player>();
        listBlocks = new LinkedList<Action>();
        allowedActions=new ArrayList<Integer>();
    }
    
    public void addReciever(Player player) {
        recievers.add(player);
    }
    
    public void rollback() {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Thread rollbacker = new Rollbacker(plugin, server.getScheduler());
        rollbacker.start();
    }
    
    private String getSimpleString(ArrayList<?> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).toString());
            if (i + 1 < list.size()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
    
    public void rollbackAll() {
        rollbackAll = true;
    }
    
    public void addPlayers(ArrayList<String> playerList) {
        players.addAll(playerList);
    }
    
    public void setTime(long l) {
        this.time = l;
    }
    
    public void addTypes(ArrayList<Integer> blockTypes) {
        this.blockTypes.addAll(blockTypes);
    }
    
    private void rollbackBlocks() {
        lastRollback.clear();
        new RollbackByTick(plugin.getServer().getScheduler(), plugin);
    }
    
    public static boolean canUndo() {
        if (lastRollback != null) {
            return lastRollback.size() > 0;
        } else {
            return false;
        }
    }
    
    public static int undoSize() {
        if (lastRollback != null) {
            return lastRollback.size();
        } else {
            return 0;
        }
    }
    
    public static void undo(Server server, Player player) {
        int i = 0;
        while (lastRollback.size() > 0) {
            Action dataBlock = lastRollback.removeFirst();
            if (dataBlock != null) {
                dataBlock.redo(server);
                i++;
            }
        }
        if (undoRollback != null) {
            if(BBDB.tryUpdate(undoRollback)) {
                undoRollback = null;
                player.sendMessage(ChatColor.AQUA + "Successfully undid a rollback of " + i + " edits");
            }
        }
    }
    
    public void setRadius(int radius, Location center) {
        this.radius = radius;
        this.center = center;
    }
    
    private class Rollbacker extends Thread {
        
        private PreparedStatement create_ps=null;
        private PreparedStatement update_ps=null;
        
        private Rollbacker(Plugin plugin, BukkitScheduler scheduler) {
            try {
                create_ps = BBDB.prepare(RollbackPreparedStatement.getInstance().create(Rollback.this, manager));
                update_ps = BBDB.prepare(RollbackPreparedStatement.getInstance().update(Rollback.this, manager));
            } catch (SQLException e) {
                BBLogging.severe("Rollbacker failed to initialize:",e);
            }
        }
        
        public void run() {
            ResultSet set = null;
            try {
                
                set = create_ps.executeQuery();
                BBDB.commit();
                
                int rollbackSize = 0;
                while (set.next()) {
                    String data = set.getString("data");
                    listBlocks.addLast(
ActionProvider.findAndProvide(set.getInt("action"),BBUsersTable.getInstance().getUserByID(set.getInt("player")), set.getString("world"), set.getInt("x"),
                            set.getInt("y"), set.getInt("z"), set.getInt("type"), data));
                    rollbackSize++;
                }
                if (rollbackSize > 0) {
                    for (Player player : recievers) {
                        player.sendMessage(BigBrother.premessage + "Rolling back " + rollbackSize + " edits.");
                        String playersString = (rollbackAll) ? "All Players" : getSimpleString(players);
                        player.sendMessage(ChatColor.BLUE + "Player(s): " + ChatColor.WHITE + playersString);
                        if (blockTypes.size() > 0) {
                            player.sendMessage(ChatColor.BLUE + "Block Type(s): " + ChatColor.WHITE + getSimpleString(blockTypes));
                        }
                        if (allowedActions.size() > 0) {
                            player.sendMessage(ChatColor.BLUE + "Action Type(s): " + ChatColor.WHITE + getActionString(allowedActions));
                        }
                        if (time != 0) {
                            Calendar cal = Calendar.getInstance();
                            String DATE_FORMAT = "kk:mm:ss 'on' MMM d";
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                            cal.setTimeInMillis(time * 1000);
                            player.sendMessage(ChatColor.BLUE + "Since: " + ChatColor.WHITE + sdf.format(cal.getTime()));
                        }
                        if (radius != 0) {
                            player.sendMessage(ChatColor.BLUE + "Radius: " + ChatColor.WHITE + radius + " blocks");
                        }
                        
                    }
                    try {
                        rollbackBlocks();
                        for (Player player : recievers) {
                            player.sendMessage(BigBrother.premessage + "Successfully rollback'd.");
                        }
                        
                        update_ps.execute();
                        BBDB.commit();
                        undoRollback = RollbackPreparedStatement.getInstance().undoStatement(Rollback.this, manager);
                    } catch (SQLException ex) {
                        BBLogging.severe("Rollback edit SQL Exception: "+RollbackPreparedStatement.getInstance().update(Rollback.this, manager), ex);
                    }
                } else {
                    for (Player player : recievers) {
                        player.sendMessage(BigBrother.premessage + "Nothing to rollback.");
                    }
                }
            } catch (SQLException ex) {
                BBLogging.severe("Rollback get SQL Exception", ex);
            } finally {
                try {
                    if(create_ps!=null)
                        create_ps.close();
                    if(update_ps!=null)
                        update_ps.close();
                } catch (SQLException e) {}
            }
        }

        /**
         * @param allowedActions
         * @return
         */
        private String getActionString(List<Integer> allowedActions) {
            String o = "";
            boolean first=true;
            for (int actID : allowedActions) {
                if(!first) {
                    o+=",";
                }
                first=false;
                o+=ActionProvider.findActionName(actID);
            }
            return o;
        }
    }
    
    private class RollbackByTick implements Runnable {
        
        private final int id;
        
        public RollbackByTick(BukkitScheduler scheduler, Plugin plugin) {
            this.id = scheduler.scheduleSyncRepeatingTask(plugin, this, 0, 1);
        }
        
        @Override
        public void run() {
            int count = 0;
            
            while (count < BBSettings.rollbacksPerTick && listBlocks.size() > 0) {
                Action dataBlock = listBlocks.removeFirst();
                if (dataBlock != null) {
                    lastRollback.addFirst(dataBlock);
                    try {
                        dataBlock.rollback(server.getWorld(dataBlock.world));
                    } catch (Exception e) {
                        BBLogging.warning("Caught exception when rolling back a " + dataBlock.getName(), e);
                    }
                    count++;
                }
            }
            
            
            if (listBlocks.size() == 0) {
                BBLogging.debug("Finished rollback");
                
                plugin.getServer().getScheduler().cancelTask(id);
            } else {
                BBLogging.debug("Need to rollback " + listBlocks.size() + " more");
                
            }
        }
    }
}
