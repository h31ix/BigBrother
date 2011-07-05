package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class FlintAndSteel extends BBAction {

    public FlintAndSteel(String player, Block block, String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), "");
    }

    private FlintAndSteel(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }

    /**
     * 
     */
    public FlintAndSteel() {
        // TODO Auto-generated constructor stub
    }

    public void rollback(World wld) {
    }

    public void redo(Server server) {
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new FlintAndSteel(pi, world, x, y, z, type, data);
    }

    
    @Override
    public String toString() {
        return "ignited something with a flint and steel";
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
