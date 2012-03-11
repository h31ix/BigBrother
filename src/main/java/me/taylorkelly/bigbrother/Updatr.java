/**
 * Update handler
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

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Updatr {
    
    public static void updateAvailable(final Player player) {
        final URLReader reader = new URLReader();
        if (!reader.versionIsUpToDate(BigBrother.version)) {
            player.sendMessage(ChatColor.RED.toString() + BigBrother.name + " " + BigBrother.version + " has an update to " + reader.getCurrVersion());
        } else {
            player.sendMessage(ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version + " is up to date!");
        }
    }
    
    static void updateAvailable(final ConsoleCommandSender console) {
        final URLReader reader = new URLReader();
        if (!reader.versionIsUpToDate(BigBrother.version)) {
            console.sendMessage(ChatColor.RED.toString() + BigBrother.name + " " + BigBrother.version + " has an update to " + reader.getCurrVersion());
        } else {
            console.sendMessage(ChatColor.AQUA.toString() + BigBrother.name + " " + BigBrother.version + " is up to date!");
        }
    }
}
