package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Chat extends BBAction {
	public Chat(Player player, String message, String world) {
		super(player.getName(), world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), 0, message);
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}

	public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new Chat(pi, world, x, y, z, type, data);
	}

	private Chat(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, world, x, y, z, type, data);
	}
    
    @Override
    public String toString() {
        return String.format("said \"%s\"",data);
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
        return ActionCategory.COMMUNICATION;
    }
}
