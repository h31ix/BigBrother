package me.taylorkelly.bigbrother.datablock.explosions;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.block.Block;

public class TNTExplosion extends Explosion {
    
    public TNTExplosion(final String player, final Block block, final String world) {
        super(player, block, world);
    }
    
    public TNTExplosion(final Block block, final String world) {
        super(ENVIRONMENT, block, world);
    }
    
    @Override
    protected Explosion newInstance(final String player, final Block block) {
        return new TNTExplosion(player, block, block.getWorld().getName());
    }
    
    private TNTExplosion(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public TNTExplosion() {
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new TNTExplosion(pi, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "blew up TNT";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        // TODO Auto-generated method stub
        return ActionCategory.BLOCKS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "A box of TNT exploding.";
    }
}
