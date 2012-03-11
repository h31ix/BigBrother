/**
 * For the idiots in the world who can't understand humor.
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

import me.taylorkelly.bigbrother.BigBrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Rob
 * 
 */
public class MowlawnCommand implements CommandExecutor {
    
    @SuppressWarnings("unused")
    private final BigBrother plugin;
    
    public MowlawnCommand(final BigBrother p) {
        plugin = p;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    
    public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
        arg0.sendMessage(BigBrother.premessage + " I hope you realize that I was joking what I said that BigBrother can mow your lawn, you big dummy.");
        return true;
    }
    
}
