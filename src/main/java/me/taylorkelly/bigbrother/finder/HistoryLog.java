package me.taylorkelly.bigbrother.finder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HistoryLog extends StickMode {
    private ItemStack oldItem;
    private int slot;

    @Override
    public void initialize(Player player) {
        slot = player.getInventory().getHeldItemSlot();
        oldItem = player.getInventory().getItem(slot);
        if(oldItem != null && oldItem.getAmount() > 0) {
            player.sendMessage(ChatColor.AQUA + "Saving your " + oldItem.getType() + ".");
        }
        player.getInventory().setItem(slot, new ItemStack(Material.LOG, 1));
    }

    @Override
    public void disable(Player player) {
        if(oldItem != null && oldItem.getAmount() > 0) {
            player.sendMessage(ChatColor.AQUA + "Here's your " + oldItem.getType() + " back!");
            player.getInventory().setItem(slot, oldItem);
        } else {
            player.getInventory().clear(slot);
        }
    }

    @Override
    public ArrayList<String> getInfoOnBlock(Block block, WorldManager manager, boolean leftclick) {
        ArrayList<Action> history = BBDataTable.getInstance().getBlockHistory(block, manager); //BlockHistory.hist(block, manager);

        ArrayList<String> msgs = new ArrayList<String>();
        if (history.isEmpty()) {
            msgs.add(ChatColor.RED + "No edits on this block");
        } else {
            msgs.add(ChatColor.AQUA.toString() + history.size() + " edits on this block");
            for (Action dataBlock : history) {
                Calendar cal = Calendar.getInstance();
                String DATE_FORMAT = "MMM.d@'" + ChatColor.GRAY + "'kk.mm.ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                cal.setTimeInMillis(dataBlock.date * 1000);
                StringBuilder msg = new StringBuilder(sdf.format(cal.getTime()));
                msg.append(ChatColor.WHITE).append(" - ").append(ChatColor.YELLOW);
                msg.append(dataBlock.player);
                msg.append(ChatColor.WHITE);
                msg.append(" ");
                String[] lines=dataBlock.toString().split("\n");
                msg.append(lines[0]);
                msgs.add(msg.toString());
                if(lines.length>1) {
                    for(int l = 1;l<lines.length;l++)
                        msgs.add(lines[l]);
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
    public void update(Player player) {
        player.getInventory().setItem(slot, new ItemStack(Material.LOG, 1));
    }

    @Override
    public boolean usesStick(ItemStack itemStack) {
        if(itemStack.getType() == Material.LOG) {
            return true;
        }
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
