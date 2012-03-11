package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.BBDB;

public abstract class BBWorldsTable extends DBTable {
    
    private static final int VERSION = 6;
    // Singletons :D
    private static BBWorldsTable instance = null;
    
    public BBWorldsTable() {
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
    
    @Override
    protected String getActualTableName() {
        return "bbworlds";
    }
    
    public static BBWorldsTable getInstance() {
        if (instance == null) {
            if (BBDB.usingDBMS(DBMS.POSTGRES)) {
                instance = new BBWorldsPostgreSQL();
            } else {
                instance = new BBWorldsMySQL();
            }
        }
        return instance;
    }
    
    public static void cleanup() {
        instance = null;
    }
    
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" + "id INTEGER PRIMARY KEY," + "name varchar(50) NOT NULL DEFAULT 'world');";
    }
    
    public HashMap<String, Integer> getWorlds() {
        final HashMap<String, Integer> ret = new HashMap<String, Integer>();
        ResultSet set = null;
        try {
            set = BBDB.executeQuery(getSelectWorldsQuery());
            while (set.next()) {
                final int index = set.getInt("id");
                final String name = set.getString("name");
                ret.put(name, index);
            }
        } catch (final SQLException ex) {
            BBLogging.severe("World Load Exception", ex);
        } finally {
            BBDB.cleanup("World Load", null, set);
        }
        
        BBLogging.debug("Loaded worlds: " + ret.keySet().toString());
        return ret;
    }
    
    protected abstract String getSelectWorldsQuery();
    
    public boolean insertWorld(final int index, final String world) {
        return BBDB.tryUpdate(getInsertWorldQuery(), index, world);
    }
    
    protected abstract String getInsertWorldQuery();
    
}
