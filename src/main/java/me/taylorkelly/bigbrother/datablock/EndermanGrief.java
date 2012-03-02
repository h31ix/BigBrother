/**
 * Terrible things done by tall, handsome black people from the universe next door
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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * @author Rob
 * 
 */
public class EndermanGrief extends Action {
    
    private boolean isPlace;
    
    /**
     * 
     */
    public EndermanGrief() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     */
    public EndermanGrief(String player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     */
    public EndermanGrief(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
        // TODO Auto-generated constructor stub
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#rollback(org.bukkit.World)
     */
    @Override
    public void rollback(World world) {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#redo(org.bukkit.Server)
     */
    @Override
    public void redo(Server server) {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "EndermanGrief";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        // TODO Auto-generated method stub
        return ActionCategory.ENTITY;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return (isPlace) ? "Enderman Placed" : "Enderman Stole";
    }
    
    /**
     * @param entity
     * @param block
     */
    public static void createPickup(Entity entity, Block block) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * @param entity
     * @param location
     * @param type
     */
    public static void createPlace(Entity entity, Location location, Material type) {
        // TODO Auto-generated method stub
        
    }
    
}
