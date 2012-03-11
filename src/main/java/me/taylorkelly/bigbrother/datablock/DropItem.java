package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Item;

public class DropItem extends BBAction {
    
    public DropItem(final String player, final Item item, final String world) {
        super(player, world, item.getLocation().getBlockX(), item.getLocation().getBlockY(), item.getLocation().getBlockZ(), item.getItemStack().getTypeId(), item.getItemStack().getAmount() + ";" + item.getItemStack().getData().getData() + ";" + item.getItemStack().getDurability());
    }
    
    @Override
    public void rollback(final World wld) {
    }
    
    @Override
    public void redo(final Server server) {
    }
    
    public static BBAction getBBDataBlock(final BBPlayerInfo pi, final String world, final int x, final int y, final int z, final int type, final String data) {
        return new DropItem(pi, world, x, y, z, type, data);
    }
    
    private DropItem(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public DropItem() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return "dropped some " + Material.getMaterial(type);
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
        return "A dropped item";
    }
}
