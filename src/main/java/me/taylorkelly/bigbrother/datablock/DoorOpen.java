package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class DoorOpen extends BBAction {

    public DoorOpen(String player, Block door, String world) {
        super(player, world, door.getX(), door.getY(), door.getZ(), 324, door.getData() + "");
    }

    public void rollback(World wld) {
    }

    public void redo(Server server) {
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new DoorOpen(pi, world, x, y, z, type, data);
    }

    private DoorOpen(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public DoorOpen() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "opened a door";
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
        return ActionCategory.MISC;
    }

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "An opened door.";
    }
}
