package me.taylorkelly.bigbrother.datablock.explosions;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.block.Block;

public class TNTExplosion extends Explosion {

    public TNTExplosion(String player, Block block, String world) {
        super(player, block, world);
    }

    public TNTExplosion(Block block, String world) {
        super(ENVIRONMENT, block, world);
    }

    @Override
    protected Explosion newInstance(String player, Block block) {
        return new TNTExplosion(player, block, block.getWorld().getName());
    }

    private TNTExplosion(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }

    /**
     * 
     */
    public TNTExplosion() {
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new TNTExplosion(pi, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "blew up TNT";
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getName()
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getCategory()
     */
    @Override
    public ActionCategory getCategory() {
        // TODO Auto-generated method stub
        return ActionCategory.BLOCKS;
    }
}
