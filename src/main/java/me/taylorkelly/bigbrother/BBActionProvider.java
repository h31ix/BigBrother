/**
 * BigBrother's own ActionProvider
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

import me.taylorkelly.bigbrother.datablock.*;
import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTExplosion;

/**
 * This should only be initialized after BigBrother is loaded (e.g. from a PluginListener)
 * @author Rob
 *
 */
public class BBActionProvider extends ActionProvider {
    public BBActionProvider(BigBrother plugin) {
        super(plugin);
        /*****************************************************
         * WARNINGWARNINGWARNINGWARNINGWARNINGWARNINGWARNING *
         *                                                   *
         *    ONLY ADD ITEMS TO THE BOTTOM OF THIS LIST!     *
         *  FAILURE TO DO SO MAY RESULT IN LOST DATA DUE TO  *
         *              REASSIGNED ACTION IDS!               *
         *                                                   *
         * WARNINGWARNINGWARNINGWARNINGWARNINGWARNINGWARNING *
         *****************************************************/
        //BEGIN LEGACY ACTIONS
        registerActionForceID(plugin, this, new BrokenBlock(), 0);
        registerActionForceID(plugin, this, new PlacedBlock(), 1);
        registerActionForceID(plugin, this, new DestroySignText(), 2);
        registerActionForceID(plugin, this, new Teleport(), 3);
        registerActionForceID(plugin, this, new DeltaChest(), 4);
        registerActionForceID(plugin, this, new Command(), 5);
        registerActionForceID(plugin, this, new Chat(), 6);
        registerActionForceID(plugin, this, new Disconnect(), 7);
        registerActionForceID(plugin, this, new Login(), 8);
        registerActionForceID(plugin, this, new DoorOpen(), 9);
        registerActionForceID(plugin, this, new ButtonPress(), 10);
        registerActionForceID(plugin, this, new LeverSwitch(), 11);
        registerActionForceID(plugin, this, new CreateSignText(), 12);
        registerActionForceID(plugin, this, new LeafDecay(), 13);
        registerActionForceID(plugin, this, new FlintAndSteel(), 14);

        registerActionForceID(plugin, this, new TNTExplosion(), 15);
        registerActionForceID(plugin, this, new CreeperExplosion(), 16);
        registerActionForceID(plugin, this, new MiscExplosion(), 17);
        
        registerActionForceID(plugin, this, new ChestOpen(), 18);
        registerActionForceID(plugin, this, new BlockBurn(), 19);
        registerActionForceID(plugin, this, new Flow(), 20);
        registerActionForceID(plugin, this, new DropItem(), 21);
        registerActionForceID(plugin, this, new PickupItem(), 22);
        registerActionForceID(plugin, this, new SignDestroyed(), 23);
        //END LEGACY ACTIONS
        
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.ActionProvider#getAction(java.lang.String, me.taylorkelly.bigbrother.BBPlayerInfo, java.lang.String, int, int, int, int, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Action getAction(String actionName, BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        //And here, we use the magic of reflection.
        Class<? extends Action> c=null;
        try {
            c=(Class<? extends Action>) Class.forName("me.taylorkelly.bigbrother.datablock."+actionName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            return c.getConstructor(BBPlayerInfo.class,String.class,int.class,int.class,int.class,int.class,String.class).newInstance(player,world,  x,  y,  z,  type,  data);
        } catch (Exception e) {
            BBLogging.severe("Error loading me.taylorkelly.bigbrother.datablock."+actionName+": ",e);
        }
        return null;
    }
    
}
