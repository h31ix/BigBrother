package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Disconnect extends BBAction {
    public Disconnect(String player, Location location, String world) {
        super(player, world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, "");
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new Disconnect(pi, world, x, y, z, type, data);
    }
    
    private Disconnect(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public Disconnect() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void rollback(World wld) {
    }
    
    @Override
    public void redo(Server server) {
    }
    
    @Override
    public String toString() {
        return "disconnected";
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
        return "A disconnected player.";
    }
}
