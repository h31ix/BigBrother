package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MiscExplosion extends Explosion {
    
    public MiscExplosion(final String player, final Block block, final String world) {
        super(player, block, world);
    }
    
    public MiscExplosion(final Block block, final String world) {
        super(ENVIRONMENT, block, world);
    }
    
    @Override
    protected Explosion newInstance(final String player, final Block block) {
        return new MiscExplosion(player, block, block.getWorld().getName());
    }
    
    public static void create(final Location location, final List<Block> blockList, final String world) {
        for (final Block block : blockList) {
            final BBAction dataBlock = new MiscExplosion(ENVIRONMENT, block, world);
            dataBlock.send();
            if (block.getType() == Material.TNT) {
                TNTLogger.log(ENVIRONMENT, block);
            }
        }
    }
    
    private MiscExplosion(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public MiscExplosion() {
        // TODO Auto-generated constructor stub
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new MiscExplosion(player, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "detonated something unknown";
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
        return "An explosion with an unknown cause.";
    }
}
