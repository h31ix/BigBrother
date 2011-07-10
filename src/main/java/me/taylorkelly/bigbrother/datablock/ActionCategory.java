/**
* Category of Action
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

/**
 * @author Rob
 *
 */
public enum ActionCategory {
    /**
     * Environmental changes, such as lava flows and falling leaves.
     */
    ENVIRONMENT,
    
    /**
     * Changes made to blocks (Block breakage/formation etc)
     */
    BLOCKS,
    
    /**
     * Stuff that affects a player (picking up stuff, dropping stuff, logging in,
     * disconnecting, dying, etc.)
     */
    PLAYER,
    
    /**
     * Things that affect entities.
     */
    ENTITY,
    
    /**
     * Explosions
     */
    EXPLOSION,
    
    /**
     * Communication (chat, PMs, commands)
     */
    COMMUNICATION,
    
    /**
     * Everything else
     */
    MISC
}
