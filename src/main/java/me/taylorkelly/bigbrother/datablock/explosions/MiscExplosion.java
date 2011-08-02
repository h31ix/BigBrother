package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MiscExplosion extends Explosion {
    
    public MiscExplosion(String player, Block block, String world) {
        super(player, block, world);
    }
    
    public MiscExplosion(Block block, String world) {
        super(ENVIRONMENT, block, world);
    }
    
    protected Explosion newInstance(String player, Block block) {
        return new MiscExplosion(player, block, block.getWorld().getName());
    }
    
    public static void create(Location location, List<Block> blockList,
            String world) {
        for (Block block : blockList) {
            BBAction dataBlock = new MiscExplosion(ENVIRONMENT, block, world);
            dataBlock.send();
            if (block.getType() == Material.TNT) {
                TNTLogger.log(ENVIRONMENT, block);
            }
        }
    }
    
    private MiscExplosion(BBPlayerInfo player, String world, int x, int y,
            int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public MiscExplosion() {
        // TODO Auto-generated constructor stub
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo player, String world,
            int x, int y, int z, int type, String data) {
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
