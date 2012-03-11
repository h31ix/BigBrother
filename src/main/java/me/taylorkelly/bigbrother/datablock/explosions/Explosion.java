package me.taylorkelly.bigbrother.datablock.explosions;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;
import me.taylorkelly.bigbrother.datablock.BBAction;
import me.taylorkelly.bigbrother.datablock.SignDestroyed;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public abstract class Explosion extends BBAction {
    
    private ArrayList<BBAction> bystanders;
    
    public Explosion(final String name, final Block block, final String world) {
        super(name, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(name, block);
        surroundingSignChecks(name, block);
        signCheck(name, block);
        chestCheck(name, block);
        checkGnomesLivingOnTop(name, block);
    }
    
    @Override
    public void send() {
        for (final BBAction block : bystanders) {
            block.send();
        }
        super.send();
    }
    
    @Override
    public void rollback(final World wld) {
        if ((type != 51) || BBSettings.restoreFire) {
            final World currWorld = wld;//server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }
            
            final byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }
    
    @Override
    public void redo(final Server server) {
        final World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }
        
        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }
    
    @Override
    protected final void torchCheck(final String player, final Block block) {
        final ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50);
        torchTypes.add(75);
        torchTypes.add(76);
        
        final int x = block.getX();
        final int y = block.getY();
        final int z = block.getZ();
        
        final Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);
        
        if (torchTypes.contains(torchTop.getTypeId()) && (torchTop.getData() == 5)) {
            bystanders.add(newInstance(player, torchTop));
        }
        final Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && (torchNorth.getData() == 1)) {
            bystanders.add(newInstance(player, torchNorth));
        }
        final Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && (torchSouth.getData() == 2)) {
            bystanders.add(newInstance(player, torchSouth));
        }
        final Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && (torchEast.getData() == 3)) {
            bystanders.add(newInstance(player, torchEast));
        }
        final Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && (torchWest.getData() == 4)) {
            bystanders.add(newInstance(player, torchWest));
        }
    }
    
    @Override
    protected final void surroundingSignChecks(final String player, final Block block) {
        final int x = block.getX();
        final int y = block.getY();
        final int z = block.getZ();
        
        final Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(newInstance(player, top));
        }
        final Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if ((north.getTypeId() == 68) && (north.getData() == 5)) {
            bystanders.add(newInstance(player, north));
        }
        final Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if ((south.getTypeId() == 68) && (south.getData() == 4)) {
            bystanders.add(newInstance(player, south));
        }
        final Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if ((east.getTypeId() == 68) && (east.getData() == 3)) {
            bystanders.add(newInstance(player, east));
        }
        final Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if ((west.getTypeId() == 68) && (west.getData() == 2)) {
            bystanders.add(newInstance(player, west));
        }
    }
    
    @Override
    protected final void signCheck(final String player, final Block block) {
        if (block.getState() instanceof Sign) {
            final Sign sign = (Sign) block.getState();
            bystanders.add(new SignDestroyed(player, block.getTypeId(), block.getData(), sign, world));
        }
    }
    
    @Override
    protected final void checkGnomesLivingOnTop(final String player, final Block block) {
        final ArrayList<Integer> gnomes = new ArrayList<Integer>();
        gnomes.add(6); // Sapling
        gnomes.add(37); // Yellow Flower
        gnomes.add(38); // Red Flower
        gnomes.add(39); // Brown Mushroom
        gnomes.add(40); // Red Mushroom
        gnomes.add(55); // Redstone
        gnomes.add(59); // Crops
        gnomes.add(64); // Wood Door
        gnomes.add(66); // Tracks
        gnomes.add(69); // Lever
        gnomes.add(70); // Stone pressure plate
        gnomes.add(71); // Iron Door
        gnomes.add(72); // Wood pressure ePlate
        gnomes.add(78); // Snow
        gnomes.add(81); // Cactus
        gnomes.add(83); // Reeds
        
        final int x = block.getX();
        final int y = block.getY();
        final int z = block.getZ();
        final Block mrGnome = block.getWorld().getBlockAt(x, y + 1, z);
        
        if (gnomes.contains(mrGnome.getTypeId())) {
            bystanders.add(newInstance(player, mrGnome));
        }
    }
    
    protected abstract Explosion newInstance(String player, Block block);
    
    protected Explosion(final BBPlayerInfo player, final String world, final int x, final int y, final int z, final int type, final String data) {
        super(player, world, x, y, z, type, data);
    }
    
    /**
     * 
     */
    public Explosion() {
        // TODO Auto-generated constructor stub
    }
}
