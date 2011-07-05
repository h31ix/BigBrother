package me.taylorkelly.bigbrother.datablock;

import java.util.ArrayList;

import me.taylorkelly.bigbrother.BBPlayerInfo;
import me.taylorkelly.bigbrother.BBSettings;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class BlockBurn extends BBAction {

    private ArrayList<BBAction> bystanders;

    public BlockBurn(String player, Block block, String world) {
        super(player, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(player, block);
        surroundingSignChecks(player, block);
        signCheck(player, block);
        checkGnomesLivingOnTop(player, block);
    }

    public BlockBurn(Block block, String world) {
        super(ENVIRONMENT, world, block.getX(), block.getY(), block.getZ(), block.getTypeId(), Byte.toString(block.getData()));
        bystanders = new ArrayList<BBAction>();
        torchCheck(block);
        surroundingSignChecks(block);
        signCheck(block);
        checkGnomesLivingOnTop(block);
    }

    @Override
    public void send() {
        for (BBAction block : bystanders) {
            block.send();
        }
        super.send();
    }

    public void rollback(World wld) {
        if (type != 51 || BBSettings.restoreFire) {
            World currWorld = wld;//server.getWorld(world);
            if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
                currWorld.loadChunk(x >> 4, z >> 4);
            }

            byte blockData = Byte.parseByte(data);
            currWorld.getBlockAt(x, y, z).setTypeId(type);
            currWorld.getBlockAt(x, y, z).setData(blockData);
        }
    }

    public void redo(Server server) {
        World currWorld = server.getWorld(world);
        if (!currWorld.isChunkLoaded(x >> 4, z >> 4)) {
            currWorld.loadChunk(x >> 4, z >> 4);
        }

        currWorld.getBlockAt(x, y, z).setTypeId(0);
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return new BlockBurn(pi, world, x, y, z, type, data);
    }

    private BlockBurn(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }

    /**
     * @param findOwner
     * @param block
     * @param world
     */
    public BlockBurn(BBPlayerInfo player, Block block, World world) {
        super(player.getName(), world.getName(), block.getX(), block.getY(), block.getZ(), block.getTypeId(), "");
        bystanders = new ArrayList<BBAction>();
        torchCheck(player.getName(), block);
        surroundingSignChecks(player.getName(), block);
        signCheck(player.getName(), block);
        checkGnomesLivingOnTop(player.getName(), block);
    }

    private void torchCheck(String player, Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50);
        torchTypes.add(75);
        torchTypes.add(76);

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);

        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            bystanders.add(new BrokenBlock(player, torchTop, world));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            bystanders.add(new BrokenBlock(player, torchNorth, world));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            bystanders.add(new BrokenBlock(player, torchSouth, world));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            bystanders.add(new BrokenBlock(player, torchEast, world));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            bystanders.add(new BrokenBlock(player, torchWest, world));
        }
    }

    private void surroundingSignChecks(String player, Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(new BrokenBlock(player, top, world));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            bystanders.add(new BrokenBlock(player, north, world));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            bystanders.add(new BrokenBlock(player, south, world));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            bystanders.add(new BrokenBlock(player, east, world));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
            bystanders.add(new BrokenBlock(player, west, world));
        }
    }

    private void signCheck(String player, Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new SignDestroyed(player, block.getTypeId(), block.getData(), sign, world));
        }
    }

    private void checkGnomesLivingOnTop(String player, Block block) {
        ArrayList<Integer> gnomes = new ArrayList<Integer>();
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

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        Block mrGnome = block.getWorld().getBlockAt(x, y + 1, z);

        if (gnomes.contains(mrGnome.getTypeId())) {
            bystanders.add(new BrokenBlock(player, mrGnome, world));
        }
    }

    private void torchCheck(Block block) {
        ArrayList<Integer> torchTypes = new ArrayList<Integer>();
        torchTypes.add(50);
        torchTypes.add(75);
        torchTypes.add(76);

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block torchTop = block.getWorld().getBlockAt(x, y + 1, z);

        if (torchTypes.contains(torchTop.getTypeId()) && torchTop.getData() == 5) {
            bystanders.add(new BlockBurn(torchTop, world));
        }
        Block torchNorth = block.getWorld().getBlockAt(x + 1, y, z);
        if (torchTypes.contains(torchNorth.getTypeId()) && torchNorth.getData() == 1) {
            bystanders.add(new BlockBurn(torchNorth, world));
        }
        Block torchSouth = block.getWorld().getBlockAt(x - 1, y, z);
        if (torchTypes.contains(torchSouth.getTypeId()) && torchSouth.getData() == 2) {
            bystanders.add(new BlockBurn(torchSouth, world));
        }
        Block torchEast = block.getWorld().getBlockAt(x, y, z + 1);
        if (torchTypes.contains(torchEast.getTypeId()) && torchEast.getData() == 3) {
            bystanders.add(new BlockBurn(torchEast, world));
        }
        Block torchWest = block.getWorld().getBlockAt(x, y, z - 1);
        if (torchTypes.contains(torchWest.getTypeId()) && torchWest.getData() == 4) {
            bystanders.add(new BlockBurn(torchWest, world));
        }
    }

    private void surroundingSignChecks(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        Block top = block.getWorld().getBlockAt(x, y + 1, z);
        if (top.getTypeId() == 63) {
            bystanders.add(new BlockBurn(top, world));
        }
        Block north = block.getWorld().getBlockAt(x + 1, y, z);
        if (north.getTypeId() == 68 && north.getData() == 5) {
            bystanders.add(new BlockBurn(north, world));
        }
        Block south = block.getWorld().getBlockAt(x - 1, y, z);
        if (south.getTypeId() == 68 && south.getData() == 4) {
            bystanders.add(new BlockBurn(south, world));
        }
        Block east = block.getWorld().getBlockAt(x, y, z + 1);
        if (east.getTypeId() == 68 && east.getData() == 3) {
            bystanders.add(new BlockBurn(east, world));
        }
        Block west = block.getWorld().getBlockAt(x, y, z - 1);
        if (west.getTypeId() == 68 && west.getData() == 2) {
            bystanders.add(new BlockBurn(west, world));
        }
    }

    private void signCheck(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            bystanders.add(new DestroySignText(player, sign, world));
        }
    }

    private void checkGnomesLivingOnTop(Block block) {
        ArrayList<Integer> gnomes = new ArrayList<Integer>();
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

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        Block mrGnome = block.getWorld().getBlockAt(x, y + 1, z);

        if (gnomes.contains(mrGnome.getTypeId())) {
            bystanders.add(new BlockBurn(mrGnome, world));
        }
    }
    
    @Override
    public String toString() {
        return "burnt up a block";
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
