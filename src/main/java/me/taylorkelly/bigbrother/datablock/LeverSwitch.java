package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LeverSwitch extends BBAction {

	public LeverSwitch(String player, Block lever, String world) {
		super(player, world, lever.getX(), lever.getY(), lever.getZ(), 69, Byte.toString(lever.getData()));
	}

	public void rollback(World wld) {}
	public void redo(Server server) {}


	public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new LeverSwitch(pi, world, x, y, z, type, data);
	}

	private LeverSwitch(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
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
