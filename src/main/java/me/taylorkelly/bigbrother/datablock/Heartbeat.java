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

package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.Server;
import org.bukkit.World;

/**
 * x = current online players
 * y = max players
 * data = Current time in Milliseconds
 * @author Rob
 *
 */
public class Heartbeat extends BBAction {

    /**
     * 
     */
    public Heartbeat(BigBrother plugin) {
        // Populate fields
        super(BBPlayerInfo.ENVIRONMENT, "BB_GLOBAL", 0, 0, 0, 0, "");
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        data=sb.toString();
        type=0;
        x=plugin.getServer().getOnlinePlayers().length;
        y=plugin.getServer().getMaxPlayers();
    }

    /**
     * For ActionProvider registration only.
     */
    public Heartbeat() {
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#rollback(org.bukkit.World)
     */
    @Override
    public void rollback(World world) {
        
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#redo(org.bukkit.Server)
     */
    @Override
    public void redo(Server server) {
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        return "Heartbeat";
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        return ActionCategory.HIDDEN;
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return "Keeps the connection to the database alive, also provides useful statistics!";
    }
    
    @Override
    public String toString() {
        return "[HEARTBEAT]";
    }
}
