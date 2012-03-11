package me.taylorkelly.bigbrother.finder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HistoryLog extends StickMode {
    /**
     * This used to just give people logs. The new version will simply tell them that they must have a log in their hands to read block history.
     * 
     * @author N3X15
     */
    @Override
    public void initialize(final Player player) {
        player.sendMessage(BigBrother.premessage + "Every log in your inventory (and any logs you pick up) are now History Logs!");
        player.sendMessage(ChatColor.AQUA + "To read the history of a solid block (e.g. Dirt), punch (left-click) it with your History Log.");
        player.sendMessage(ChatColor.AQUA + "To read the history of a non-solid block (e.g. water or air), replace it (right-click) with your History Log");
        player.sendMessage(ChatColor.AQUA + "When you're done, simply type " + ChatColor.WHITE + "/bb done" + ChatColor.AQUA + " to return your logs to their normal behavior.");
    }
    
    @Override
    public void disable(final Player player) {
        player.sendMessage(BigBrother.premessage + "Your logs have returned to their previous level of worthlessness!");
    }
    
    @Override
    public ArrayList<String> getInfoOnBlock(final Block block, final WorldManager manager, final boolean leftclick) {
        final ArrayList<Action> history = BBDataTable.getInstance().getBlockHistory(block, manager); //BlockHistory.hist(block, manager);
        
        final ArrayList<String> msgs = new ArrayList<String>();
        if (history.isEmpty()) {
            msgs.add(ChatColor.RED + "No edits on this block");
        } else {
            msgs.add(ChatColor.AQUA.toString() + history.size() + " edits on this block");
            for (final Action dataBlock : history) {
                final Calendar cal = Calendar.getInstance();
                final String DATE_FORMAT = "MMM.d@'" + ChatColor.GRAY + "'kk.mm.ss";
                final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                cal.setTimeInMillis(dataBlock.date * 1000);
                final StringBuilder msg = new StringBuilder(sdf.format(cal.getTime()));
                msg.append(ChatColor.WHITE).append(" - ").append(ChatColor.YELLOW);
                msg.append(dataBlock.player);
                msg.append(ChatColor.WHITE);
                msg.append(" ");
                final String[] lines = dataBlock.toString().split("\n");
                msg.append(lines[0]);
                msgs.add(msg.toString());
                if (lines.length > 1) {
                    for (int l = 1; l < lines.length; l++) {
                        msgs.add(lines[l]);
                    }
                }
            }
        }
        return msgs;
    }
    
    @Override
    public String getDescription() {
        return "History Log";
    }
    
    public boolean usesStick() {
        return false;
    }
    
    @Override
    public void update(final Player player) {
        //player.getInventory().setItem(slot, new ItemStack(Material.LOG, 1));
    }
    
    @Override
    public boolean usesStick(final ItemStack itemStack) {
        if (itemStack.getType() == Material.LOG)
            return true;
        return false;
    }
    
    @Override
    public boolean rightClickStick() {
        return false;
    }
    
    @Override
    public boolean leftClickStick() {
        return true;
    }
    
}
