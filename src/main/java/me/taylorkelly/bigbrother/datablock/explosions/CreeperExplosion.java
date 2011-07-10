package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.List;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.datablock.ActionCategory;
import me.taylorkelly.bigbrother.datablock.BBAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CreeperExplosion extends Explosion {

    public CreeperExplosion(String player, Block block, String world) {
        super(player, block, world);
    }

    public CreeperExplosion(Block block, String world) {
        super(ENVIRONMENT, block, world);
    }

    protected Explosion newInstance(String player, Block block) {
        return new CreeperExplosion(player, block, block.getWorld().getName());
    }

    public static void create(Location location, List<Block> blockList, String world) {
        for (Block block : blockList) {
            BBAction dataBlock = new CreeperExplosion(ENVIRONMENT, block, world);
            dataBlock.send();
            if (block.getType() == Material.TNT) {
                TNTLogger.log(ENVIRONMENT, block);
            }
        }
    }

    private CreeperExplosion(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }

    /**
     * 
     */
    public CreeperExplosion() {
        // TODO Auto-generated constructor stub
    }

    public static BBAction getBBDataBlock(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        return new CreeperExplosion(player, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "exploded a creeper";
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
