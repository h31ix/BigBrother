/**
* <A line to describe this file>
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.ActionProvider.ActionData;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datasource.BBDB;

/**
 * @author Rob
 *
 */
public class ActionMySQL extends ActionTable {
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.ActionTable#addAction(java.lang.String, java.lang.String, int)
     */
    @Override
    protected int addAction(String pluginName, String actionName, int catID) {
        PreparedStatement ps = null;
        try {
            ps=BBDB.prepare("INSERT INTO "+getTableName()+" (actName,actPlugin,actCategory) VALUES (?,?,?)");
            ps.setString(1, actionName);
            ps.setString(2, pluginName);
            ps.setInt(3, catID);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            BBLogging.severe("Can't add a new action: ",e);
        } finally {
            BBDB.cleanup("ActionMySQL", ps, null);
        }
        
        ResultSet rs = null;
        try {
            rs = BBDB.executeQuery("SELECT LAST_INSERT_ID()");
            return rs.getInt(1);
        } catch (SQLException e) {
            BBLogging.severe("Can't add a new action: ",e);
        } finally {
            BBDB.cleanup("ActionMySQL", null, rs);
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#onLoad()
     */
    @Override
    protected void onLoad() {}
    
    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        return "CREATE TABLE "+getTableName()+" (" +
        		"actID INTEGER PRIMARY KEY AUTO_INCREMENT," +
        		"actName TEXT UNIQUE INDEX," +
        		"actPlugin TEXT," +
        		"actCategory INTEGER," +
        		"actDecription TEXT"+
        		") ENGINE="+BBDB.engine;
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.tablemgrs.ActionTable#init()
     */
    @Override
    public void init() {
        ResultSet rs = null;
        try {
            rs = BBDB.executeQuery("SELECT * FROM "+getTableName());
            while(rs.next()) {
                ActionData dat = new ActionData(rs.getString("actPlugin"), ActionCategory.values()[rs.getInt("actCategory")], rs.getString("actName"));
                ActionProvider.Actions.put(rs.getInt("actID"),dat);
            }
        } catch (SQLException e) {
            BBLogging.severe("Can't add a new action: ",e);
        } finally {
            BBDB.cleanup("ActionMySQL", null, rs);
        }
    }
    
}
