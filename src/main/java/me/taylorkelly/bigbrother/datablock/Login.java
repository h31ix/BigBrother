package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.tablemgrs.BBUsersTable;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Login extends BBAction {
	public Login(Player player, String world) {
        super(BBUsersTable.getInstance().getUserByName(player.getName()), world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0,
                BBSettings.ipPlayer ? player.getAddress().getAddress().toString().substring(1) : "");
	}

	public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Login(pi, world, x, y, z, type, data);
	}

	private Login(BBPlayerInfo player, String world, int x, int y, int z, int type,  String data) {
		super(player, world, x, y, z, type, data);
	}

	/**
     * 
     */
    public Login() {
        // TODO Auto-generated constructor stub
    }

    public void rollback(World wld) {}
	public void redo(Server server) {}
    
    @Override
    public String toString() {
        return "logged in";
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
        return ActionCategory.PLAYER;
    }
}
