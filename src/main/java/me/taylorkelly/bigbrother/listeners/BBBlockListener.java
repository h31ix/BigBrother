/**
 * Block Listener
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
package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBAction;
import me.taylorkelly.bigbrother.datablock.BlockPistoned;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.CreateSignText;
import me.taylorkelly.bigbrother.datablock.FlintAndSteel;
import me.taylorkelly.bigbrother.datablock.Flow;
import me.taylorkelly.bigbrother.datablock.LeafDecay;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datablock.SignDestroyed;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import net.nexisonline.bigbrother.ownership.OwnershipManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class BBBlockListener extends BlockListener {

    private BigBrother plugin;

    public BBBlockListener(BigBrother plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!event.isCancelled()) {
            Block blockTo = event.getBlock();
            Block blockFrom = event.getSource();
            BBLogging.debug(String.format("BlockSpread: <%d,%d,%d> (%s) flowed to <%d,%d,%d> (%s)",
                    blockFrom.getX(), blockFrom.getY(), blockFrom.getZ(), blockFrom.getType().name(),
                    blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockTo.getType().name()
                    ));
            if(blockTo.getType() == Material.FIRE 
                    || blockFrom.getType() == Material.FIRE) {
                OwnershipManager.trackFlow(blockFrom, blockTo);
            }
        }
    }
    
    /**
     * Basically: 
     *  * Update ownership of all the blocks moved
     *  * Extend ownership of the piston in the direction it is pushing
     *  * Generate a new log item for every block moved
     *  * Kill james bond
     * @author N3X15
     */
    @Override
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        World w = e.getBlock().getWorld();
        // Determine direction of move
        int xo = e.getDirection().getModX();
        int yo = e.getDirection().getModY();
        int zo = e.getDirection().getModZ();
        BBPlayerInfo opi = OwnershipManager.findOwner(e.getBlock());
        //Extend piston ownership one block outward
        int x = e.getBlock().getX();
        int y = e.getBlock().getY();
        int z = e.getBlock().getZ();
        
        OwnershipManager.setOwnerLocation(new Location(w,x+xo,y+yo,z+zo),opi);
        for(Block b : e.getBlocks()) {
            //Get ownership information
            BBPlayerInfo pi = OwnershipManager.findOwner(b);
            x = b.getX();
            y = b.getY();
            z = b.getZ();
            pi=OwnershipManager.findOwner(b);
            OwnershipManager.setOwnerLocation(new Location(w,x+xo,y+yo,z+zo),pi);
            
            // Generate action
            BlockPistoned action = new BlockPistoned(opi,b,e.getDirection());
            if(pi.getWatched())
                action.send();
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        BBLogging.debug("onBlockDamage");
        if (event.getBlock().getType() == Material.TNT) {
            TNTLogger.log(event.getPlayer().getName(), event.getBlock());
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            BBLogging.debug("onBlockBreak");
            Player player = event.getPlayer();
            BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (!ActionProvider.isDisabled(BrokenBlock.class) && pi.getWatched()) {
                Block block = event.getBlock();
                BrokenBlock dataBlock = new BrokenBlock(player.getName(), block, block.getWorld().getName());
                dataBlock.send();
            }
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (!ActionProvider.isDisabled(PlacedBlock.class) && pi.getWatched() && !event.isCancelled()) {
            BBLogging.debug("onBlockPlace");
            Block block = event.getBlockPlaced();
            if (block.getType() == Material.WATER 
                    || block.getType() == Material.STATIONARY_WATER 
                    || block.getType() == Material.LAVA 
                    || block.getType() == Material.STATIONARY_LAVA 
                    || block.getType() == Material.FIRE) {
                OwnershipManager.setOwner(block, pi);
            }
            PlacedBlock dataBlock = new PlacedBlock(player.getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @Override
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (!ActionProvider.isDisabled(LeafDecay.class) && !event.isCancelled()) {
            // TODO try to find a player that did it.
            final Block block = event.getBlock();
            BBAction dataBlock = LeafDecay.create(block, block.getWorld().getName());
            OwnershipManager.removeOwner(block);
            dataBlock.send();
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!ActionProvider.isDisabled(FlintAndSteel.class) && event.getCause() == IgniteCause.FLINT_AND_STEEL && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBAction dataBlock = new FlintAndSteel(event.getPlayer().getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    /**
     * Called whenever a block is destroyed by fire
     */
    @Override
    public void onBlockBurn(BlockBurnEvent event) {
        if (!ActionProvider.isDisabled(Flow.class) && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBAction dataBlock = OwnershipManager.trackBurn(block);
            OwnershipManager.removeOwner(block);
            dataBlock.send();
        }
    }

    /**
     * Called whenever something flows from one block to another.
     */
    @Override
    public void onBlockFromTo(BlockFromToEvent event) {
        if(ActionProvider.isDisabled(Flow.class))
            return;
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        BBLogging.debug(String.format("BlockFromTo: <%d,%d,%d> (%s) flowed to <%d,%d,%d> (%s)",
                blockFrom.getX(), blockFrom.getY(), blockFrom.getZ(), blockFrom.getType().name(),
                blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockTo.getType().name()
                ));
        if (!event.isCancelled()) {
            int fromID = blockFrom.getTypeId();
            int toID = blockTo.getTypeId();
            if(BBSettings.isBlockIgnored(fromID) 
                    || fromID == Material.WATER.getId()
                    || fromID == Material.STATIONARY_WATER.getId()
                    || fromID == Material.LAVA.getId()
                    || fromID == Material.STATIONARY_LAVA.getId()
                    || BBSettings.isBlockIgnored(toID)
                    || toID == Material.WATER.getId()
                    || toID == Material.STATIONARY_WATER.getId()
                    || toID == Material.LAVA.getId()
                    || toID == Material.STATIONARY_LAVA.getId())
                return;
            // Only record a change if the owner is different (avoids duplicates)
            if(OwnershipManager.findOwner(blockFrom).getID()!=OwnershipManager.findOwner(blockTo).getID()) {
                Flow dataBlock = OwnershipManager.trackFlow(blockFrom, blockTo);
                dataBlock.send();
            }
        }
    }

    @Override
    public void onSignChange(SignChangeEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            boolean oldText = false;
            for (String line : sign.getLines()) {
                if (!line.equals("")) {
                    oldText = true;
                }
            }
            if (oldText) {
                SignDestroyed dataBlock = new SignDestroyed(event.getPlayer().getName(), event.getBlock().getTypeId(), event.getBlock().getData(),(Sign) event.getBlock().getState(), event.getBlock().getWorld().getName());
                dataBlock.send();
            }
        }
        if (!event.isCancelled() && !ActionProvider.isDisabled(CreateSignText.class)) {
            CreateSignText dataBlock = new CreateSignText(event.getPlayer().getName(), event.getLines(), event.getBlock());
            dataBlock.send();
        }
    }
}
