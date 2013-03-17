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
package me.taylorkelly.bigbrother;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.datablock.ActionFactory;
import me.taylorkelly.bigbrother.datablock.BBAction;
import me.taylorkelly.bigbrother.datablock.BlockPistoned;
import me.taylorkelly.bigbrother.datablock.BrokenBlock;
import me.taylorkelly.bigbrother.datablock.ButtonPress;
import me.taylorkelly.bigbrother.datablock.Chat;
import me.taylorkelly.bigbrother.datablock.ChestOpen;
import me.taylorkelly.bigbrother.datablock.Command;
import me.taylorkelly.bigbrother.datablock.CreateSignText;
import me.taylorkelly.bigbrother.datablock.Disconnect;
import me.taylorkelly.bigbrother.datablock.DoorOpen;
import me.taylorkelly.bigbrother.datablock.DropItem;
import me.taylorkelly.bigbrother.datablock.FlintAndSteel;
import me.taylorkelly.bigbrother.datablock.Flow;
import me.taylorkelly.bigbrother.datablock.LeafDecay;
import me.taylorkelly.bigbrother.datablock.LeverSwitch;
import me.taylorkelly.bigbrother.datablock.Login;
import me.taylorkelly.bigbrother.datablock.PickupItem;
import me.taylorkelly.bigbrother.datablock.PlacedBlock;
import me.taylorkelly.bigbrother.datablock.SignDestroyed;
import me.taylorkelly.bigbrother.datablock.Teleport;
import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;
import me.taylorkelly.util.ChestTools;
import net.nexisonline.bigbrother.ownership.OwnershipManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;

public class BBListener implements Listener {
    
    private final BigBrother plugin;
    
    public BBListener(final BigBrother plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (!event.isCancelled()) {
            final Block blockTo = event.getBlock();
            final Block blockFrom = event.getSource();
            BBLogging.debug(String.format("BlockSpread: <%d,%d,%d> (%s) flowed to <%d,%d,%d> (%s)", blockFrom.getX(), blockFrom.getY(), blockFrom.getZ(), blockFrom.getType().name(), blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockTo.getType().name()));
            if ((blockTo.getType() == Material.FIRE) || (blockFrom.getType() == Material.FIRE)) {
                OwnershipManager.trackFlow(blockFrom, blockTo);
            }
        }
    }
    
    /**
     * Basically: * Update ownership of all the blocks moved * Extend ownership of the piston in the direction it is pushing * Generate a new log item for every block moved * Kill james bond
     * 
     * @author N3X15
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPistonExtend(final BlockPistonExtendEvent e) {
        final World w = e.getBlock().getWorld();
        // Determine direction of move
        final int xo = e.getDirection().getModX();
        final int yo = e.getDirection().getModY();
        final int zo = e.getDirection().getModZ();
        final BBPlayerInfo opi = OwnershipManager.findOwner(e.getBlock());
        //Extend piston ownership one block outward
        int x = e.getBlock().getX();
        int y = e.getBlock().getY();
        int z = e.getBlock().getZ();
        
        OwnershipManager.setOwnerLocation(new Location(w, x + xo, y + yo, z + zo), opi);
        for (final Block b : e.getBlocks()) {
            //Get ownership information
            BBPlayerInfo pi = OwnershipManager.findOwner(b);
            x = b.getX();
            y = b.getY();
            z = b.getZ();
            pi = OwnershipManager.findOwner(b);
            OwnershipManager.setOwnerLocation(new Location(w, x + xo, y + yo, z + zo), pi);
            
            // Generate action
            final BlockPistoned action = new BlockPistoned(opi, b, e.getDirection());
            if (pi.getWatched()) {
                action.send();
            }
        }
    }
    
    /**
     * Basically: * Remove the piston's "pusher" ownership * Move the stickied block's ownership back to where it was
     * 
     * @author N3X15
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPistonRetract(final BlockPistonRetractEvent e) {
        if (!e.isSticky() && !e.isCancelled())
            return;
        final World w = e.getBlock().getWorld();
        // Determine direction of move
        final int xo = e.getDirection().getModX();
        final int yo = e.getDirection().getModY();
        final int zo = e.getDirection().getModZ();
        final BBPlayerInfo opi = OwnershipManager.findOwner(e.getBlock());
        //Extend piston ownership one block outward
        final int x = e.getBlock().getX();
        final int y = e.getBlock().getY();
        final int z = e.getBlock().getZ();
        
        OwnershipManager.setOwnerLocation(new Location(w, x + xo, y + yo, z + zo), BBPlayerInfo.ENVIRONMENT);
        final Block pistonShaft = w.getBlockAt(x + xo, y + yo, z + zo);
        // <N3X15_> EvilSeph, Dinnerbone, whoever is available:  How am I supposed to track blocks retracted by a piston?  BlockPistonRetractEvent doesn't have a getBlocks() method, so I can't determine what blocks will be affected by sticky pistons.
        // <EvilSeph> rudimentary, sorry
        //for(Block b : e.getBlocks()) {
        final Block b = w.getBlockAt(x + (xo * 2), y + (yo * 2), z + (zo * 2));
        final BBPlayerInfo pi = OwnershipManager.findOwner(b);
        
        // Clear the moved block's ownership
        OwnershipManager.setOwner(b, BBPlayerInfo.ENVIRONMENT);
        
        // Update the new position with updated ownership
        OwnershipManager.setOwner(pistonShaft, pi);
        
        // Generate action
        final BlockPistoned action = new BlockPistoned(opi, b, e.getDirection());
        if (pi.getWatched()) {
            action.send();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void STICK_onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission(Permissions.INFO.id) && plugin.hasStick(player, event.getItemInHand())) {
            plugin.stick(player, event.getBlockPlaced(), false);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamage(final BlockDamageEvent event) {
        BBLogging.debug("onBlockDamage");
        if (event.getBlock().getType() == Material.TNT) {
            TNTLogger.log(event.getPlayer().getName(), event.getBlock());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!event.isCancelled()) {
            BBLogging.debug("onBlockBreak");
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (!ActionProvider.isDisabled(BrokenBlock.class)) {
                final Block block = event.getBlock();
                final BrokenBlock dataBlock = new BrokenBlock(player.getName(), block, block.getWorld().getName());
                dataBlock.send();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
        plugin.closeChestIfOpen(pi);
        if (!ActionProvider.isDisabled(PlacedBlock.class) && pi.getWatched() && !event.isCancelled()) {
            BBLogging.debug("onBlockPlace");
            final Block block = event.getBlockPlaced();
            if ((block.getType() == Material.WATER) || (block.getType() == Material.STATIONARY_WATER) || (block.getType() == Material.LAVA) || (block.getType() == Material.STATIONARY_LAVA) || (block.getType() == Material.FIRE)) {
                OwnershipManager.setOwner(block, pi);
            }
            final PlacedBlock dataBlock = new PlacedBlock(player.getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeavesDecay(final LeavesDecayEvent event) {
        if (!ActionProvider.isDisabled(LeafDecay.class) && !event.isCancelled()) {
            // TODO try to find a player that did it.
            final Block block = event.getBlock();
            final BBAction dataBlock = LeafDecay.create(block, block.getWorld().getName());
            OwnershipManager.removeOwner(block);
            dataBlock.send();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (!ActionProvider.isDisabled(FlintAndSteel.class) && (event.getCause() == IgniteCause.FLINT_AND_STEEL) && !event.isCancelled()) {
            final Block block = event.getBlock();
            final BBAction dataBlock = new FlintAndSteel(event.getPlayer().getName(), block, block.getWorld().getName());
            dataBlock.send();
        }
    }
    
    /**
     * Called whenever a block is destroyed by fire
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBurn(final BlockBurnEvent event) {
        if (!ActionProvider.isDisabled(Flow.class) && !event.isCancelled()) {
            final Block block = event.getBlock();
            final BBAction dataBlock = OwnershipManager.trackBurn(block);
            OwnershipManager.removeOwner(block);
            dataBlock.send();
        }
    }
    
    /**
     * Called whenever something flows from one block to another.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockFromTo(final BlockFromToEvent event) {
        if (ActionProvider.isDisabled(Flow.class))
            return;
        final Block blockFrom = event.getBlock();
        final Block blockTo = event.getToBlock();
        BBLogging.debug(String.format("BlockFromTo: <%d,%d,%d> (%s) flowed to <%d,%d,%d> (%s)", blockFrom.getX(), blockFrom.getY(), blockFrom.getZ(), blockFrom.getType().name(), blockTo.getX(), blockTo.getY(), blockTo.getZ(), blockTo.getType().name()));
        if (!event.isCancelled()) {
            final int fromID = blockFrom.getTypeId();
            final int toID = blockTo.getTypeId();
            if (BBSettings.isBlockIgnored(fromID) || (fromID == Material.WATER.getId()) || (fromID == Material.STATIONARY_WATER.getId()) || (fromID == Material.LAVA.getId()) || (fromID == Material.STATIONARY_LAVA.getId()) || BBSettings.isBlockIgnored(toID) || (toID == Material.WATER.getId()) || (toID == Material.STATIONARY_WATER.getId()) || (toID == Material.LAVA.getId()) || (toID == Material.STATIONARY_LAVA.getId()))
                return;
            // Only record a change if the owner is different (avoids duplicates)
            if (OwnershipManager.findOwner(blockFrom).getID() != OwnershipManager.findOwner(blockTo).getID()) {
                final Flow dataBlock = OwnershipManager.trackFlow(blockFrom, blockTo);
                dataBlock.send();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignChange(final SignChangeEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            final Sign sign = (Sign) event.getBlock().getState();
            boolean oldText = false;
            for (final String line : sign.getLines()) {
                if (!line.equals("")) {
                    oldText = true;
                }
            }
            if (oldText) {
                final SignDestroyed dataBlock = new SignDestroyed(event.getPlayer().getName(), event.getBlock().getTypeId(), event.getBlock().getData(), (Sign) event.getBlock().getState(), event.getBlock().getWorld().getName());
                dataBlock.send();
            }
        }
        if (!event.isCancelled() && !ActionProvider.isDisabled(CreateSignText.class)) {
            final CreateSignText dataBlock = new CreateSignText(event.getPlayer().getName(), event.getLines(), event.getBlock());
            dataBlock.send();
        }
    }
    
    /////////////////////////////////////////////////////////////
    // Entity Crap
    /////////////////////////////////////////////////////////////
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityBlockChange(final EntityChangeBlockEvent event) {
        if (event.getTo().equals(Material.AIR)) {
            switch (event.getEntityType()) {
                case ENDERMAN:
                    ActionFactory.createEndermanPickup((LivingEntity)event.getEntity(), event.getBlock());
                    break;
                case ENDER_DRAGON:
                    ActionFactory.createEnderGrief((LivingEntity)event.getEntity(), event.getBlock());
                    break;
            }
        } else {
            switch (event.getEntityType()) {
                case ENDERMAN:
                    ActionFactory.createEndermanPlace((LivingEntity)event.getEntity(), event.getBlock().getLocation(), event.getBlock().getType());
                    break;
                case SILVERFISH:
                    ActionFactory.createSilverfishGrief((LivingEntity)event.getEntity(), event.getBlock());
                    break;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(final EntityExplodeEvent event) {
        // Err... why is this null when it's a TNT?
        if (!event.isCancelled()) {
            if (event.getEntity() == null) {
                if (!ActionProvider.isDisabled(TNTExplosion.class)) {
                    TNTLogger.createTNTDataBlock(event.blockList(), event.getLocation());
                }
            } else if (event.getEntity() instanceof LivingEntity) {
                if (!ActionProvider.isDisabled(CreeperExplosion.class)) {
                    CreeperExplosion.create(event.getEntity().getLocation(), event.blockList(), event.getLocation().getWorld().getName());
                }
            } else if (!ActionProvider.isDisabled(MiscExplosion.class)) {
                MiscExplosion.create(event.getEntity().getLocation(), event.blockList(), event.getLocation().getWorld().getName());
            }
        }
    }
    
    //////////////////////////////////////////////////////
    // Player crap
    //////////////////////////////////////////////////////
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        try {
            //plugin.processPsuedotick();
            if ((event == null) || (event.getPlayer() == null))
                return;
            final String[] parts = BBCommand.groupArgs(event.getMessage().split(" "));
            final String cmd = parts[0].toLowerCase();
            String msg = event.getMessage();
            
            // Perform censoring
            if (BBSettings.censoredCommands.contains(cmd)) {
                msg = cmd;
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        msg += " ";
                        for (int j = 0; j < parts[i].length(); j++) {
                            msg += "*";
                        }
                    }
                }
            }
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (!ActionProvider.isDisabled(Command.class) && pi.getWatched()) {
                final Command dataBlock = new Command(player, msg, player.getWorld().getName());
                dataBlock.send();
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerCommandPreprocess(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        try {
            //plugin.processPsuedotick();
            if ((event == null) || (event.getPlayer() == null))
                return;
            final Player player = event.getPlayer();
            
            BBUsersTable.getInstance().addOrUpdateUser(player);
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            
            if (!ActionProvider.isDisabled(Login.class) && pi.getWatched()) {
                final Login dataBlock = new Login(player, player.getWorld().getName());
                dataBlock.send();
            }
            
            if (player.hasPermission(Permissions.INFO.id)) {
                plugin.sticker.onPlayerJoin(player, pi);
            }
            
            BBLogging.debug(player.getName() + " has Permissions: ");
            BBLogging.debug("- Watching privileges: " + player.hasPermission(Permissions.WATCH.id));
            BBLogging.debug("- Info privileges: " + player.hasPermission(Permissions.INFO.id));
            BBLogging.debug("- Rollback privileges: " + player.hasPermission(Permissions.ROLLBACK.id));
            BBLogging.debug("- Cleansing privileges: " + player.hasPermission(Permissions.CLEANSE.id));
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerJoin(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        try {
            //plugin.processPsuedotick();
            if ((event == null) || (event.getPlayer() == null))
                return;
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (!ActionProvider.isDisabled(Disconnect.class) && pi.getWatched()) {
                final Disconnect dataBlock = new Disconnect(player.getName(), player.getLocation(), player.getWorld().getName());
                dataBlock.send();
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerQuit(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        try {
            //plugin.processPsuedotick();
            if ((event == null) || (event.getPlayer() == null))
                return;
            final Location from = event.getFrom();
            final Location to = event.getTo();
            
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (!ActionProvider.isDisabled(Teleport.class) && pi.getWatched() && (distance(from, to) > 5) && !event.isCancelled()) {
                final Teleport dataBlock = new Teleport(player.getName(), event.getTo());
                dataBlock.send();
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerTeleport(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        try {
            //plugin.processPsuedotick();
            if ((event == null) || (event.getPlayer() == null))
                return;
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            plugin.closeChestIfOpen(pi);
            if (!ActionProvider.isDisabled(Chat.class) && pi.getWatched()) {
                final Chat dataBlock = new Chat(player, event.getMessage(), player.getWorld().getName());
                dataBlock.send();
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerChat(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        try {
            if ((event == null) || (event.getPlayer() == null) || (event.getItem() == null))
                return;
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            if (!ActionProvider.isDisabled(PickupItem.class) && pi.getWatched()) {
                // It should not be null, but I have no other way to explain the NPEs.  Bukkit Bug?
                if ((event.getItem() != null) && (event.getItem().getItemStack() != null)) {
                    final PickupItem dataBlock = new PickupItem(player.getName(), event.getItem(), event.getItem().getWorld().getName());
                    dataBlock.send();
                }
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerPickupItem(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        try {
            if ((event == null) || (event.getPlayer() == null) || (event.getItemDrop() == null) || (event.getItemDrop().getItemStack() == null))
                return;
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            if (!ActionProvider.isDisabled(DropItem.class) && pi.getWatched()) {
                final DropItem dataBlock = new DropItem(player.getName(), event.getItemDrop(), event.getItemDrop().getWorld().getName());
                dataBlock.send();
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerDropItem(" + event.toString() + ")", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        try {
            if ((event == null) || (event.getPlayer() == null))
                return;
            //plugin.processPsuedotick();
            if (event.isCancelled())
                return;
            
            final Player player = event.getPlayer();
            final BBPlayerInfo pi = BBUsersTable.getInstance().getUserByName(player.getName());
            
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (player.hasPermission(Permissions.INFO.id) && plugin.hasStick(player, player.getItemInHand()) && plugin.leftClickStick(player)) {
                    // Process left-clicks (punch action on log, etc)
                    plugin.stick(player, event.getClickedBlock(), true);
                    
                    event.setCancelled(true); // Cancel in case of 1-hit breakable stuff like flowers.
                }
            }
            
            // Process right-clicking stuff.
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                // Process stick/log events first.
                if (player.hasPermission(Permissions.INFO.id) && plugin.hasStick(player, player.getItemInHand()) && plugin.rightClickStick(player)) {
                    // Get info
                    plugin.stick(player, event.getClickedBlock(), false);
                    
                    // Cancel any interactions.
                    final ArrayList<Material> nonInteracts = new ArrayList<Material>();
                    nonInteracts.add(Material.WOOD_PLATE);
                    nonInteracts.add(Material.STONE_PLATE);
                    if (!nonInteracts.contains(event.getClickedBlock().getType())) {
                        event.setCancelled(true);
                    }
                    // Otherwise...
                } else if (!ActionProvider.isDisabled(PlacedBlock.class) && pi.getWatched()) {
                    int x;
                    int y;
                    int z;
                    int type;
                    PlacedBlock dataBlock;
                    World world;
                    final Block block = event.getClickedBlock();
                    
                    plugin.closeChestIfOpen(pi);
                    if (block.getState() instanceof Chest) {
                        final Chest chest = ((Chest) block.getState());
                        // OH SHI-
                        BBUsersTable.getInstance().userOpenedChest(player.getName(), chest, ChestTools.getChestContents(chest));
                        return;
                    }
                    switch (event.getMaterial()) {
                    //TODO Door logging
                        case LAVA_BUCKET:
                            x = event.getClickedBlock().getX() + event.getBlockFace().getModX();
                            y = event.getClickedBlock().getY() + event.getBlockFace().getModY();
                            z = event.getClickedBlock().getZ() + event.getBlockFace().getModZ();
                            type = Material.LAVA.getId();
                            world = event.getClickedBlock().getWorld();
                            dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                            OwnershipManager.setOwnerLocation(new Location(world, x, y, z), pi);
                            dataBlock.send();
                            break;
                        case WATER_BUCKET:
                            x = event.getClickedBlock().getX() + event.getBlockFace().getModX();
                            y = event.getClickedBlock().getY() + event.getBlockFace().getModY();
                            z = event.getClickedBlock().getZ() + event.getBlockFace().getModZ();
                            type = Material.WATER.getId();
                            world = event.getClickedBlock().getWorld();
                            dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) 0);
                            OwnershipManager.setOwnerLocation(new Location(world, x, y, z), pi);
                            dataBlock.send();
                            break;
                        case SIGN:
                            x = event.getClickedBlock().getX() + event.getBlockFace().getModX();
                            y = event.getClickedBlock().getY() + event.getBlockFace().getModY();
                            z = event.getClickedBlock().getZ() + event.getBlockFace().getModZ();
                            world = event.getClickedBlock().getWorld();
                            
                            int data = 0;
                            switch (event.getBlockFace()) {
                                case UP:
                                    type = Material.SIGN_POST.getId();
                                    break;
                                case NORTH:
                                    data = 4;
                                    type = Material.WALL_SIGN.getId();
                                    break;
                                case SOUTH:
                                    data = 5;
                                    type = Material.WALL_SIGN.getId();
                                    break;
                                case EAST:
                                    data = 2;
                                    type = Material.WALL_SIGN.getId();
                                    break;
                                case WEST:
                                    data = 3;
                                    type = Material.WALL_SIGN.getId();
                                    break;
                                default:
                                    type = Material.SIGN.getId();
                            }
                            dataBlock = new PlacedBlock(event.getPlayer().getName(), world.getName(), x, y, z, type, (byte) data);
                            dataBlock.send();
                            break;
                        case BUCKET:
                            BrokenBlock dataBlock2;
                            world = event.getClickedBlock().getWorld();
                            switch (event.getClickedBlock().getType()) {
                                case STATIONARY_LAVA:
                                case LAVA:
                                    x = event.getClickedBlock().getX();
                                    y = event.getClickedBlock().getY();
                                    z = event.getClickedBlock().getZ();
                                    type = Material.LAVA.getId();
                                    dataBlock2 = new BrokenBlock(BBUsersTable.getInstance().getUserByName(event.getPlayer().getName()), world.getName(), x, y, z, type, (byte) 0);
                                    OwnershipManager.removeOwnerByLocation(event.getClickedBlock().getLocation());
                                    dataBlock2.send();
                                    break;
                                case STATIONARY_WATER:
                                case WATER:
                                    x = event.getClickedBlock().getX();
                                    y = event.getClickedBlock().getY();
                                    z = event.getClickedBlock().getZ();
                                    type = Material.WATER.getId();
                                    dataBlock2 = new BrokenBlock(BBUsersTable.getInstance().getUserByName(event.getPlayer().getName()), world.getName(), x, y, z, type, (byte) 0);
                                    OwnershipManager.removeOwnerByLocation(event.getClickedBlock().getLocation());
                                    dataBlock2.send();
                            }
                            break;
                        default:
                            
                            switch (event.getClickedBlock().getType()) {
                                case WOODEN_DOOR:
                                    //case IRON_DOOR:
                                    if (!ActionProvider.isDisabled(DoorOpen.class)) {
                                        final DoorOpen doorDataBlock = new DoorOpen(event.getPlayer().getName(), block, block.getWorld().getName());
                                        doorDataBlock.send();
                                    }
                                    break;
                                case LEVER:
                                    if (!ActionProvider.isDisabled(LeverSwitch.class)) {
                                        final LeverSwitch leverDataBlock = new LeverSwitch(event.getPlayer().getName(), block, block.getWorld().getName());
                                        leverDataBlock.send();
                                    }
                                    break;
                                case STONE_BUTTON:
                                    if (!ActionProvider.isDisabled(ButtonPress.class)) {
                                        final ButtonPress buttonDataBlock = new ButtonPress(event.getPlayer().getName(), block, block.getWorld().getName());
                                        buttonDataBlock.send();
                                    }
                                    break;
                                case CHEST:
                                    if (!ActionProvider.isDisabled(ChestOpen.class)) {
                                        final BBAction chestDataBlock = new ChestOpen(event.getPlayer().getName(), block, block.getWorld().getName());
                                        chestDataBlock.send();
                                    }
                                    break;
                            }
                            break;
                    }
                }
            }
        } catch (final Throwable e) {
            BBLogging.severe("onPlayerInteract(" + event.toString() + ")", e);
        }
    }
    
    private double distance(final Location from, final Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }
}
