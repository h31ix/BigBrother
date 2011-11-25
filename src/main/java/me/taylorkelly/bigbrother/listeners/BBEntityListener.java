package me.taylorkelly.bigbrother.listeners;

import me.taylorkelly.bigbrother.ActionProvider;
import me.taylorkelly.bigbrother.BigBrother;
import me.taylorkelly.bigbrother.datablock.EndermanGrief;
import me.taylorkelly.bigbrother.datablock.explosions.CreeperExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.MiscExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTExplosion;
import me.taylorkelly.bigbrother.datablock.explosions.TNTLogger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class BBEntityListener extends EntityListener {
    
    /**
     * @param bigBrother
     */
    public BBEntityListener(BigBrother bigBrother) {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param entityListener
     * @param event
     */
    public static void onEndermanPickup(EntityListener entityListener,
            EndermanPickupEvent event) {
        EndermanGrief.createPickup(event.getEntity(), event.getBlock());
    }
    
    /**
     * @param entityListener
     * @param event
     */
    public static void onEndermanPlace(EntityListener entityListener,
            EndermanPlaceEvent event) {
        EndermanGrief.createPlace(event.getEntity(), event.getLocation(), event.getType());
    }
    
    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
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
}
