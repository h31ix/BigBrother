/**
 * Player information structure
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

import me.taylorkelly.bigbrother.finder.StickMode;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * @author N3X15
 * 
 */
public class BBPlayerInfo {
    enum PlayerField {
        WATCHED,
        HAS_LOG
    }
    
    public static BBPlayerInfo ENVIRONMENT;
    
    /**
     * New guy? (INSERT instead of UPDATE)
     */
    private boolean isNew = true;
    
    /**
     * Are we waiting for this guy to do something after he opens a chest? (Workaround for lack of inventory update events)
     */
    private ItemStack[] chestContents = null;
    
    private String name = "";
    private int flags = 0; // bitfield flags
    private int id = -1;
    
    public StickMode historyTool;
    
    private Chest myOpenChest = null;
    
    /**
     * For caching a new player.
     * 
     * @param name
     */
    public BBPlayerInfo(final String name) {
        this.name = name;
        setNew(true); // Only really used to determine if we need to INSERT or
                      // UPDATE.
        if (BBSettings.autoWatch) {
            setWatched(true);
        }
        
        BBUsersTable.getInstance().addOrUpdatePlayer(this);
        refresh(); // Get ID#
        setNew(false);
        BBLogging.debug("New user: " + name + " -> #" + id);
    }
    
    /**
     * For bringing in a user from the database.
     * 
     * @param id
     * @param name
     * @param flags
     */
    public BBPlayerInfo(final int id, final String name, final int flags) {
        this.id = id;
        this.name = name;
        this.flags = flags;
    }
    
    private void setFlag(final PlayerField fld, final boolean on) {
        if (!on) {
            flags &= ~(1 << fld.ordinal());
        } else {
            flags |= (1 << fld.ordinal());
        }
        if (id != -1) {
            BBUsersTable.getInstance().addOrUpdatePlayer(this);
        }
    }
    
    /**
     * Reload from the database.
     */
    public void refresh() {
        BBPlayerInfo clone;
        BBLogging.debug("BBPlayerInfo.refresh(): " + name + "#" + Integer.valueOf(id));
        if (id > -1) {
            clone = BBUsersTable.getInstance().getUserFromDB(id);
        } else {
            clone = BBUsersTable.getInstance().getUserFromDB(name);
        }
        id = clone.id;
        flags = clone.flags;
        name = clone.name;
    }
    
    private boolean getFlag(final PlayerField fld) {
        final int f = (1 << fld.ordinal());
        return (flags & f) == f;
    }
    
    public int getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getFlags() {
        return flags;
    }
    
    /**
     * Used for tracking whether a user is new to the database or not.
     * 
     * @param b
     */
    public void setNew(final boolean b) {
        isNew = b;
    }
    
    /**
     * 
     * @return
     */
    public boolean getNew() {
        return isNew;
    }
    
    /**
     * @param isWatched
     */
    public void setWatched(final boolean isWatched) {
        setFlag(PlayerField.WATCHED, isWatched);
    }
    
    /**
     * Are we tracking this user?
     * 
     * @return
     */
    public boolean getWatched() {
        return getFlag(PlayerField.WATCHED);
    }
    
    /**
     * Tell the system whether the player has a log in their possession or not.
     * 
     * @param logInPossession
     */
    public void setHasLog(final boolean logInPossession) {
        setFlag(PlayerField.HAS_LOG, logInPossession);
    }
    
    /**
     * Does this user have the SuperLog?
     * 
     * @return
     */
    public boolean hasLog() {
        return getFlag(PlayerField.HAS_LOG);
    }
    
    /**
     * Set true when user has opened a chest. Set false when they move/do stuff that can only be done outside of inventory.
     * 
     * @param b
     */
    public void setHasOpenedChest(final Chest c, final ItemStack[] contents) {
        myOpenChest = c;
        
        if (contents != null) {
            chestContents = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] == null) {
                    chestContents[i] = null;
                } else {
                    //primitive cloning  - I can't figure, how to get Data field as well (ag)
                    chestContents[i] = new ItemStack(contents[i].getTypeId(), contents[i].getAmount(), contents[i].getDurability());
                }
            }
        } else {
            chestContents = null;
        }
    }
    
    /**
     * True if the user is most likely messing around with their chest inventory.
     * 
     * @return
     */
    public boolean hasOpenedChest() {
        return chestContents != null;
    }
    
    /**
     * Format username, colorize if necessary
     */
    @Override
    public String toString() {
        final String player = getName();
        /*
         * TODO: Future consideration, working to get this hunk of bugs out the door atm. - N3X if(BBSettings.colorPlayerNames) { player=BBPermissions.getPrefix(player)+player+BBPermissions.getSuffix(player); }
         */
        return player;
    }
    
    public ItemStack[] getOldChestContents() {
        if (chestContents == null) {
            BBLogging.severe("getOldChestContents is about to return a null.  Please report this.");
        }
        return chestContents;
    }
    
    public Chest getOpenedChest() {
        return myOpenChest;
    }
}
