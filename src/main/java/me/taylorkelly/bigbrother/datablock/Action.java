/**
 * Action class
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

package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.ActionSender;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * An action that occurred on a world at a certain location.
 * 
 * @author N3X15
 */
public abstract class Action {
    public final static String ENVIRONMENT = "Environment";
    
    public BBPlayerInfo player = BBPlayerInfo.ENVIRONMENT;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    public String world = "BB_GLOBAL";
    public int type = -1;
    public String data = "";
    public long date = 0;
    protected ArrayList<BBAction> children = new ArrayList<BBAction>();
    
    public Action() {
    }
    
    public Action(String player, String world, int x, int y, int z, int type,
            String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = BBUsersTable.getInstance().getUserByName(player);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }
    
    public Action(BBPlayerInfo player, String world, int x, int y, int z,
            int type, String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = player;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }
    
    public void send() {
        if (!isDisabled())
            ActionSender.offer(this);
    }
    
    public final boolean isDisabled() {
        return ActionProvider.disabledActions.contains(ActionProvider.getActionID(this));
    }
    
    /**
     * Perform a rollback of this action in world.
     * 
     * @param world
     */
    public abstract void rollback(World world);
    
    /**
     * Redo the action (undo the rollback)
     * 
     * @param server
     */
    public abstract void redo(Server server);
    
    /**
     * Name of this action (Uppercase letters and underscores only)
     * 
     * @return
     */
    public abstract String getName();
    
    /**
     * Category of this action
     * 
     * @return
     */
    public abstract ActionCategory getCategory();
    
    /**
     * Category of this action
     * 
     * @return
     */
    public abstract String getDescription();
    
    protected void chestCheck(String player, Block block) {
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            children.add(new DeltaChest(player, chest, chest.getInventory().getContents(), new ItemStack[chest.getInventory().getSize()]));
        }
    }
    
    protected void torchCheck(String player, Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50); // Torch
        torchTypes.add(75); // Redstone torch (on)
        torchTypes.add(76); // Redstone torch (off)
        
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        
        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);
        
        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            children.add(new BrokenBlock(player, torchTop, world));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            children.add(new BrokenBlock(player, torchNorth, world));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            children.add(new BrokenBlock(player, torchSouth, world));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            children.add(new BrokenBlock(player, torchEast, world));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            children.add(new BrokenBlock(player, torchWest, world));
        }
    }
    
    protected void surroundingSignChecks(String player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        
        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            children.add(new BrokenBlock(player, top, world));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            children.add(new BrokenBlock(player, north, world));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            children.add(new BrokenBlock(player, south, world));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            children.add(new BrokenBlock(player, east, world));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
            children.add(new BrokenBlock(player, west, world));
        }
    }
    
    protected void signCheck(String player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            children.add(new SignDestroyed(player, block.getTypeId(), block.getData(), sign, world));
        }
    }
    
    protected void checkGnomesLivingOnTop(String player, Block block) {
        ArrayList<Integer> gnomes = new ArrayList<Integer>();
        gnomes.add(6); // Sapling
        gnomes.add(37); // Yellow Flower
        gnomes.add(38); // Red Flower
        gnomes.add(39); // Brown Mushroom
        gnomes.add(40); // Red Mushroom
        gnomes.add(55); // Redstone
        gnomes.add(59); // Crops
        gnomes.add(64); // Wood Door
        gnomes.add(66); // Tracks
        gnomes.add(69); // Lever
        gnomes.add(70); // Stone pressure plate
        gnomes.add(71); // Iron Door
        gnomes.add(72); // Wood pressure ePlate
        gnomes.add(78); // Snow
        gnomes.add(81); // Cactus
        gnomes.add(83); // Reeds
        gnomes.add(Material.LONG_GRASS.getId());
        gnomes.add(Material.DIODE_BLOCK_ON.getId());
        gnomes.add(Material.DIODE_BLOCK_OFF.getId());
        gnomes.add(Material.FENCE.getId());
        gnomes.add(Material.DEAD_BUSH.getId());
        gnomes.add(Material.DETECTOR_RAIL.getId());
        gnomes.add(Material.POWERED_RAIL.getId());
        
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        Block mrGnome = block.getWorld().getBlockAt(x, y + 1, z);
        
        if (gnomes.contains(mrGnome.getTypeId())) {
            children.add(new BrokenBlock(player, mrGnome, world));
        }
    }
    
    protected void bedCheck(String player, Block bed) {
        if (bed.getType() == Material.BED_BLOCK) {
            if (bed.getData() >= 8) { // Head of bed
                Block foot = null;
                switch (bed.getData() - 8) {
                    case (0): // Head is pointing West
                        foot = bed.getWorld().getBlockAt(x, y, z - 1);
                        children.add(new BrokenBlock(player, foot, world, false));
                        break;
                    case (1): // Head is pointing North
                        foot = bed.getWorld().getBlockAt(x + 1, y, z);
                        children.add(new BrokenBlock(player, foot, world, false));
                        break;
                    case (2): // Head is pointing East
                        foot = bed.getWorld().getBlockAt(x, y, z + 1);
                        children.add(new BrokenBlock(player, foot, world, false));
                        break;
                    case (3): // Head is pointing South
                        foot = bed.getWorld().getBlockAt(x - 1, y, z);
                        children.add(new BrokenBlock(player, foot, world, false));
                        break;
                }
            } else { // Foot of bed
                Block head = null;
                switch (bed.getData()) {
                    case (0): // Head is pointing West
                        head = bed.getWorld().getBlockAt(x, y, z + 1);
                        children.add(new BrokenBlock(player, head, world, false));
                        break;
                    case (1): // Head is pointing North
                        head = bed.getWorld().getBlockAt(x - 1, y, z);
                        children.add(new BrokenBlock(player, head, world, false));
                        break;
                    case (2): // Head is pointing East
                        head = bed.getWorld().getBlockAt(x, y, z - 1);
                        children.add(new BrokenBlock(player, head, world, false));
                        break;
                    case (3): // Head is pointing South
                        head = bed.getWorld().getBlockAt(x + 1, y, z);
                        children.add(new BrokenBlock(player, head, world, false));
                        break;
                }
            }
        }
    }
    
}
