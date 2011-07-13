package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBLogging;
import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.WorldManager;
import me.taylorkelly.util.ChestTools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.sk89q.worldedit.blocks.ItemType;

public class DeltaChest extends BBAction {

    public enum DeltaType {
        NO_CHANGE,
        ADDED,
        REMOVED,
        REPLACED
    }

    public DeltaChest(String player, Chest chest, String changes) {
        super(player, chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ(), chest.getTypeId(), changes);
    }

    private DeltaChest(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }
    
    public DeltaChest(String player, Chest chest, ItemStack[] orig, ItemStack[] latest) {
        super(player, chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ(), chest.getTypeId(), 
        DeltaChest.getInventoryDelta(orig, latest));
    }

    /**
     * 
     */
    public DeltaChest() {
        // TODO Auto-generated constructor stub
    }

    // NEW FORMAT TO SUPPORT DAMAGE TRACKING (Wool, etc)
    // {SLOT:ID:[+|-]COUNT:DATA:DAMAGE[;...]
    // 0:1:15:20:0;
    // + = Added stuff
    // - = Removed stuff
    // no symbol = Replaced a slot
    public static String getInventoryDelta(ItemStack[] orig, ItemStack[] latest) {
        StringBuilder builder = new StringBuilder();
        builder.append("{"); // Marker for the new format
        for (int i = 0; i < orig.length; i++) {
            DeltaEntry e = new DeltaEntry(i,orig[i],latest[i]);
            if(e.isChanged()) {
            	builder.append(e.toString());
            	//if (i + 1 < orig.length) { // do we really need this?
                    builder.append(";");
                //}
            }
        }
        return builder.toString();
    }
    
    public static DeltaEntry[] processDeltaStream(int chestCapacity, String data) {
        data=data.substring(1);
        DeltaEntry[] de = new DeltaEntry[chestCapacity];
        for(int i = 0;i<chestCapacity;i++) {
            de[i] = new DeltaEntry(i);
        }
        int numskipped=0;
        for(String chunk : data.split(";")) {
        	// Handle chest deltas where the chest is merely opened and then closed without changing crap.
        	if(chunk.startsWith("{"))
        		chunk=chunk.substring(1);
        	if(chunk.isEmpty())
        		continue;
            DeltaEntry e = new DeltaEntry(chunk);
            // Check if we have enough room before adding crap to the array.  Solves BB-10.
            if(e.Slot > chestCapacity-1) {
            	BBLogging.debug("Skipping slot #"+e.Slot+", not enough room in chest. ("+e.Slot+" > "+chestCapacity+")");
            	numskipped++;
            } else {
            	de[e.Slot]=e;
            }
        }
        if(numskipped>0)
        	BBLogging.warning("Skipped "+numskipped+" slots because there's not enough room in the chest.");
        
        return de;
    }
    public static class DeltaEntry {
        public int Slot;
        public int ID=0;
        public int Amount=0;
        public short Damage=0;
        public byte Data=0;
        public DeltaType Type=DeltaType.NO_CHANGE;
        private ItemStack newStack;
        private ItemStack oldStack;
        
        public DeltaEntry(int slot) {
            Slot=slot;
        }
        public DeltaEntry(String chunk) {
            String[] dchunks=chunk.split(":");
            Slot=Integer.valueOf(dchunks[0]);
            if(chunk.contains("=")) {
                String[] schunks = chunk.split("=");
                oldStack=parseStack(1,schunks[0].split(":"));
                newStack=parseStack(0,schunks[1].split(":"));
                ID=newStack.getTypeId();
                Type=DeltaType.REPLACED;
                Amount=newStack.getAmount();
                Damage=newStack.getDurability();
                Data=newStack.getData().getData();
            } else {
                ID=Integer.valueOf(dchunks[1]);
                if(dchunks[2].startsWith("+")) {
                    dchunks[2]=dchunks[2].substring(1);
                    Type=DeltaType.ADDED;
                } else {
                    if(dchunks[2].startsWith("-"))
                        Type=DeltaType.REMOVED;
                }
                Amount=Integer.valueOf(dchunks[2]);
                Damage=Short.valueOf(dchunks[3]);
                Data=Byte.valueOf(dchunks[4]);
            }
        }
        
        private ItemStack parseStack(int i, String[] dchunks) {
            ItemStack stack=new ItemStack(Integer.valueOf(dchunks[i]));
            stack.setAmount(Integer.valueOf(dchunks[i++]));
            stack.setDurability(Short.valueOf(dchunks[i++]));
            stack.setData(new MaterialData(Byte.valueOf(dchunks[i++])));
            return stack;
        }
        
        public DeltaEntry(int slot, ItemStack orig, ItemStack latest) {
            oldStack=orig;
            newStack=latest;
            Slot=slot;
            orig=fixStack(orig);
            latest=fixStack(latest);
            
            if(isTypeDifferent(orig,latest)) {
                Type=DeltaType.REPLACED;
                ID=latest.getTypeId();
                Amount=latest.getAmount();
                Damage=latest.getDurability();
                // Why do you do this
                if(orig.getData()==null)
                    Data=0;
                else
                    Data=orig.getData().getData();
            } else {      	
            	//First we must determine this
                Amount=latest.getAmount()-orig.getAmount();
                if(Amount==0)
                    Type=DeltaType.NO_CHANGE;
                else if(Amount<0) {
                    Type=DeltaType.REMOVED;
                    //item might be removed completely, so our information source must be 'orig'
                    ID = orig.getTypeId();
                    Damage = orig.getDurability();
                    if(orig.getData()==null)
                        Data = 0;
                    else
                        Data = orig.getData().getData();
                    
                } else if(Amount>0)	{
                	Type=DeltaType.ADDED;
                	//item might be added to an empty slot
                	ID = latest.getTypeId();
                    Damage = latest.getDurability();
                    if(latest.getData()==null)
                        Data = 0;
                    else
                        Data = latest.getData().getData();
                }   
            }
        }

        public boolean isChanged()
        {
        	return (Type!=DeltaType.NO_CHANGE);
        }
        
        private boolean isTypeDifferent(ItemStack orig, ItemStack latest) {
            if(orig.getTypeId()==0 || latest.getTypeId()==0)
                return false;
            
            if(orig.getTypeId() != latest.getTypeId())
                return true;
            else {
                return(ItemType.usesDamageValue(orig.getTypeId()) && orig.getDurability()!=latest.getDurability());
            }
        }

        private ItemStack fixStack(ItemStack stack) {
            if(stack==null) {
                stack = new ItemStack(0);
                stack.setData(new MaterialData(0));
            }
            
            // If air, data, durability, and damage are always 0.
            if(stack.getType().equals(Material.AIR))
            {
                stack.setAmount(0);
                stack.setData(new MaterialData(0));
                stack.setDurability((byte)0);
            }
            return stack;
        }
        
        @Override
        public String toString() {
            // {SLOT:ID:[+|-]COUNT:DATA:DAMAGE[;...]
            // SLOT:ID:COUNT:DATA:DAMAGE=NEWID:NEWCOUNT...
            if(Type.equals(DeltaType.REPLACED)) {
                return Slot+":"+rawDump(oldStack)+"="+rawDump(newStack);
            } else {
                StringBuilder b = new StringBuilder();
                b.append(Slot);
                b.append(":");
                b.append(ID);
                b.append(":");
                if (Type == DeltaType.ADDED)  {
                	b.append("+");	//nothing else to do here. If it was removed, the minus is already in the amount.
                }
                b.append(Amount);	
                b.append(":");
                b.append(Data);
                b.append(":");
                b.append(Damage);
                return b.toString();
            }
        }
        
        private String rawDump(ItemStack a) {
            // ID:COUNT:DATA:DAMAGE
            StringBuilder b = new StringBuilder();
            b.append(a.getTypeId());
            b.append(":");
            b.append(a.getAmount());
            b.append(":");
            byte dat;
            // Why do you do this
            if(a.getData()==null)
                dat=0;
            else
                dat=a.getData().getData();
            b.append(dat);
            b.append(":");
            b.append(a.getDurability());
            return b.toString();
        }
    }

    @Override
    public void rollback(World wld) {
        World currWorld = wld;
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        Block block = currWorld.getBlockAt(x, y, z);
        if(data.startsWith("{")) { // Check for new marker!
            do_NewRollback(currWorld,block);
        } else {
            do_OldRollback(currWorld,block);
        }
    }
    private void do_NewRollback(World currWorld, Block block) {
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            ItemStack[] inv = ChestTools.getChestContents(chest);
            DeltaEntry[] diff = processDeltaStream(inv.length,data);
            for(int i = 0;i<chest.getInventory().getSize();i++) {
                switch(diff[i].Type) {
                case ADDED:
                case REMOVED:
                    ItemStack stack = inv[i];
                    if(stack==null)
                    	stack=diff[i].oldStack;
                    else
                    {
                    	stack.setAmount(stack.getAmount()-diff[i].Amount);
                    	stack.setDurability(diff[i].Damage);
                    }
                    inv[i]=stack;
                    break;
                case REPLACED:
                    inv[i]=diff[i].oldStack;
                    break;
                case NO_CHANGE:
                    break;
                }
            }
            ChestTools.setChestContents(chest, inv);
        }
    }
    
    private void do_OldRollback(World currWorld,Block block) {
        String[] changes = data.split(";");
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            Inventory inv = chest.getInventory();
            for (int i = 0; i < changes.length; i++) {
                String change = changes[i];
                try {
                    if (change.equals("")) {
                        continue;
                    }
                    String[] pieces = change.split(",");
                    if (pieces.length != 2) {
                        continue;
                    }
                    int id = 0;
                    int itemData = 0;
                    if (pieces[0].contains(":")) {
                        String[] subPieces = pieces[0].split(":");
                        id = Integer.parseInt(subPieces[0]);
                        itemData = Integer.parseInt(subPieces[1]);
                    } else {
                        id = Integer.parseInt(pieces[0]);
                    }
                    int amount = -1 * Integer.parseInt(pieces[1]);
                    ItemStack stack = inv.getItem(i);
                    if (stack == null || stack.getAmount() == 0) {
                        if (amount > 0) {
                            ItemStack newStack = new ItemStack(id, amount, (byte) 0x01, (byte) itemData);
                            inv.setItem(i, newStack);
                        } else {
                            BBLogging.warning("Chest restore conflict. Trying to remove from a empty slot");
                        }
                    } else {
                        if (stack.getTypeId() != id) {
                            BBLogging.warning("Chest restore conflict. Different types.");
                        } else {
                            amount = stack.getAmount() + amount;
                            int damage = stack.getDurability();
                            if (amount < 0) {
                                inv.clear(i);
                            } else {
                                ItemStack newStack = new ItemStack(id, amount, (byte) damage, (byte) itemData);
                                inv.setItem(i, newStack);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    BBLogging.warning("Broken Chest Log with piece: " + change);
                }
            }
        } else {
            BBLogging.severe("Error when restoring chest: block.getState() returned a "+block.getState().getClass().getName()+" instead of a Chest!");
        }
    }

    @Override
    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        String[] changes = data.split(";");
        Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            Inventory inv = chest.getInventory();
            for (int i = 0; i < changes.length; i++) {
                String change = changes[i];
                try {
                    if (change.equals("")) {
                        continue;
                    }
                    String[] pieces = change.split(",");
                    if (pieces.length != 2) {
                        continue;
                    }
                    int id = 0;
                    int itemData = 0;
                    if (pieces[0].contains(":")) {
                        String[] subPieces = pieces[0].split(":");
                        id = Integer.parseInt(subPieces[0]);
                        itemData = Integer.parseInt(subPieces[1]);
                    } else {
                        id = Integer.parseInt(pieces[0]);
                    }
                    int amount = Integer.parseInt(pieces[1]);
                    ItemStack stack = inv.getItem(i);
                    if (stack == null || stack.getAmount() == 0) {
                        if (amount > 0) {
                            ItemStack newStack = new ItemStack(id, amount, (byte) 0x01, (byte) itemData);
                            inv.setItem(i, newStack);
                        } else {
                            BBLogging.warning("Chest restore conflict. Trying to remove from a empty slot");
                        }
                    } else {
                        if (stack.getTypeId() != id) {
                            BBLogging.warning("Chest restore conflict. Different types.");
                        } else {
                            amount = stack.getAmount() + amount;
                            int damage = stack.getDurability();
                            if (amount < 0) {
                                inv.clear(i);
                            } else {
                                ItemStack newStack = new ItemStack(id, amount, (byte) damage, (byte) itemData);
                                inv.setItem(i, newStack);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    BBLogging.warning("Broken Chest Log with piece: " + change);
                }
            }
        } else {
            BBLogging.severe("Error when redoing chest: block.getState() returned a "+block.getState().getClass().getName()+" instead of a Chest!");
        }
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new DeltaChest(pi, world, x, y, z, type, data);
    }

    /**
     * @return
     */
    public DeltaEntry[] getChanges(World currWorld) {
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        Block block = currWorld.getBlockAt(x, y, z);
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            ItemStack[] inv = ChestTools.getChestContents(chest);
            return processDeltaStream(inv.length,data);
        }
        else return new DeltaEntry[0];
    }

    /**
     * @return
     */
    public DeltaEntry[] getChanges() {
        return processDeltaStream(54,data);
    }
    
    @Override
    public String toString() {
        String o = "changed a chest:\n";
        for(DeltaEntry de : getChanges()) {
            switch(de.Type) {
                case ADDED:
                    o+=String.format("%s + Added %d %s%s\n",ChatColor.GREEN.toString(),de.Amount, ItemType.toName(de.ID),ChatColor.YELLOW.toString());
                    break;
                case REMOVED:
                    o+=String.format("%s - Removed %d %s%s\n",ChatColor.RED.toString(),de.Amount, ItemType.toName(de.ID),ChatColor.YELLOW.toString());
                    break;
                case REPLACED:
                    o+=String.format("%s ! Replaced slot #%d with %d %s%s\n",
                            ChatColor.YELLOW.toString(),de.Slot,de.Amount, ItemType.toName(de.ID),ChatColor.YELLOW.toString());
                    break;
            }
        }
        return o;
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

    /* (non-Javadoc)
     * @see me.taylorkelly.bigbrother.datablock.Action#getDescription()
     */
    @Override
    public String getDescription() {
        return "A change applied to a chest.";
    }
}
