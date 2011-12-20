/**
 * Determines which users should be monitored.
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

import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Watcher {
    
    private Server server;
    
    public Watcher(Server server) {
        this.server = server;
    }
    
    public boolean watching(Player player) {
        return BBUsersTable.getInstance().getUserByName(player.getName()).getWatched();
    }
    
    public boolean toggleWatch(String player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player);
        pi.setWatched(!pi.getWatched());
        return pi.getWatched();
    }
    
    public String getWatchedPlayers() {
        StringBuilder list = new StringBuilder();
        for (BBPlayerInfo pi : BBUsersTable.getInstance().knownPlayers.values()) {
            if (pi.getWatched()) {
                list.append(pi.getName());
                list.append(", ");
            }
        }
        if (list.toString().contains(",")) {
            list.delete(list.lastIndexOf(","), list.length());
        }
        return list.toString();
    }
    
    public boolean haveSeen(Player player) {
        return BBUsersTable.getInstance().knownNames.containsKey(player.getName());
    }
    
    public void watchPlayer(Player player) {
        watchPlayer(player.getName());
    }
    
    public void watchPlayer(String player) {
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player);
        pi.setWatched(true);
    }
    
    public String getUnwatchedPlayers() {
        Player[] playerList = server.getOnlinePlayers();
        StringBuilder list = new StringBuilder();
        for (Player name : playerList) {
            BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(name.getName());
            if (pi.getWatched()) {
                list.append(pi.getName());
                list.append(", ");
            }
        }
        if (list.toString().contains(",")) {
            list.delete(list.lastIndexOf(","), list.length());
        }
        return list.toString();
    }
}
