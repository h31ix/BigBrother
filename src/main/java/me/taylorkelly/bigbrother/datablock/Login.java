package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Login extends BBAction {
    public Login(final Player player, final String world) {
        super(BBUsersTable.getInstance().getUserByName(player.getName()), world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, BBSettings.logPlayerIPs ? player.getAddress().getAddress().toString().substring(1) : "");
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new Login(pi, world, x, y, z, type, data);
    }
    
    private Login(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public Login() {
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
        return "logged in";
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
        return "A player logging in";
    }
}
