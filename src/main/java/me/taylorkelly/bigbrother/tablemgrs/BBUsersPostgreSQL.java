package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.BBDB;

public class BBUsersPostgreSQL extends BBUsersMySQL {
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE \"" + getTableName() + "\" (" + "\"id\" SERIAL," + "\"name\" varchar(32) NOT NULL DEFAULT 'Player',\"flags\" INT NOT NULL DEFAULT '0'," + "PRIMARY KEY (\"id\")," + "UNIQUE(\"name\"));";
    }
    
    /**
     * @see BBUsersMySQL changed quotes
     */
    @Override
    public BBPlayerInfo getUserFromDB(String name) {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE \"name\"=?";
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql, name);
            
            if (!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Error trying to find the user `" + name + "`.", e);
        } finally {
            BBDB.cleanup("BBUsersMySQL.getUserFromDB(string)", null, rs);
        }
        return null;
    }
    
    @Override
    public BBPlayerInfo getUserFromDB(int id) {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE \"id\"=?;";
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql, id);
            
            if (!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find user #" + id + ".", e);
        } finally {
            BBDB.cleanup("BBUsersMySQL.getUserFromDB(int)", null, rs);
        }
        return null;
    }
    
    @Override
    protected void do_addOrUpdatePlayer(BBPlayerInfo pi) {
        if (pi.getNew() && (getUserFromDB(pi.getName()) == null)) {
            String sql = "INSERT INTO " + getTableName() + " (name,flags) VALUES (?,?)";
            BBDB.executeUpdate(sql, pi.getName(), pi.getFlags());
            
        } else {
            String sql = "UPDATE " + getTableName() + " SET flags = ? WHERE id=?";
            BBDB.executeUpdate(sql, pi.getFlags(), pi.getID());
        }
    }
}
