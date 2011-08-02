package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ChestOpen extends BBAction {
    
    public ChestOpen(String player, Block block, String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), 54, "");
    }
    
    private ChestOpen(BBPlayerInfo player, String world, int x, int y, int z,
            int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public ChestOpen() {
        // TODO Auto-generated constructor stub
    }
    
    public void rollback(World wld) {
        
    }
    
    public void redo(Server server) {
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x,
            int y, int z, int type, String data) {
        return new ChestOpen(pi, world, x, y, z, type, data);
    }
    
    @Override
    public String toString() {
        return "opened";
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
        return "A chest opened by a player.";
    }
    
}
