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
import me.taylorkelly.bigbrother.BBPermissions;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.Action;
import me.taylorkelly.bigbrother.tablemgrs.BBDataTable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
    
    private BigBrother plugin;
    
    public HistoryCommand(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    // bb history n3x15 a:BlockPlaced w:hyperion pg:2
    @Override
    public boolean onCommand(CommandSender send, Command cmd, String cmdLabel, String[] args) {
        Player player=(Player) send;
        if(args.length>0) {
            return false;
        }
        if(BBPermissions.info(player)) {
            List<Integer> acts = ActionProvider.getDefaultActions();
            String name = "Environment";
            for(String arg : args) {
                if(arg.startsWith("a:")) {
                    acts=ActionProvider.parseActionSwitch(acts, arg.substring(2));
                } else
                    name = arg;
            }
            ArrayList<Action> history = BBDataTable.getInstance().getPlayerHistory(player, name, plugin.worldManager);

            if (history.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No edits on this block");
            } else {
                player.sendMessage(ChatColor.AQUA.toString() + history.size() + " edits on this block");
                for (Action dataBlock : history) {
                    Calendar cal = Calendar.getInstance();
                    String DATE_FORMAT = "MMM.d@'" + ChatColor.GRAY + "'kk.mm.ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                    cal.setTimeInMillis(dataBlock.date * 1000);
                    StringBuilder msg = new StringBuilder(sdf.format(cal.getTime()));
                    msg.append(ChatColor.WHITE).append(" - ").append(ChatColor.YELLOW);
                    msg.append(dataBlock.player);
                    msg.append(ChatColor.WHITE);
                    msg.append(" ");
                    String[] lines=dataBlock.toString().split("\n");
                    msg.append(lines[0]);
                    player.sendMessage(msg.toString());
                    if(lines.length>1) {
                        for(int l = 1;l<lines.length;l++)
                            player.sendMessage(lines[l]);
                    }
                }
            }
        } else {
            player.sendMessage(BigBrother.permissionDenied);
        }
        return false;
    }
}