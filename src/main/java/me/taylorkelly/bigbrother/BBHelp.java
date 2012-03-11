/**
 * Register with Help.
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

import me.taylorkelly.help.Help;

import org.bukkit.plugin.Plugin;

public class BBHelp {
    
    public static void initialize(final Plugin plugin) {
        final Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            if (!plugin.getServer().getPluginManager().isPluginEnabled(test)) {
                BBLogging.info("Help plugin detected but disabled. Enabling plugin 'Help' (v" + test.getDescription().getVersion() + ").");
                plugin.getServer().getPluginManager().enablePlugin(test);
            }
            
            final Help helpPlugin = ((Help) test);
            final String[] permissions = new String[] { "bb.admin.watch", "bb.admin.info", "bb.admin.rollback", "bb.admin.cleanse" };
            helpPlugin.registerCommand("bb help", "Help for all BigBrother commands", plugin, permissions);
            helpPlugin.registerCommand("bb watch [player]", "Toggle the watch on [player]", plugin, permissions[0]);
            helpPlugin.registerCommand("bb watched", "Displays the list of watched players", plugin, permissions[1]);
            helpPlugin.registerCommand("bb unwatched", "Displays the list of unwatched players", plugin, permissions[1]);
            helpPlugin.registerCommand("bb here", "An overview of the block history around you", plugin, true, permissions[1]);
            helpPlugin.registerCommand("bb here [#]", "An overview of [#] blocks around you", plugin, permissions[1]);
            helpPlugin.registerCommand("bb here [player]", "Displays [player]'s changes around you", plugin, permissions[1]);
            helpPlugin.registerCommand("bb here [player] [#]", "Displays [player]'s changes within [#] blocks", plugin, permissions[1]);
            helpPlugin.registerCommand("bb find [x] [y] [z]", "Displays changes around [x] [y] [z]", plugin, permissions[1]);
            helpPlugin.registerCommand("bb find [x] [y] [z] [player]", "Displays [player]'s changes around [x] [y] [z]", plugin, permissions[1]);
            helpPlugin.registerCommand("bb rollback (players) (t) (r) (id)", "Perform a rollback with given arguments", plugin, permissions[2]);
            helpPlugin.registerCommand("bb undo", "Undoes the most recent rollback", plugin, permissions[2]);
            helpPlugin.registerCommand("bb cleanse", "Cleanse the database", plugin, permissions[3]);
            helpPlugin.registerCommand("bb log", "Gives you a log for inspecting non-solid blocks", plugin, permissions[1]);
            BBLogging.info("'Help' support enabled");
        } else {
            BBLogging.warning("'Help' isn't detected. No /help support");
        }
    }
}
