package me.taylorkelly.bigbrother.tablemgrs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.BBDB;

public class BBUsersH2 extends BBUsersTable {
    public final int revision = 1;
    
    public String toString() {
        return "BBUsers H2 Driver r" + Integer.valueOf(revision);
    }
    
    @Override
    protected void onLoad() {
    }
    
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (" + "`id` INT AUTO_INCREMENT PRIMARY KEY," + "`name` varchar(32) NOT NULL DEFAULT 'Player'," + "`flags` INT NOT NULL DEFAULT '0');" + "CREATE UNIQUE INDEX IF NOT EXISTS idxUsername ON `" + getTableName() + "` (`name`)"; // ANSI
    }
    
    @Override
    public BBPlayerInfo getUserFromDB(String name) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE `name`=?";
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql, name);
            
            if (!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Error trying to find the user `" + name + "`.", e);
        } finally {
            BBDB.cleanup("BBUsersH2.getUserFromDB(string)", ps, rs);
        }
        return null;
    }
    
    @Override
    protected void do_addOrUpdatePlayer(BBPlayerInfo pi) {
        PreparedStatement ps = null;
        try {
            if (pi.getNew() && getUserFromDB(pi.getName()) == null) {
                ps = BBDB.prepare("INSERT INTO " + getTableName() + " (name,flags) VALUES (?,?)");
                ps.setString(1, pi.getName());
                ps.setInt(2, pi.getFlags());
            } else {
                ps = BBDB.prepare("UPDATE " + getTableName() + " SET flags = ? WHERE id=?");
                ps.setInt(1, pi.getFlags());
                ps.setInt(2, pi.getID());
            }
            BBLogging.debug(ps.toString());
            ps.executeUpdate();
            BBDB.commit();
        } catch (SQLException e) {
            BBLogging.severe("Can't update the user `" + pi.getName() + "`.", e);
        } finally {
            BBDB.cleanup("BBUsersH2.do_addOrUpdatePlayer", ps, null);
        }
    }
    
    @Override
    public BBPlayerInfo getUserFromDB(int id) {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE `id`=?";
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql, id);
            if (!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find user #" + id + ".", e);
        } finally {
            BBDB.cleanup("BBUsersH2.getUserFromDB(int)", null, rs);
        }
        return null;
    }
    
    @Override
    protected void loadCache() {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName();
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql);
            
            while (rs.next()) {
                BBPlayerInfo pi = new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
                this.knownPlayers.put(pi.getID(), pi);
                this.knownNames.put(pi.getName(), pi.getID());
            }
        } catch (SQLException e) {
            BBLogging.severe("Error trying to load the user/name cache.", e);
        } finally {
            BBDB.cleanup("BBUsersH2.getUserFromDB(string)", null, rs);
        }
    }
    
}
