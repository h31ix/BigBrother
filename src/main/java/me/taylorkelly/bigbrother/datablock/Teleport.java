package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Teleport extends BBAction {
    public Teleport(final String player, final Location to) {
        super(player, to.getWorld().getName(), to.getBlockX(), to.getBlockY(), to.getBlockZ(), 0, "");
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new Teleport(pi, world, x, y, z, type, data);
    }
    
    private Teleport(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public Teleport() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void rollback(final World wld) {
    }
    
    @Override
    public void redo(final Server server) {
    }
    
    @Override
    public String toString() {
        return "teleported";
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
        return ActionCategory.PLAYER;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "A player teleporting";
    }
}
