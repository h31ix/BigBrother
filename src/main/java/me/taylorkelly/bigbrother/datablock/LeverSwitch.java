package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LeverSwitch extends BBAction {
    
    public LeverSwitch(final String player, final Block lever, final String world) {
        super(player, world, lever.getX(), lever.getY(), lever.getZ(), 69, Byte.toString(lever.getData()));
    }
    
    @Override
    public void rollback(final World wld) {
    }
    
    @Override
    public void redo(final Server server) {
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new LeverSwitch(pi, world, x, y, z, type, data);
    }
    
    private LeverSwitch(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public LeverSwitch() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return "toggled a lever";
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
        return "Lever toggled";
    }
}
