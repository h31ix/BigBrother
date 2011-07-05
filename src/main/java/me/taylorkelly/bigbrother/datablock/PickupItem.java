package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Item;

public class PickupItem extends BBAction {

	public PickupItem(String player, Item item, String world) {
		super(player, world, item.getLocation().getBlockX(), item.getLocation().getBlockY(), item.getLocation().getBlockZ(), item.getItemStack().getTypeId(), item.getItemStack().getAmount() + ";" + item.getItemStack().getData().getData() + ";" + item.getItemStack().getDurability());
	}

	public void rollback(World wld) {
	}

	public void redo(Server server) {
	}

	public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
		return new PickupItem(pi, world, x, y, z, type, data);
	}

	private PickupItem(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
		super(player, world, x, y, z, type, data);
	}
    
    /**
     * 
     */
    public PickupItem() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "picked up an item of type "+Material.getMaterial(type);
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
