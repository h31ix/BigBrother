package me.taylorkelly.bigbrother.datablock;

import me.taylorkelly.bigbrother.BBPlayerInfo;

public abstract class BBAction extends Action {
    
    /**
     * @param player
     * @param action
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @param data
     */
    public BBAction(BBPlayerInfo player, String world, int x, int y, int z, int type, String data) {
        super(player, world, x, y, z, type, data);
    }

    public BBAction(String player, String world, int x, int y, int z, int type, String data) {
        super(player,world,x,y,z,type,data);
    }
    /**
     * 
     */
    public BBAction() {
        // TODO Auto-generated constructor stub
    }

    public static BBAction getBBDataBlock(BBPlayerInfo pi, String world, int x, int y, int z, int type, String data) {
        return null;
    }
/*
    public static Action getDataBlock(BBPlayerInfo pi, int action, String actionName, String world, int x, int y, int z, int type, String data) {
        switch (action) {
            case BLOCK_BROKEN:
                return BrokenBlock.getBBDataBlock(pi, world, x, y, z, type, data);
            case BLOCK_PLACED:
                return PlacedBlock.getBBDataBlock(pi, world, x, y, z, type, data);
            case DESTROY_SIGN_TEXT:
                return DestroySignText.getBBDataBlock(pi, world, x, y, z, type, data);
            case TELEPORT:
                return Teleport.getBBDataBlock(pi, world, x, y, z, type, data);
            case DELTA_CHEST:
                return DeltaChest.getBBDataBlock(pi, world, x, y, z, type, data);
            case COMMAND:
                return Command.getBBDataBlock(pi, world, x, y, z, type, data);
            case CHAT:
                return Chat.getBBDataBlock(pi, world, x, y, z, type, data);
            case DISCONNECT:
                return Disconnect.getBBDataBlock(pi, world, x, y, z, type, data);
            case LOGIN:
                return Login.getBBDataBlock(pi, world, x, y, z, type, data);
            case DOOR_OPEN:
                return DoorOpen.getBBDataBlock(pi, world, x, y, z, type, data);
            case BUTTON_PRESS:
                return ButtonPress.getBBDataBlock(pi, world, x, y, z, type, data);
            case LEVER_SWITCH:
                return LeverSwitch.getBBDataBlock(pi, world, x, y, z, type, data);
            case CREATE_SIGN_TEXT:
                return CreateSignText.getBBDataBlock(pi, world, x, y, z, type, data);
            case LEAF_DECAY:
                return LeafDecay.getBBDataBlock(pi, world, x, y, z, type, data);
            case FLINT_AND_STEEL:
                return FlintAndSteel.getBBDataBlock(pi, world, x, y, z, type, data);
            case TNT_EXPLOSION:
                return TNTExplosion.getBBDataBlock(pi, world, x, y, z, type, data);
            case CREEPER_EXPLOSION:
                return CreeperExplosion.getBBDataBlock(pi, world, x, y, z, type, data);
            case MISC_EXPLOSION:
                return MiscExplosion.getBBDataBlock(pi, world, x, y, z, type, data);
            case OPEN_CHEST:
                return ChestOpen.getBBDataBlock(pi, world, x, y, z, type, data);
            case BLOCK_BURN:
                return BlockBurn.getBBDataBlock(pi, world, x, y, z, type, data);
            case FLOW:
                return Flow.getBBDataBlock(pi, world, x, y, z, type, data);
            case DROP_ITEM:
                return DropItem.getBBDataBlock(pi, world, x, y, z, type, data);
            case PICKUP_ITEM:
                return PickupItem.getBBDataBlock(pi, world, x, y, z, type, data);
            case SIGN_DESTROYED:
                return SignDestroyed.getBBDataBlock(pi, world, x, y, z, type, data);
            default:
                return null;
        }
    }
    
    public static Action getBBDataBlock(int plyID, String world, int x, int y, int z, int type, String data) {
        return getDataBlock(BBUsersTable.getInstance().getUserByID(plyID),world,x,y,z,type,data);
    }
    */
}
