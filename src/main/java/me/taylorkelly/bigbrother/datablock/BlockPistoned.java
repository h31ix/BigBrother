/**
 * A piston moving stuff
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

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * @author Rob
 * 
 */
public class BlockPistoned extends BBAction {
    
    /**
     * 
     * @param player
     * @param block
     * @param direction
     */
    public BlockPistoned(final BBPlayerInfo player, final Block block, final BlockFace direction) {
        super(player, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()) + "\t" + direction.name());
    }
    
    /**
     * 
     */
    public BlockPistoned() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param pi
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     */
    public BlockPistoned(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(pi, world, x, y, z, type, data);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#rollback(org.bukkit.World)
     */
    @Override
    public void rollback(final World world) {
        
        final Chunk c = world.getChunkAt(x, z);
        if (!world.isChunkLoaded(c)) {
            world.loadChunk(c);
        }
        // Get the block we're about to replace
        final Block rb = world.getBlockAt(x, y, z);
        rb.setTypeIdAndData(type, getData(), true);
    }
    
    /**
     * @return
     */
    private byte getData() {
        final String dat = data.split("\t")[0];
        
        return Byte.valueOf(dat);
    }
    
    /**
     * @return
     */
    private Vector getDirectionOffset() {
        final BlockFace d = getDirection();
        return new Vector(d.getModX(), d.getModY(), d.getModZ());
    }
    
    /**
     * @return
     */
    private BlockFace getDirection() {
        final String dname = data.split("\t")[1];
        
        return BlockFace.valueOf(dname);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#redo(org.bukkit.Server)
     */
    @Override
    public void redo(final Server server) {
        final Vector d = getDirectionOffset();
        final World w = server.getWorld(world);
        Chunk c = w.getChunkAt(x, z);
        if (!w.isChunkLoaded(c)) {
            w.loadChunk(c);
        }
        c = w.getChunkAt(x + d.getBlockX(), z + d.getBlockZ());
        if (!w.isChunkLoaded(c)) {
            w.loadChunk(c);
        }
        // Get the block we're about to replace
        final Block rb = w.getBlockAt(x + d.getBlockX(), y + d.getBlockY(), z + d.getBlockZ());
        rb.setTypeIdAndData(type, getData(), true);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        return ActionCategory.BLOCKS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "A block moved by a piston";
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new BlockPistoned(pi, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return String.format(" - A piston moved a " + Material.getMaterial(type).name() + ".");
    }
}
