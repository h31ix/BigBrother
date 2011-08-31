package me.taylorkelly.bigbrother.finder;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Used to activate and control different SuperSticks
 * 
 * @author taylor
 */
public class Sticker {
    //private Server server;
    
    private WorldManager manager;
    
    /**
     * Creates the Stick Controller
     * 
     * @param server
     *            The server
     * @param manager
     *            The world manager (for passing to sticks)
     */
    public Sticker(Server server, WorldManager manager) {
        this.manager = manager;
    }
    
    public void onPlayerJoin(Player player, BBPlayerInfo pi) {
        if (pi.hasLog()) {
            pi.historyTool = new HistoryLog();
            player.sendMessage(BigBrother.premessage + "NOTE:  Your logs are still History Logs.  Type /bb done to change them back.");
        }
    }
    
    public void removeLog(Player player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        /*
         * if (pi.hasLog()) { player.sendMessage(BigBrother.premessage + "... But you don't HAVE a History Log, you doofus."); return; }
         */
        player.sendMessage(BigBrother.premessage + "Turning off the History Log...");
        HistoryLog c = new HistoryLog();
        c.disable(player);
        pi.historyTool = null;
        pi.setHasLog(false);
    }
    
    public void giveLog(Player player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.hasLog()) {
            player.sendMessage(BigBrother.premessage + "You already have a History Log.");
            return;
        }
        pi.historyTool = new HistoryLog();
        pi.historyTool.initialize(player);
        pi.setHasLog(true);
    }
    
    /**
     * Returns the description of the Stick that the player is holding
     * 
     * @param player
     *            The player to get their stick info
     * @return the description, or null if the player has no stick
     */
    public String descMode(Player player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.historyTool != null) {
            return pi.historyTool.getDescription();
        } else {
            return null;
        }
    }
    
    /**
     * Determines if a player is using their stick based on the item stack they're associated with. This association is typically what item stack they have in their hand
     * 
     * @param player
     *            The player to check
     * @param itemStack
     *            The item stack they're interacting with.
     * @return true if they're using their stick. false if not
     */
    public boolean hasStick(Player player, ItemStack itemStack) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.historyTool != null) {
            return pi.historyTool.usesStick(itemStack);
        }
        return false;
    }
    
    /**
     * Sends info on a block to a specific player based on their stick
     * 
     * @param player
     *            The player to send info to and to use their stick
     * @param block
     *            The block to get info about
     * @param leftclick
     */
    private void blockInfo(Player player, Block block, boolean leftclick) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.historyTool != null) {
            StickMode mode = pi.historyTool;
            ArrayList<String> info = mode.getInfoOnBlock(block, manager, leftclick);
            for (String msg : info) {
                player.sendMessage(msg);
            }
        }
    }
    
    /**
     * Occurs when a player uses their stick. Gets info and applies updates
     * 
     * @param player
     *            The player who is using a HistoryTool
     * @param block
     *            The block that the stick is interacting with
     */
    public void stick(Player player, Block block, boolean leftclick) {
        blockInfo(player, block, leftclick);
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.historyTool != null) {
            pi.historyTool.update(player);
        }
    }
    
    /**
     * Returns if the player is holding a stick that uses right clicks
     * 
     * @param player
     *            The player to get info about
     * @return Whether they are holding a right click stick
     */
    public boolean rightClickStick(Player player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.historyTool != null) {
            return pi.historyTool.rightClickStick();
        }
        return false;
    }
    
    public boolean leftClickStick(Player player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        if (pi.historyTool != null) {
            return pi.historyTool.leftClickStick();
        }
        return false;
    }
}
