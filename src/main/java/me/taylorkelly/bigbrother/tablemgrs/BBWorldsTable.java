package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBSettings.DBMS;
import me.taylorkelly.bigbrother.datasource.BBDB;

public abstract class BBWorldsTable extends DBTable {
    
    // Singletons :D
    private static BBWorldsTable instance = null;
    
    @Override
    protected String getActualTableName() {
        return "bbworlds";
    }
    
    public static BBWorldsTable getInstance() {
        if (instance == null) {
            if (BBDB.usingDBMS(DBMS.MYSQL))
                instance = new BBWorldsMySQL();
            else if (BBDB.usingDBMS(DBMS.POSTGRES))
                instance = new BBWorldsPostgreSQL();
            else
                instance = new BBWorldsH2();
        }
        return instance;
    }
    
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " ("
                + "id INTEGER PRIMARY KEY,"
                + "name varchar(50) NOT NULL DEFAULT 'world');";
    }
    
    public HashMap<String, Integer> getWorlds() {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        ResultSet set = null;
        try {
            set = BBDB.executeQuery(getSelectWorldsQuery());
            int size = 0;
            while (set.next()) {
                size++;
                int index = set.getInt("id");
                String name = set.getString("name");
                ret.put(name, index);
            }
        } catch (SQLException ex) {
            BBLogging.severe("World Load Exception", ex);
        } finally {
            BBDB.cleanup("World Load", null, set);
        }
        
        BBLogging.debug("Loaded worlds: " + ret.keySet().toString());
        return ret;
    }
    
    protected abstract String getSelectWorldsQuery();
    
    public boolean insertWorld(int index, String world) {
        return BBDB.tryUpdate(getInsertWorldQuery(), index, world);
    }
    
    protected abstract String getInsertWorldQuery();
    
}
