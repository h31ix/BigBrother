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
import java.sql.SQLException;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.datasource.BBDB;

/**
 * @author Rob
 * 
 */
public class ActionPostgreSQL extends ActionTable {
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.ActionTable#addAction(java.lang.String, java.lang.String, int)
     */
    @Override
    protected int addAction(String pluginName, String actionName, int catID, String actionDesc) {
        PreparedStatement ps = null;
        try {
            ps = BBDB.prepare("INSERT INTO " + getActualTableName() + " (actName,actPlugin,actCategory, actDescription) VALUES (?,?,?,?)");
            ps.setString(1, actionName);
            ps.setString(2, pluginName);
            ps.setInt(3, catID);
            ps.setString(4, actionDesc);
            ps.executeUpdate();
            BBDB.commit();
        } catch (SQLException e) {
            BBLogging.severe("ActionPostgreSQL.addAction", e);
        } finally {
            BBDB.cleanup("ActionPostgreSQL.addAction", ps, null);
        }
        return 0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#onLoad()
     */
    @Override
    protected void onLoad() {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.DBTable#getCreateSyntax()
     */
    @Override
    public String getCreateSyntax() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.ActionTable#init()
     */
    @Override
    public void init() {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.tablemgrs.ActionTable#addActionForceID(java.lang.String, java.lang.String, int, int)
     */
    @Override
    protected void addActionForceID(String pluginName, String actionName, int catID, int ID, String description) {
        // TODO Auto-generated method stub
    }
    
}
