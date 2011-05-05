/**
 * 
 */
package me.taylorkelly.bigbrother.tablemgrs;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.BBDB;

/**
 * @author N3X15
 *
 */
public class BBUsersMySQL extends BBUsersTable {
    public final int revision = 1;
    public String toString() {
        return "BBUsers MySQL Driver r"+Integer.valueOf(revision);
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#onLoad()
     */
    @Override
    protected void onLoad() {
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE `"+getTableName()+"` ("
        + "`id` INT NOT NULL AUTO_INCREMENT," 
        + "`name` varchar(32) NOT NULL DEFAULT 'Player'," 
        + "`flags` INT NOT NULL DEFAULT '0',"
        + "PRIMARY KEY (`id`),"
        + "UNIQUE(`name`));"; //Engine doesn't matter, really.
    }

    @Override
    public BBPlayerInfo getUserFromDB(String name) {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM "+getTableName()+" WHERE `name`=?";
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql,name);
            
            if(!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Error trying to find the user `"+name+"`.", e);
        } finally {
            BBDB.cleanup( "BBUsersMySQL.getUserFromDB(string)",null, rs );
        }
        return null;
    }

    @Override
    protected void do_addOrUpdatePlayer(BBPlayerInfo pi) {
        try {
            if(pi.getNew() && getUserFromDB(pi.getName())==null) {
                BBDB.executeUpdate("INSERT INTO "+getTableName()+" (name,flags) VALUES (?,?) ON DUPLICATE KEY UPDATE flags=VALUES(flags)",pi.getName(),pi.getFlags());
            } else {
                BBDB.executeUpdate("UPDATE "+getTableName()+" SET flags = ? WHERE id=?", pi.getFlags(), pi.getID());
            }
            BBDB.commit();
        } catch (SQLException e) {
            BBLogging.severe("Can't update the user `"+pi.getName()+"`.", e);
        }
    }

    @Override
    public BBPlayerInfo getUserFromDB(int id) {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM " + getTableName() + " WHERE `id`=?;";
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql,id);
            if(!rs.next())
                return null;
            
            return new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
            
        } catch (SQLException e) {
            BBLogging.severe("Can't find user #"+id+".", e);
        } finally {
            BBDB.cleanup( "BBUsersMySQL.getUserFromDB(int)",null, rs );
        }
        return null;
    }
    
    public int getSubversion(File file) {
        try {
            Scanner scan = new Scanner(file);
            String version = scan.nextLine();
            try {
                int numVersion = Integer.parseInt(version);
                return numVersion;
            } catch (Exception e) {
                return 0;
            }
            
        } catch (FileNotFoundException e) {
            return 0;
        }
    }

    @Override
    protected void loadCache() {
        ResultSet rs = null;
        try {
            String sql = "SELECT id,name,flags FROM "+getTableName();
            BBLogging.debug(sql);
            rs = BBDB.executeQuery(sql);
            
            while(rs.next()){
                BBPlayerInfo pi = new BBPlayerInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("flags"));
                this.knownPlayers.put(pi.getID(), pi);
                this.knownNames.put(pi.getName(),pi.getID());
            }
        } catch (SQLException e) {
            BBLogging.severe("Error trying to load the user/name cache.", e);
        } finally {
            BBDB.cleanup( "BBUsersMySQL.getUserFromDB(string)", null, rs );
        }
    }
}
