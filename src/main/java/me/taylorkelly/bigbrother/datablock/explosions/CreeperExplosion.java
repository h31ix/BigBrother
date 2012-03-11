package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CreeperExplosion extends Explosion {
    
    public CreeperExplosion(final String player, final Block block, final String world) {
        super(player, block, world);
    }
    
    public CreeperExplosion(final Block block, final String world) {
        super(ENVIRONMENT, block, world);
    }
    
    @Override
    protected Explosion newInstance(final String player, final Block block) {
        return new CreeperExplosion(player, block, block.getWorld().getName());
    }
    
    public static void create(final Location location, final List<Block> blockList, final String world) {
        for (final Block block : blockList) {
            final BBAction dataBlock = new CreeperExplosion(ENVIRONMENT, block, world);
            dataBlock.send();
            if (block.getType() == Material.TNT) {
                TNTLogger.log(ENVIRONMENT, block);
            }
        }
    }
    
    private CreeperExplosion(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public CreeperExplosion() {
        // TODO Auto-generated constructor stub
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new CreeperExplosion(player, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "exploded a creeper";
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
        return "A creeper exploding.";
    }
}
