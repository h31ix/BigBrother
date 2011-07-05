/**
* Actions table manager
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
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.datasource.BBDB;

/**
 * actID
 * actName
 * actCategory
 * actPlugin
 * actDescription
 * @author Rob
 *
 */
public abstract class ActionTable extends DBTable {
    private static final int VERSION = 1;
    private static ActionTable instance=null;
    
    public ActionTable() {
        if(BBDB.needsUpdate(BBSettings.dataFolder, getActualTableName(), VERSION))
            drop();
        if (!tableExists()) {
            BBLogging.info("Building `"+getTableName()+"` table...");
            createTable();
        } else {
            BBLogging.debug("`"+getTableName()+"` table already exists");
        }
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getActualTableName()
     */
    @Override
    protected String getActualTableName() {
        return "actions";
    }
    
    public static ActionTable getInstance() {
        if(instance==null) {
            //BBLogging.info("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if(BBDB.usingDBMS(DBMS.MYSQL))
                instance=new ActionMySQL();
            else if(BBDB.usingDBMS(DBMS.POSTGRES))
                instance=new ActionPostgreSQL();
            else
                instance=new ActionH2();
        }
        return instance;
    }
    
    public static void cleanup() {
        instance=null;
    }

    /**
     * @param act Action to add to the database
     * @return int ID of the Action
     */
    public static int add(String pluginName,String actionName,int catID) {
        return getInstance().addAction(pluginName,actionName,catID);
    }

    /**
     * @param act
     * @return
     */
    protected abstract int addAction(String pluginName,String actionName,int catID);
    
    public abstract void init();
}
