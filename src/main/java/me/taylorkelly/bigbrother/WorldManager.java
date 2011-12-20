/**
 * World->ID list.
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

import java.util.Collection;
import java.util.HashMap;

import me.taylorkelly.bigbrother.tablemgrs.BBWorldsTable;

public class WorldManager {
    private HashMap<String, Integer> worldMap;
    
    public WorldManager() {
        worldMap = BBWorldsTable.getInstance().getWorlds();
    }
    
    /**
     * Returns the BB index of the world to use (starts at 0 and goes up). If BB has seen it before, it will use the key that it already had paired. Otherwise it will designate a new key, and save that key to bbworlds for later usage
     * 
     * @param world The name of the world
     * @return The index of the world
     */
    public int getWorld(String world) {
        if (worldMap.containsKey(world))
            return worldMap.get(world);
        else {
            int nextKey = 0;
            if (!worldMap.isEmpty()) {
                nextKey = getMax(worldMap.values()) + 1;
            }
            saveWorld(world, nextKey);
            worldMap.put(world, nextKey);
            return nextKey;
        }
    }
    
    /**
     * Generic max finder for a collection. Only works with positive numbers (which we'd be dealing with)
     * 
     * @param values Collection of values
     * @return The max of those numbers (or -1 if it's empty)
     */
    public static int getMax(Collection<Integer> values) {
        int max = -1;
        for (Integer value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    
    private boolean saveWorld(String world, int index) {
        return BBWorldsTable.getInstance().insertWorld(index, world);
    }
}
