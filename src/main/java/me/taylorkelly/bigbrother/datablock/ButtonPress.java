package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ButtonPress extends BBAction {
    
    public ButtonPress(String player, Block button, String world) {
        super(player, world, button.getX(), button.getY(), button.getZ(), 77, Byte.toString(button.getData()));
    }
    
    @Override
    public void rollback(World wld) {
    }
    
    @Override
    public void redo(Server server) {
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new ButtonPress(pi, world, x, y, z, type, data);
    }
    
    private ButtonPress(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public ButtonPress() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return "pressed button";
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
        return "A button pressed by a player.";
    }
}
