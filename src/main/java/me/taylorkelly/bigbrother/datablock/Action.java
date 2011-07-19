/**
* Action class
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

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datasource.ActionSender;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.World;

/**
 * An action that occurred on a world at a certain location.
 * @author N3X15
 */
public abstract class Action {
    public final static String ENVIRONMENT = "Environment";
    
    public BBPlayerInfo player=BBPlayerInfo.ENVIRONMENT;
    public int x=0;
    public int y=0;
    public int z=0;
    public String world="BB_GLOBAL";
    public int type=-1;
    public String data="";
    public long date=0;
    
    public Action(){}
    public Action(String player, String world, int x, int y, int z, int type, String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = BBUsersTable.getInstance().getUserByName(player);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }
    public Action(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        this.date = System.currentTimeMillis() / 1000;
        this.player = player;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
    }

    public void send() {
        if(!isDisabled())
            ActionSender.offer(this);
    }
    
    public final boolean isDisabled() {
        return ActionProvider.disabledActions.contains(ActionProvider.getActionID(this));
    }

    /**
     * Perform a rollback of this action in world.
     * @param world
     */
    public abstract void rollback(World world);

    /**
     * Redo the action (undo the rollback)
     * @param server
     */
    public abstract void redo(Server server);
    
    /**
     * Name of this action (Uppercase letters and underscores only)
     * @return
     */
    public abstract String getName();
    
    /**
     * Category of this action
     * @return
     */
    public abstract ActionCategory getCategory();
    
    /**
     * Category of this action
     * @return
     */
    public abstract String getDescription();
}
