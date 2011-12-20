/**
 * (c)2011 BigBrother Contributors
 *
 */
package me.taylorkelly.util;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * @author Rob
 * 
 */
public class ChestTools {
    /**
     * Workaround for horrific bug, that took me ages to discover. When Accessing double chest inventory using default bukkit method, the getInventory() method returns only half of the contents, depending on what block was right-clicked. According to forums, there is no appropriate solution in bukkit API, so we must manually search, whether current chest is double chest, and then eventually return merged inventories.
     * 
     * @TODO: Is this fixed?
     */
    public static ItemStack[] getChestContents(Chest chest) {
        Chest second = null;
        
        // iterate through nearby blocks.
        
        if (chest.getBlock().getRelative(BlockFace.NORTH).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.NORTH).getState();
        } else if (chest.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.SOUTH).getState();
        } else if (chest.getBlock().getRelative(BlockFace.EAST).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.EAST).getState();
        } else if (chest.getBlock().getRelative(BlockFace.WEST).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.WEST).getState();
        }
        
        if (second == null)
            // no problem here
            return chest.getInventory().getContents();
        else {
            // I think it would be good, to consistently return same chest
            // contents, regardless of what
            // block was clicked on. That means, we must determine, which part
            // of chest comes first, and which second.
            // I choose the one, which has lower X coordinate. If they are same,
            // than it's the one with lower Z coordinate.
            // I believe it can be easily checked with this trick:
            
            ItemStack[] result = new ItemStack[54];
            ItemStack[] firstHalf;
            ItemStack[] secondHalf;
            
            if ((chest.getX() + chest.getZ()) < (second.getX() + second.getZ())) {
                firstHalf = chest.getInventory().getContents();
                secondHalf = second.getInventory().getContents();
            } else {
                firstHalf = second.getInventory().getContents();
                secondHalf = chest.getInventory().getContents();
            }
            
            // now merge them
            // possibly unsafe number 27?
            for (int i = 0; i < 27; i++) {
                result[i] = firstHalf[i];
                result[i + 27] = secondHalf[i];
            }
            
            return result;
        }
    }
    
    /**
     * Adapted by Nexypoo.
     * 
     * @param chest
     */
    public static void setChestContents(Chest chest, ItemStack[] items) {
        Chest second = null;
        
        // iterate through nearby blocks.
        
        if (chest.getBlock().getRelative(BlockFace.NORTH).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.NORTH).getState();
        } else if (chest.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.SOUTH).getState();
        } else if (chest.getBlock().getRelative(BlockFace.EAST).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.EAST).getState();
        } else if (chest.getBlock().getRelative(BlockFace.WEST).getType() == Material.CHEST) {
            second = (Chest) chest.getBlock().getRelative(BlockFace.WEST).getState();
        }
        
        if (second == null) {
            // no problem here
            chest.getInventory().setContents(items);
        } else {
            // I think it would be good, to consistently return same chest
            // contents, regardless of what
            // block was clicked on. That means, we must determine, which part
            // of chest comes first, and which second.
            // I choose the one, which has lower X coordinate. If they are same,
            // than it's the one with lower Z coordinate.
            // I believe it can be easily checked with this trick:
            
            ItemStack[] firstHalf;
            ItemStack[] secondHalf;
            
            if ((chest.getX() + chest.getZ()) < (second.getX() + second.getZ())) {
                firstHalf = chest.getInventory().getContents();
                secondHalf = second.getInventory().getContents();
            } else {
                firstHalf = second.getInventory().getContents();
                secondHalf = chest.getInventory().getContents();
            }
            
            // now merge them
            // possibly unsafe number 27?
            for (int i = 0; i < 27; i++) {
                firstHalf[i] = items[i];
                secondHalf[i] = items[i + 27];
            }
            
            // UND SAVE
            if ((chest.getX() + chest.getZ()) < (second.getX() + second.getZ())) {
                chest.getInventory().setContents(firstHalf);
                second.getInventory().setContents(secondHalf);
            } else {
                second.getInventory().setContents(firstHalf);
                chest.getInventory().setContents(secondHalf);
            }
        }
    }
}
