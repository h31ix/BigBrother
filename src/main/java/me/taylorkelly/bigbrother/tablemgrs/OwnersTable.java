/**
 * Ownership API
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
package me.taylorkelly.bigbrother.tablemgrs;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.BBDB;

/**
 * @author Rob
 * 
 */
public abstract class OwnersTable extends DBTable {
    private static final int VERSION = 1;
    // Singletons :D
    private static OwnersTable instance = null;
    
    public OwnersTable() {
        if (BBDB.needsUpdate(BBSettings.dataFolder, getActualTableName(), VERSION)) {
            drop();
        }
        if (!tableExists()) {
            BBLogging.info("Building `" + getTableName() + "` table...");
            createTable();
        } else {
            BBLogging.debug("`" + getTableName() + "` table already exists");
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getActualTableName()
     */
    @Override
    protected String getActualTableName() {
        return "ownership";
    }
    
    public static OwnersTable getInstance() {
        if (instance == null) {
            if (BBDB.usingDBMS(DBMS.POSTGRES)) {
                instance = new OwnersPostgreSQL();
            } else {
                instance = new OwnersMySQL();
            }
        }
        return instance;
    }
    
    public static void cleanup() {
        instance = null;
    }
    
    /**
     * @param world
     * @param x
     * @param y
     * @param z
     * @param playerID
     */
    public static void set(final int world, final int x, final int y, final int z, final int playerID) {
        getInstance().setBlockOwner(world, x, y, z, playerID);
    }
    
    /**
     * @param world
     * @param x
     * @param y
     * @param z
     * @param playerID
     */
    protected abstract void setBlockOwner(int world, int x, int y, int z, int playerID);
    
    /**
     * @param name
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int get(final int world, final int x, final int y, final int z) {
        return getInstance().getBlockOwner(world, x, y, z);
    }
    
    /**
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    protected abstract int getBlockOwner(int world, int x, int y, int z);
    
    /**
     * @param i
     * @param blockX
     * @param blockY
     * @param blockZ
     */
    public static void remove(final int wldID, final int x, final int y, final int z) {
        getInstance().removeBlockOwner(wldID, x, y, z);
    }
    
    /**
     * @param wldID
     * @param x
     * @param y
     * @param z
     * @return
     */
    protected abstract void removeBlockOwner(int wldID, int x, int y, int z);
}
