package me.taylorkelly.bigbrother.tablemgrs;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.datasource.BBDB;

public abstract class DBTable {
    
    /**
     * Get the name of the table, minus prefix.
     * 
     * @return
     */
    protected abstract String getActualTableName();
    
    /**
     * Specify your on-load procedures in here.
     */
    protected abstract void onLoad();
    
    /**
     * Specify the CREATE TABLE syntax here.
     * 
     * @return
     */
    public abstract String getCreateSyntax();
    
    public String getTableName() {
        return BBDB.applyPrefix(getActualTableName());
    }
    
    public boolean tableExists() {
        return BBDB.tableExists(getTableName());
    }
    
    public void createTable() {
        BBDB.executeUpdate(getCreateSyntax());
    }
    
    public boolean executeUpdate(final String desc, final String sql, final Object[] args) {
        return BBDB.tryUpdate(sql, args);
    }
    
    public boolean executeUpdate(final String desc, final String sql) {
        return executeUpdate(desc, sql, new Object[] {});
    }
    
    public void drop() {
        BBLogging.info("Dropping table " + getTableName());
        BBDB.executeUpdate("DROP TABLE IF EXISTS " + getTableName());
        createTable();
    }
}
