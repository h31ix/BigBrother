package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Command extends BBAction {
    
    public Command(Player player, String command, String world) {
        super(player.getName(), world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, command);
    }
    
    public void rollback(World wld) {
    }
    
    public void redo(Server server) {
    }
    
    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x,
            int y, int z, int type, String data) {
        return new Command(pi, world, x, y, z, type, data);
    }
    
    private Command(BBPlayerInfo player, String world, int x, int y, int z,
            int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public Command() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return String.format("used command \"%s\"", data);
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
        return ActionCategory.COMMUNICATION;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "A player using a command.";
    }
}
