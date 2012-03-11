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

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.BBDB;

/**
 * actID actName actCategory actPlugin actDescription
 * 
 * @author Rob
 * 
 */
public abstract class ActionTable extends DBTable {
    private static ActionTable instance = null;
    
    public static enum LegacyAction {
        BLOCKBROKEN,
        BLOCKPLACED,
        DESTROYSIGNTEXT,
        TELEPORT,
        DELTACHEST,
        COMMAND,
        CHAT,
        DISCONNECT,
        LOGIN,
        DOOROPEN,
        BUTTONPRESS,
        LEVERSWITCH,
        CREATESIGNTEXT,
        LEAFDECAY,
        FLINTANDSTEEL,
        TNTEXPLOSION,
        CREEPEREXPLOSION,
        MISCEXPLOSION,
        OPENCHEST,
        BLOCKBURN,
        FLOW,
        DROPITEM,
        PICKUPITEM,
        SIGNDESTROYED,
        ENDERMANGRIEF
    }
    
    public ActionTable() {
        if (!tableExists()) {
            BBLogging.info("Building `" + getTableName() + "` table...");
            createTable();
        } else {
            BBLogging.debug("`" + getTableName() + "` table already exists");
        }
    }
    
    /**
     * Convert from legacy static BBDataBlock IDs (which correspond with an enum) to the new dynamically-allocated system.
     */
    private static void doActionIDUpdates() {
        BBLogging.warning("Preparing to update from legacy action IDs to the newer, dynamically-assigned ones.");
        BBLogging.warning("This WILL take a long time, so if you do not want to wait, please delete your bbdata table and restart.");
        
        final String tmpTable = "_TMP_" + BBDataTable.getInstance().getActualTableName();
        final String tbl = BBDataTable.getInstance().getTableName();
        // Create temporary table.
        BBDB.executeUpdate(BBDataTable.getInstance().getCreateSyntax().replace(tbl, tmpTable));
        
        // Begin looping through each legacy ID
        for (final LegacyAction act : LegacyAction.values()) {
            // Locate an Action that corresponds to each ID, based on name.
            final int id = ActionProvider.findActionID(act.name().toLowerCase());
            if (id == 3) {
                BBLogging.severe("Unable to locate an action that corresponds with " + act.name() + "!");
            } else {
                BBDB.executeUpdate("INSERT INTO " + tmpTable + " (id, date, player, action, world, x, y, z, type, data, rbacked) (SELECT id, date, player,?,world,x,y,z,type,data,rbacked FROM " + tbl + " WHERE id=?)", id, act.ordinal());
            }
        }
        
        BBDB.executeUpdate("DROP TABLE " + tbl);
        BBDB.executeUpdate("RENAME TABLE " + tmpTable + " TO " + tbl);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getActualTableName()
     */
    @Override
    protected String getActualTableName() {
        return "bbactions";
    }
    
    public static ActionTable getInstance() {
        if (instance == null) {
            //BBLogging.info("BBSettings.databaseSystem="+BBSettings.databaseSystem.toString());
            if (BBDB.usingDBMS(DBMS.POSTGRES)) {
                instance = new ActionPostgreSQL();
            } else {
                instance = new ActionMySQL();
            }
        }
        return instance;
    }
    
    public static void cleanup() {
        instance = null;
    }
    
    /**
     * @param act Action to add to the database
     * @return int ID of the Action
     */
    public static int add(final String pluginName, final String actionName, final int catID, final String description) {
        return getInstance().addAction(pluginName, actionName, catID, description);
    }
    
    /**
     * 
     * @param pluginName
     * @param actionName
     * @param catID
     * @return
     */
    protected abstract int addAction(String pluginName, String actionName, int catID, String description);
    
    public abstract void init();
    
    /**
     * Only for use with BB IDs.
     * 
     * @param pluginName
     * @param actionName
     * @param catID
     * @param ID
     * @param description
     */
    protected abstract void addActionForceID(String pluginName, String actionName, int catID, int ID, String description);
    
    /**
     * @param pluginName
     * @param actionName
     * @param catID
     * @param actID
     */
    public static void addForcedID(final String pluginName, final String actionName, final int catID, final int actID, final String description) {
        getInstance().addActionForceID(pluginName, actionName, catID, actID, description);
    }
    
    /**
     * Change bbdata table, if needed.
     */
    public static void performPostponedUpdates() {
        BBDB.getVersion(BBSettings.dataFolder, ActionTable.getInstance().getActualTableName());
        final int dataVersion = BBDB.getVersion(BBSettings.dataFolder, BBDataTable.getInstance().getActualTableName());
        
        if (dataVersion == 6) {
            doActionIDUpdates();
        }
    }
}
