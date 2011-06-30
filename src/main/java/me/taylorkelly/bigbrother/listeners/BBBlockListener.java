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

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.BBDataBlock;
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

import org.bukkit.Material;
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
            Block to = event.getBlock();
            Block from = event.getSource();
            if(to.getType() == Material.FIRE) {
                OwnershipManager.trackFlow(from, to);
            }
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
            if (BBSettings.blockBreak && pi.getWatched()) {
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
        if (BBSettings.blockPlace && pi.getWatched() && !event.isCancelled()) {
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
        if (BBSettings.leafDrops && !event.isCancelled()) {
            // TODO try to find a player that did it.
            final Block block = event.getBlock();
            BBDataBlock dataBlock = LeafDecay.create(block, block.getWorld().getName());
            OwnershipManager.removeOwner(block);
            dataBlock.send();
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (BBSettings.fire && event.getCause() == IgniteCause.FLINT_AND_STEEL && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBDataBlock dataBlock = new FlintAndSteel(event.getPlayer().getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }

    /**
     * Called whenever a block is destroyed by fire
     */
    @Override
    public void onBlockBurn(BlockBurnEvent event) {
        if (BBSettings.fire && !event.isCancelled()) {
            final Block block = event.getBlock();
            BBDataBlock dataBlock = OwnershipManager.trackBurn(block);
            OwnershipManager.removeOwner(block);
            dataBlock.send();
        }
    }

    /**
     * Called whenever something flows from one block to another.
     */
    @Override
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        if (!event.isCancelled()) {
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
        if (!event.isCancelled() && BBSettings.blockPlace) {
            CreateSignText dataBlock = new CreateSignText(event.getPlayer().getName(), event.getLines(), event.getBlock());
            dataBlock.send();
        }
    }
}
