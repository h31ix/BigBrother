/**
 * Track ownership of burning blocks and flowing liquids.
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
package net.nexisonline.bigbrother.ownership;

import java.util.HashMap;
import java.util.Map.Entry;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
import me.taylorkelly.bigbrother.datablock.BlockBurn;
import me.taylorkelly.bigbrother.datablock.Flow;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import me.taylorkelly.bigbrother.tablemgrs.OwnersTable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * This tracks ownership of blocks.
 * @author Rob
 *
 */
public class OwnershipManager {
    /**
     * How long to cache ownership data in-memory.
     */
    public static final long MAX_SECONDS_UNACCESSED = 10;
    private static final BlockFace[] cardinalFaces = new BlockFace[]{
        BlockFace.UP,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.WEST,
        BlockFace.SOUTH,
        BlockFace.DOWN
    };
    protected static class OwnershipData {
        public int owner;
        public long time;
        /**
         * @param id
         */
        public OwnershipData(int id) {
            owner=id;
            time=System.currentTimeMillis();
        }
        /**
         * @return
         */
        public boolean isTimeToGo() {
            return ((System.currentTimeMillis()-time)/1000 > MAX_SECONDS_UNACCESSED);
        }
    }
    private static BigBrother plugin;
    private static HashMap<Location, OwnershipData> ownershipMap = new HashMap<Location, OwnershipData>();
    
    public static void init(BigBrother plg) {
        plugin=plg;
    }
    /**
     * Attempt to find the owner of a block.
     * TODO: Try to interface with LWC, etc.
     * 
     * @param x X position of the block
     * @param y Y position of the block
     * @param z Z position of the block
     * @return Owner, or Environment if unknown.
     */
    public static BBPlayerInfo findOwner(Block b) {
        if(!BBSettings.storeOwners)
            return BBPlayerInfo.ENVIRONMENT;
        
        Location loc= b.getLocation();
        int ownerID=-1;
        if(ownershipMap.containsKey(loc))
            ownerID = getFromMap(loc);
        else {
            int wldID = plugin.worldManager.getWorld(b.getWorld().getName());
            ownerID = OwnersTable.get(wldID,b.getX(),b.getY(),b.getZ());
        }
        return BBUsersTable.getInstance().getUserByID(ownerID);
    }
    
    /**
     * @param loc
     * @return
     */
    private static int getFromMap(Location loc) {
        synchronized(ownershipMap) {
            OwnershipData dat = ownershipMap.get(loc);
            dat.time=System.currentTimeMillis();
            ownershipMap.remove(loc);
            ownershipMap.put(loc, dat);
            return dat.owner;
        }
    }
    /**
     * Try to set a block's owner
     * @param b Block
     * @param p Player
     */
    public static void setOwner(Block b, BBPlayerInfo p) {
        setOwnerLocation(b.getLocation(),p);
    }
    
    /**
     * Remove old cache.
     */
    private static void cleanup() {
        synchronized(ownershipMap) {
            @SuppressWarnings("unchecked")
            HashMap<Location,OwnershipData> lom = (HashMap<Location, OwnershipData>) ownershipMap.clone();
            for(Entry<Location,OwnershipData> es : lom.entrySet()) {
                if(es.getValue().isTimeToGo()) {
                    ownershipMap.remove(es.getKey());
                }
            }
        }
    }
    
    /**
     * Determine ownership data for water or lava flows.
     * @param blockFrom
     * @param blockTo
     * @return
     */
    public static Flow trackFlow(Block blockFrom, Block blockTo) {
        // Try to determine owner.
        BBPlayerInfo player = findOwner(blockFrom);
        setOwner(blockTo,player);
        return new Flow(player.getName(), blockFrom.getWorld().getName(), blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockFrom.getTypeId(), (byte) 0);
    }
    /**
     * @param location
     * @param pi
     */
    public static void setOwnerLocation(Location loc, BBPlayerInfo p) {
        if(!BBSettings.storeOwners) return;
        OwnershipData dat = new OwnershipData(p.getID());
        synchronized(ownershipMap) {
            if(ownershipMap.containsKey(loc))
                ownershipMap.remove(loc); // Replace.
            ownershipMap.put(loc, dat);
        }
        OwnersTable.set(plugin.worldManager.getWorld(loc.getWorld().getName()),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),p.getID());
        cleanup();
    }
    /**
     * @param block
     * @return
     */
    public static BBDataBlock trackBurn(Block block) {
        Block fire = getBurnSource(block);
        BBPlayerInfo player = BBPlayerInfo.ENVIRONMENT;
        if(fire!=null) {
            player=findOwner(fire);
        }
        return new BlockBurn(player,block,block.getWorld());
    }
    /**
     * @param block
     * @return
     */
    private static Block getBurnSource(Block block) {
        // Fire can be attached to any face of the block,
        // but we should check the top, first.
        for(BlockFace bf:cardinalFaces) {
            if(isFireSource(block.getFace(bf))) {
                return block.getFace(bf);
            }
        }
        return null;
    }
    /**
     * @param face
     * @return
     */
    private static boolean isFireSource(Block block) {
        Material mat = block.getType();
        return mat.equals(Material.LAVA) ||
            mat.equals(Material.STATIONARY_LAVA) ||
            mat.equals(Material.FIRE);
    }
    /**
     * @param block
     */
    public static void removeOwner(Block block) {
        removeOwnerByLocation(block.getLocation());
    }
    /**
     * @param location
     */
    public static void removeOwnerByLocation(Location loc) {
        synchronized(ownershipMap) {
            if(ownershipMap.containsKey(loc))
                ownershipMap.remove(loc);
        }
        OwnersTable.remove(plugin.worldManager.getWorld(loc.getWorld().getName()),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
    }
    
}
