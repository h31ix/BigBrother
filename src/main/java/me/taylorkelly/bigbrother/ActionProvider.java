/**
* A Bukkit plugin that provides a logging interface
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

package me.taylorkelly.bigbrother;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.tablemgrs.ActionTable;

import org.bukkit.plugin.Plugin;

/**
 * @author Rob
 *
 */
public abstract class ActionProvider implements Plugin {
    public static class ActionData {
        public ActionProvider plugin=null;
        public String pluginName;
        public Action action=null;
        public String actionName;
        public ActionCategory category;

        public ActionData(ActionProvider plugin, Action action) {
            this(plugin.getDescription().getFullName(),action.getCategory(),action.getName());
            this.plugin=plugin;
            this.action=action;
        }
        public ActionData(String plugin, ActionCategory category, String action) {
            this.pluginName=plugin;
            this.category=category;
            this.actionName=action;
        }
        
        @Override
        public boolean equals(Object o) {
            if(o instanceof ActionData) {
                return ((ActionData) o).pluginName.equalsIgnoreCase(pluginName) 
                && ((ActionData) o).actionName.equalsIgnoreCase(actionName) 
                &&   ((ActionData)o).category == category;
            }
            return false;
        }
    }
    
    public static Map<Integer,ActionData> Actions = new HashMap<Integer,ActionData>();
    
    public abstract Action getAction(String actionName, BBPlayerInfo player, String world, int x, int y, int z, int type, String data);
    
    protected final void registerAction(ActionProvider plugin, Action action) {
        ActionData dat = new ActionData(plugin,action);
        if(!Actions.containsValue(dat)) {
            int id=ActionTable.add(plugin.getDescription().getFullName(),action.getName(),action.getCategory().ordinal());
            Actions.put(id, dat);
        }
    }  

    public static int getActionID(Action action) {
        for(Entry<Integer, ActionData> e:Actions.entrySet()) {
            if(e.getValue().action == action) {
                return e.getKey();
            }
        }
        BBLogging.severe("Unknown Action: "+action.getName());
        return -1;
    }

    /**
     * Find the LoggingProvider for the ActionID provided, and get the Action.
     * @param actionID
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     * @return
     */
    public static Action findAndProvide(int actionID, BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        if(!Actions.containsKey(actionID)) return null;
        ActionProvider provider = Actions.get(actionID).plugin;
        return provider.getAction(Actions.get(actionID).actionName, player, world, x, y, z, type, data);
    }

    /**
     * @param string
     * @return
     */
    public static int findActionID(String string) {
        for(Entry<Integer, ActionData> e:Actions.entrySet()) {
            if(e.getValue().action.getName().equalsIgnoreCase(string) || e.getValue().action.getName().contains(string)) {
                return e.getKey();
            }
        }
        return -1;
    }
}
