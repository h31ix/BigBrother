package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ButtonPress extends BBAction {

	public ButtonPress(String player, Block button, String world) {
		super(player, world, button.getX(), button.getY(), button.getZ(), 77, Byte.toString(button.getData()));
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}

	public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new ButtonPress(pi, world, x, y, z, type, data);
	}

	private ButtonPress(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, world, x, y, z, type, data);
	}
    
    @Override
    public String toString() {
        return "pressed button";
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
        return ActionCategory.BLOCKS;
    }
}
