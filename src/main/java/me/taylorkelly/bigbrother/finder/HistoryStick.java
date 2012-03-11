package me.taylorkelly.bigbrother.finder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HistoryStick extends StickMode {
    private ItemStack oldItem;
    private int slot;
    
    @Override
    public void initialize(final Player player) {
        slot = player.getInventory().getHeldItemSlot();
        oldItem = player.getInventory().getItem(slot);
        if ((oldItem != null) && (oldItem.getAmount() > 0)) {
            player.sendMessage(ChatColor.AQUA + "Saving your " + oldItem.getType() + ".");
        }
        player.getInventory().setItem(slot, new ItemStack(BBSettings.stickItem, 1));
    }
    
    @Override
    public void disable(final Player player) {
        if ((oldItem != null) && (oldItem.getAmount() > 0)) {
            player.sendMessage(ChatColor.AQUA + "Here's your " + oldItem.getType() + " back!");
            player.getInventory().setItem(slot, oldItem);
        } else {
            player.getInventory().clear(slot);
        }
    }
    
    @Override
    public ArrayList<String> getInfoOnBlock(final Block block, final WorldManager manager, final boolean ignored) {
        final ArrayList<Action> history = BBDataTable.getInstance().getBlockHistory(block, manager);
        
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
        return "History Stick";
    }
    
    @Override
    public void update(final Player player) {
        player.getInventory().setItem(slot, new ItemStack(BBSettings.stickItem, 1));
    }
    
    @Override
    public boolean usesStick(final ItemStack itemStack) {
        if (itemStack.getTypeId() == BBSettings.stickItem)
            return true;
        return false;
    }
    
    @Override
    public boolean rightClickStick() {
        return true;
    }
    
    @Override
    public boolean leftClickStick() {
        return true;
    }
    
}
