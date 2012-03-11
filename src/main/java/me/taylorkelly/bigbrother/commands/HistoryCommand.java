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
package me.taylorkelly.bigbrother.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.Permissions;
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
    
    private final BigBrother plugin;
    private final int ACTIONSPERPAGE = 5;
    public static final String invalidPlayer = ChatColor.RED + "[BBROTHER] Invalid Player.";
    
    public HistoryCommand(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    // bb history n3x15 a:BlockPlaced w:hyperion pg:2
    
    public boolean onCommand(final CommandSender send, final Command cmd, final String cmdLabel, final String[] args) {
        final Player player = (Player) send;
        
        if (player.hasPermission(Permissions.INFO.id)) {
            List<Integer> acts = ActionProvider.getDefaultActions();
            String name = "Environment";
            int page = 1;
            for (final String arg : args) {
                if (arg.startsWith("a:")) {
                    acts = ActionProvider.parseActionSwitch(acts, arg.substring(2));
                } else if (arg.startsWith("pg:")) {
                    page = Integer.parseInt(arg.substring(3));
                } else {
                    name = arg;
                }
            }
            
            final ArrayList<Action> history = BBDataTable.getInstance().getPlayerHistory(player, name, plugin.worldManager);
            final int maxpages = history.size() / ACTIONSPERPAGE;
            sendHeader(player, page, maxpages, history.size());
            final ArrayList<Action> trimmedHistory = new ArrayList<Action>();
            
            final int from = ((page - 1) * ACTIONSPERPAGE);
            
            // Sanity check to make sure we aren't going off into space...
            if (from > (history.size() - 1)) {
                player.sendMessage("ERROR: Page out of range");
                return true;
            }
            
            final int to = Math.min((page * ACTIONSPERPAGE) - 1, history.size() - 1);
            
            if (history.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No edits found");
                return true;
            } else {
                trimmedHistory.addAll(history.subList(from, to));
                
                for (final Action dataBlock : trimmedHistory) {
                    final Calendar cal = Calendar.getInstance();
                    final String DATE_FORMAT = "MMM.d@'" + ChatColor.GRAY + "'kk.mm.ss";
                    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                    cal.setTimeInMillis(dataBlock.date * 1000);
                    final StringBuilder msg = new StringBuilder(sdf.format(cal.getTime()));
                    msg.append(ChatColor.WHITE).append(" - ").append(ChatColor.YELLOW);
                    msg.append(dataBlock.player);
                    msg.append(ChatColor.WHITE);
                    msg.append(" ");
                    final String[] lines = dataBlock.toString().split("\n");
                    msg.append(lines[0]);
                    player.sendMessage(msg.toString());
                    if (lines.length > 1) {
                        for (int l = 1; l < lines.length; l++) {
                            player.sendMessage(lines[l]);
                        }
                    }
                }
                player.sendMessage(ChatColor.AQUA.toString() + trimmedHistory.size() + " edits on this page (out of " + history.size() + ")");
                return true;
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
            return true;
        }
    }
    
    /**
     * @param player
     * @param page
     * @param maxpages
     * @param size
     */
    private void sendHeader(final Player player, final int page, final int maxpages, final int size) {
        final StringBuilder sb = new StringBuilder();
        sb.append(BigBrother.premessage + " Player history (p. ");
        sb.append(ChatColor.WHITE);
        sb.append(page);
        sb.append(ChatColor.AQUA);
        sb.append("/");
        sb.append(ChatColor.WHITE);
        sb.append(maxpages);
        sb.append(ChatColor.AQUA);
        sb.append(", ");
        sb.append(size);
        sb.append("records)");
        player.sendMessage(sb.toString());
    }
}