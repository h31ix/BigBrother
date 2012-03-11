/**
 * A listener-like Action factory
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public abstract class ActionProvider {
    public static class ActionData {
        public Plugin plugin = null;
        public String pluginName;
        public Action action = null;
        public String actionName;
        public ActionCategory category;
        private ActionProvider provider;
        
        public ActionData(final Plugin plugin, final ActionProvider ap, final Action action) {
            this(plugin.getDescription().getName(), action.getCategory(), action.getName());
            this.plugin = plugin;
            provider = ap;
            this.action = action;
        }
        
        public ActionData(final String plugin, final ActionCategory category, final String action) {
            pluginName = plugin;
            this.category = category;
            actionName = action;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof ActionData)
                return ((ActionData) o).pluginName.equalsIgnoreCase(pluginName) && ((ActionData) o).actionName.equalsIgnoreCase(actionName) && (((ActionData) o).category == category);
            return false;
        }
    }
    
    public static Map<Integer, ActionData> Actions = new HashMap<Integer, ActionData>();
    public static List<Integer> disabledActions = new ArrayList<Integer>();
    private final Plugin plugin;
    
    /**
     * Override this to provide custom Actions.
     * 
     * @param actionName
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     * @return
     */
    public abstract Action getAction(String actionName, BBPlayerInfo player, String world, int x, int y, int z, int type, String data);
    
    public ActionProvider(final Plugin p) {
        plugin = p;
    }
    
    public final void disable() {
        final Map<Integer, ActionData> copy = new HashMap<Integer, ActionData>();
        copy.putAll(Actions);
        for (final Entry<Integer, ActionData> e : copy.entrySet()) {
            if (e.getValue().plugin == plugin) {
                Actions.remove(e.getKey());
            }
        }
    }
    
    /**
     * Called to register custom Actions with BigBrother.
     * 
     * @param plugin
     * @param provider
     * @param action
     */
    protected final void registerAction(final Plugin plugin, final ActionProvider provider, final Action action) {
        final ActionData dat = new ActionData(plugin, provider, action);
        int id = findActionID(action.getName());
        if (id == -1) {
            id = ActionTable.add(plugin.getDescription().getName(), action.getName(), action.getCategory().ordinal(), action.getDescription());
            BBLogging.info("Action #" + id + " - " + action.getName());
            Actions.put(id, dat);
        } else {
            Actions.remove(id);
            Actions.put(id, dat);
        }
    }
    
    /**
     * Get the ID # of an action.
     * 
     * @param action
     * @return
     */
    public static int getActionID(final Action action) {
        for (final Entry<Integer, ActionData> e : Actions.entrySet()) {
            if (e.getValue().action.getName().equalsIgnoreCase(action.getName()))
                return e.getKey();
        }
        BBLogging.severe("Unknown Action: " + action.getName());
        return -1;
    }
    
    /**
     * Find the LoggingProvider for the ActionID provided, and get the Action.
     * 
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
    public static Action findAndProvide(final int actionID, final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        if (!Actions.containsKey(actionID))
            return null;
        final ActionProvider provider = Actions.get(actionID).provider;
        if (provider == null)
            return null;
        return provider.getAction(Actions.get(actionID).actionName, player, world, x, y, z, type, data);
    }
    
    /**
     * Find the ID of the provided action name
     * 
     * @param actionName
     * @return
     */
    public static int findActionID(final String actionName) {
        for (final Entry<Integer, ActionData> e : Actions.entrySet()) {
            if (e.getValue().actionName.equalsIgnoreCase(actionName))
                return e.getKey();
        }
        return -1;
    }
    
    /**
     * Only for use by BigBrother! Use registerAction instead!
     * 
     * @param plugin
     * @param bbActionProvider
     * @param deltaChest
     * @param i
     */
    protected void registerActionForceID(final BigBrother plugin, final BBActionProvider provider, final Action action, final int id) {
        final ActionData dat = new ActionData(plugin, provider, action);
        if (!Actions.containsKey(id)) {
            ActionTable.addForcedID(plugin.getDescription().getName(), action.getName(), action.getCategory().ordinal(), id, action.getDescription());
            Actions.put(id, dat);
        } else {
            Actions.remove(id);
            Actions.put(id, dat);
        }
    }
    
    /**
     * For loading the disabledActions list.
     */
    public static void loadDisabled(final BetterConfig cfg) {
        List<String> actions;
        if (cfg.getValue("general.disabled-actions") == null) {
            actions = new ArrayList<String>();
            if (!cfg.getBoolean("watched.blocks.block-break", true)) {
                actions.add("BrokenBlock");
            }
            if (!cfg.getBoolean("watched.blocks.block-place", true)) {
                actions.add("PlacedBlock");
            }
            if (!cfg.getBoolean("watched.player.teleport", true)) {
                actions.add("Teleport");
            }
            if (!cfg.getBoolean("watched.blocks.chest-changes", true)) {
                actions.add("DeltaChest");
            }
            if (!cfg.getBoolean("watched.chat.commands", true)) {
                actions.add("Command");
            }
            if (!cfg.getBoolean("watched.chat.chat", true)) {
                actions.add("Chat");
            }
            if (!cfg.getBoolean("watched.player.login", true)) {
                actions.add("Login");
            }
            if (!cfg.getBoolean("watched.player.disconnect", true)) {
                actions.add("Disconnect");
            }
            if (!cfg.getBoolean("watched.misc.door-open", false)) {
                actions.add("DoorOpen");
            }
            if (!cfg.getBoolean("watched.misc.button-press", false)) {
                actions.add("ButtonPress");
            }
            if (!cfg.getBoolean("watched.misc.lever-switch", false)) {
                actions.add("LeverSwitch");
            }
            if (!cfg.getBoolean("watched.misc.flint-logging", true)) {
                actions.add("FlintAndSteel");
            }
            if (!cfg.getBoolean("watched.environment.leaf-decay", false)) {
                actions.add("LeafDecay");
            }
            if (!cfg.getBoolean("watched.explosions.tnt", true)) {
                actions.add("TNTExplosion");
            }
            if (!cfg.getBoolean("watched.explosions.creeper", true)) {
                actions.add("CreeperExplosion");
            }
            if (!cfg.getBoolean("watched.explosions.misc", true)) {
                actions.add("MiscExplosion");
            }
            if (!cfg.getBoolean("watched.player.drop-item", false)) {
                actions.add("DropItem");
            }
            if (!cfg.getBoolean("watched.player.pickup-item", false)) {
                actions.add("PickupItem");
            }
            if (!cfg.getBoolean("watched.environment.lava-flow", true)) {
                actions.add("Flow");
            }
            cfg.removeProperty("watched");
            cfg.setValue("general.disabled-actions", actions);
        } else {
            actions = cfg.getStringList("general.disabled-actions", new ArrayList<String>());
        }
        for (final String act : actions) {
            final int id = ActionProvider.findActionID(act);
            if (id == -1) {
                BBLogging.severe(String.format("Cannot disable action \"%s\", because it doesn't exist!", act));
            } else {
                disabledActions.add(id);
            }
        }
    }
    
    /**
     * @param class1
     * @return
     */
    public static boolean isDisabled(final Class<? extends Action> c) {
        // TODO Auto-generated method stub
        try {
            return c.newInstance().isDisabled();
        } catch (final Exception e) {
            BBLogging.severe("When determining status of " + c.getName() + ":", e);
        }
        return true;
    }
    
    /**
     * @param actID
     * @return
     */
    public static String findActionName(final int actID) {
        if (!Actions.containsKey(actID))
            return "UNKNOWN ACTION #" + actID;
        return Actions.get(actID).actionName;
    }
    
    /**
     * actions to roll back a:10,!BLOCK_PLACE
     * 
     * @author N3X15
     * @return
     */
    public static List<Integer> parseActionSwitch(List<Integer> acts, final String actstr) {
        if (acts == null) {
            acts = getDefaultActions();
        }
        for (final String act : actstr.split(",")) {
            if (!act.startsWith("!") && (acts.size() == 0)) {
                acts.clear();
            }
            int ca;
            if (act.startsWith("!")) {
                ca = findActionID(act.substring(1));
                if ((ca > -1) && acts.contains(ca)) {
                    acts.remove(ca);
                }
            } else {
                ca = findActionID(act);
                if ((ca > -1) && !acts.contains(ca)) {
                    acts.add(ca);
                }
            }
        }
        return acts;
    }
    
    /**
     * @return
     */
    public static List<Integer> getDefaultActions() {
        final List<Integer> acts = new ArrayList<Integer>();
        for (final int actID : Actions.keySet()) {
            // Not disabled in the config and not hidden.
            if (!disabledActions.contains(actID) && !Actions.get(actID).category.equals(ActionCategory.HIDDEN)) {
                acts.add(actID);
            }
        }
        return acts;
    }
}
