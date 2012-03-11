package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Command extends BBAction {
    
    public Command(final Player player, final String command, final String world) {
        super(player.getName(), world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, command);
    }
    
    @Override
    public void rollback(final World wld) {
    }
    
    @Override
    public void redo(final Server server) {
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new Command(pi, world, x, y, z, type, data);
    }
    
    private Command(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
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
