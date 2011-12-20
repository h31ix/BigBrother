/**
 * BigBrother core plugin
 * Copyright (C) 2011 BigBrother Contributors
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.taylorkelly.bigbrother;

import java.io.File;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.commands.CleanseCommand;
import me.taylorkelly.bigbrother.commands.ConfirmCommand;
import me.taylorkelly.bigbrother.commands.DebugCommand;
import me.taylorkelly.bigbrother.commands.DeleteCommand;
import me.taylorkelly.bigbrother.commands.DoneCommand;
import me.taylorkelly.bigbrother.commands.FindCommand;
import me.taylorkelly.bigbrother.commands.HelpCommand;
import me.taylorkelly.bigbrother.commands.HereCommand;
import me.taylorkelly.bigbrother.commands.HistoryCommand;
import me.taylorkelly.bigbrother.commands.LogCommand;
import me.taylorkelly.bigbrother.commands.MowlawnCommand;
import me.taylorkelly.bigbrother.commands.RollbackCommand;
import me.taylorkelly.bigbrother.commands.StickCommand;
import me.taylorkelly.bigbrother.commands.UndoCommand;
import me.taylorkelly.bigbrother.commands.UnwatchedCommand;
import me.taylorkelly.bigbrother.commands.VersionCommand;
import me.taylorkelly.bigbrother.commands.WatchCommand;
import me.taylorkelly.bigbrother.commands.WatchedCommand;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.DeltaChest;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datasource.ActionSender;
import me.taylorkelly.bigbrother.datasource.BBDB;
import me.taylorkelly.bigbrother.finder.Sticker;
import me.taylorkelly.bigbrother.griefcraft.util.Updater;
import me.taylorkelly.bigbrother.listeners.BBBlockListener;
import me.taylorkelly.bigbrother.listeners.BBEntityListener;
import me.taylorkelly.bigbrother.listeners.BBPlayerListener;
import me.taylorkelly.bigbrother.tablemgrs.ActionTable;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import me.taylorkelly.util.ChestTools;
import net.nexisonline.bigbrother.ownership.OwnershipManager;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BigBrother extends JavaPlugin {
    @SuppressWarnings("unused")
    private ActionProvider actionProvider = null;
    private BBPlayerListener playerListener;
    private BBBlockListener blockListener;
    private BBEntityListener entityListener;
    private StickListener stickListener;
    private Watcher watcher;
    public Sticker sticker;
    public WorldManager worldManager;
    public static String name;
    public static String version;
    public static String build;
    public final static String premessage = ChatColor.AQUA + "[BBROTHER]: " + ChatColor.WHITE;
    public static final String permissionDenied = ChatColor.RED + "[BBROTHER] PERMISSION DENIED.";
    private Updater updater;
    
    public void onDisable() {
        ActionSender.shutdown(this);
        Cleanser.shutdown(this);
        BBDB.shutdown();
    }
    
    public void onEnable() {
        BBLogging.debug("Debug Mode enabled");
        
        // Stuff that was in Constructor
        name = getDescription().getName();
        version = getDescription().getVersion();
        // git-BigBrother-jenkins-BigBrother-384
        String[] buildp = BigBrother.class.getPackage().getImplementationVersion().split("-");
        build = buildp[buildp.length - 1]; // 384
        
        if (version.endsWith("SNAPSHOT")) {
            BBLogging.info("------------------------------------");
            BBLogging.info("Hello, and thank you for using the TESTING version of BigBrother!");
            BBLogging.info("Please note that, since this is far from complete, there will be many bugs.");
            BBLogging.info("IF YOU FIND ANY BUGS, PLEASE REPORT THEM ON http://bugs.nexisonline.net/bb");
            BBLogging.info("Please stay tuned in irc.7chan.org #bigbrother for updates and build notifications.");
            BBLogging.info("------------------------------------");
        }
        
        // Initialize Settings - Needs to come pretty much first
        BBSettings.initialize(this, getDataFolder());
        
        // Download dependencies...
        if (BBSettings.libraryAutoDownload) {
            updater = new Updater();
            try {
                updater.check();
                updater.update();
            } catch (Throwable e) {
                BBLogging.severe("Could not download dependencies", e);
            }
        } else {
            BBLogging.debug("Downloading libraries was skipped");
        }
        
        // Check to see if the user set up BB or not.
        if (BBDB.usingDBMS(DBMS.NULL)) {
            BBLogging.severe("Please rename BigBrother.example.yml to BigBrother.yml and edit it, otherwise BB will not work!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Get database running.
        try {
            BBDB.reconnect();
        } catch (SQLException e) {
            BBLogging.severe("Your database settings are probably incorrect:", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize tables
        BBLogging.info(BBDataTable.getInstance().toString() + " loaded!");
        worldManager = new WorldManager();
        ActionTable.getInstance().init();
        BBPlayerInfo.ENVIRONMENT = new BBPlayerInfo("Environment");
        
        // Initialize Listeners
        playerListener = new BBPlayerListener(this);
        blockListener = new BBBlockListener(this);
        entityListener = new BBEntityListener(this);
        stickListener = new StickListener(this);
        sticker = new Sticker(getServer(), worldManager);
        actionProvider = new BBActionProvider(this);
        BBSettings.loadPostponed();
        
        // Update settings from old versions of BB
        if (new File("BigBrother").exists()) {
            updateSettings(getDataFolder());
        } else if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Fire up the ownership system.
        OwnershipManager.init(this);
        
        BBHelp.initialize(this);
        
        // Register Events
        registerEvents();
        
        // Initialize Player Watching
        watcher = BBSettings.getWatcher(getServer(), getDataFolder());
        
        // Initialize DataBlockSender
        ActionSender.initialize(this, getDataFolder(), worldManager);
        
        // Initialize Cleanser
        Cleanser.initialize(this);
        
        // Done!
        BBLogging.info(name + " " + version + " (build #" + build + ") enabled!");
    }
    
    private void updateSettings(File dataFolder) {
        File oldDirectory = new File("BigBrother");
        dataFolder.getParentFile().mkdirs();
        oldDirectory.renameTo(dataFolder);
    }
    
    private void registerEvents() {
        // TODO Only register events that are being listened to
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
        
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_SPREAD, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Monitor, this);
        
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);
        
        // These events are used for Super Sticks
        pm.registerEvent(Event.Type.BLOCK_PLACE, stickListener, Priority.Low, this);
        
        BBCommand bbc = new BBCommand(this);
        bbc.registerExecutor("debug", new DebugCommand(this));
        bbc.registerExecutor("version", new VersionCommand(this));
        //bbc.registerExecutor("update", new UpdateCommand(this));
        bbc.registerExecutor("watch", new WatchCommand(this));
        bbc.registerExecutor("watched", new WatchedCommand(this));
        bbc.registerExecutor("unwatched", new UnwatchedCommand(this));
        bbc.registerExecutor("cleanse", new CleanseCommand(this));
        bbc.registerExecutor("rollback", new RollbackCommand(this));
        bbc.registerExecutor("confirm", new ConfirmCommand(this));
        bbc.registerExecutor("delete", new DeleteCommand(this));
        bbc.registerExecutor("undo", new UndoCommand(this));
        bbc.registerExecutor("stick", new StickCommand(this));
        bbc.registerExecutor("log", new LogCommand(this));
        bbc.registerExecutor("done", new DoneCommand(this));
        bbc.registerExecutor("here", new HereCommand(this));
        bbc.registerExecutor("find", new FindCommand(this));
        bbc.registerExecutor("help", new HelpCommand(this));
        bbc.registerExecutor("mowlawn", new MowlawnCommand(this));
        bbc.registerExecutor("history", new HistoryCommand(this));
        getCommand("bb").setExecutor(bbc);
    }
    
    public boolean watching(Player player) {
        return watcher.watching(player);
    }
    
    public boolean toggleWatch(String player) {
        return watcher.toggleWatch(player);
    }
    
    public String getWatchedPlayers() {
        return watcher.getWatchedPlayers();
    }
    
    public boolean haveSeen(Player player) {
        return watcher.haveSeen(player);
    }
    
    public void watchPlayer(Player player) {
        watcher.watchPlayer(player);
    }
    
    public String getUnwatchedPlayers() {
        return watcher.getUnwatchedPlayers();
    }
    
    public boolean hasStick(Player player, ItemStack itemStack) {
        return sticker.hasStick(player, itemStack);
    }
    
    public void stick(Player player, Block block, boolean leftclick) {
        sticker.stick(player, block, leftclick);
    }
    
    public boolean rightClickStick(Player player) {
        return sticker.rightClickStick(player);
    }
    
    public boolean leftClickStick(Player player) {
        return sticker.leftClickStick(player);
    }
    
    public void closeChestIfOpen(BBPlayerInfo pi) {
        if (pi.hasOpenedChest()) {
            if (!ActionProvider.isDisabled(DeltaChest.class)) {
                World world = pi.getOpenedChest().getWorld();
                Block b = world.getBlockAt(pi.getOpenedChest().getX(), pi.getOpenedChest().getY(), pi.getOpenedChest().getZ());
                if (b.getState() instanceof Chest) {
                    Chest chest = (Chest) b.getState();
                    ItemStack[] orig = pi.getOldChestContents();
                    ItemStack[] latest = ChestTools.getChestContents(chest);
                    DeltaChest dc = new DeltaChest(pi.getName(), chest, orig, latest);
                    dc.send();
                }
            }
            BBUsersTable.getInstance().userOpenedChest(pi.getName(), null, null);
            // Chest closed.
        }
    }
    
    /*
     * /////////////////////////////////////////////////////////////// BIGBROTHER COMMON API FUNCTIONS /
     *///////////////////////////////////////////////////////////////
    
    /**
     * Tell BigBrother that a block has been broken/removed.
     * 
     * @param player Player making the change
     * @param block The block being removed
     * @param world The world in which this action occurred
     */
    public void onBlockBroken(String player, Block block, String world) {
        BrokenBlock bb = new BrokenBlock(player, block, world);
        bb.send();
    }
    
    /**
     * Tell BigBrother that a block has been placed/created.
     * 
     * @param player Player making the change
     * @param block The block being added
     * @param world The world in which this action occurred
     */
    public void onBlockPlaced(String player, Block block, String world) {
        PlacedBlock bb = new PlacedBlock(player, block, world);
        bb.send();
    }
}
