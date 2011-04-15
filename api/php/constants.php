<?php

// BIGBROTHER ACTIONS IDs

define("BLOCK_BROKEN",0);
define("BLOCK_PLACED",1);
define("DESTROY_SIGN_TEXT",2);
define("TELEPORT",3);
define("DELTA_CHEST",4);
define("COMMAND",5);
define("CHAT",6);
define("DISCONNECT",7);
define("LOGIN",8);
define("DOOR_OPEN",9);
define("BUTTON_PRESS",10);
define("LEVER_SWITCH",11);
define("CREATE_SIGN_TEXT",12);
define("LEAF_DECAY",13);
define("FLINT_AND_STEEL",14);
define("TNT_EXPLOSION",15);
define("CREEPER_EXPLOSION",16);
define("MISC_EXPLOSION",17);
define("OPEN_CHEST",18);
define("BLOCK_BURN",19);
define("LAVA_FLOW",20);
	
	

//  ITEM LIST

$block_names = array("air", "stone", "grass", "dirt", "cobblestone", "wood", "sapling", "bedrock", "water", "spring", "lava", "lava-spring", "sand", "gravel", "gold-ore", "iron-ore", "coal-ore", "log", "leaves", "sponge", "glass", "lapis-lazuli-ore", "lapis-lazuli", "dispenser", "sandstone", "note-block", "agua-cloth", "cyan-cloth", "blue-cloth", "purple-cloth", "indigo-cloth", "violet-cloth", "magenta-cloth", "pink-cloth", "black-cloth", "cloth", "wool", "flower", "rose", "brown-mushroom", "red-mushroom", "gold", "iron", "double-step", "step", "brick", "tnt", "bookshelf", "mossy-cobblestone", "obsidian", "torch", "fire", "mob-spawner", "wooden-stairs", "chest", "redstone-wire", "diamond-ore", "diamond", "workbench", "crops", "soil", "furnace", "burning-furnace", "signpost", "wooden-door", "ladder", "tracks", "stone-stairs", "wall-sign", "lever", "stone-plate", "iron-door", "wooden-plate", "redstone-ore", "glowing-redstone-ore", "redstone-torch-off", "redstone-torch", "stone-button", "snow", "ice", "snow-block", "cactus", "clay", "sugar-cane", "jukebox", "fence", "pumpkin", "brimstone", "slow-sand", "lightstone", "portal", "jack-o-lantern", "cake");
$item_names = array("iron-shovel", "iron-pickaxe", "iron-axe", "flint-and-steel", "apple", "bow", "arrow", "coal", "diamond", "iron-ingot", "gold-ingot", "iron-sword", "wooden-sword", "wooden-shovel", "wooden-pickaxe", "wooden-axe", "stone-sword", "stone-shovel", "stone-pickaxe", "stone-axe", "diamond-sword", "diamond-shovel", "diamond-pickaxe", "diamond-axe", "stick", "bowl", "mushroom-soup", "gold-sword", "gold-shovel", "gold-pickaxe", "gold-axe", "string", "feather", "sulphur", "wooden-hoe", "stone-hoe", "iron-hoe", "diamond-hoe", "gold-hoe", "seeds", "wheat", "bread", "leather-helmet", "leather-chestplate", "leather-leggings", "leather-boots", "chainmail-helmet", "chainmail-chestplate", "chainmail-leggings", "chainmail-boots", "iron-helmet", "iron-chestplate", "iron-leggings", "iron-boots", "diamond-helmet", "diamond-chestplate", "diamond-leggings", "diamond-boots", "gold-helmet", "gold-chestplate", "gold-leggings", "gold-boots", "flint", "raw-porkchop", "cooked-porkchop", "paintings", "golden-apple", "sign", "wooden-door", "bucket", "water-bucket", "lava-bucket", "mine-cart", "saddle", "iron-door", "redstone", "snowball", "boat", "leather", "milk", "clay-brick", "clay-balls", "sugar-cane", "paper", "book", "slimeball", "storage-minecart", "powered-minecart", "egg", "compass", "fishing-rod", "clock", "glowstone-dust", "raw-fish", "cooked-fish", "ink-sack", "bone", "sugar", "cake");
$special_item_names = array("gold-music-disc", "green-music-disc");

$items = array();
foreach ($block_names as $id => $block) $items[$id] = $block;
foreach ($item_names as $id => $item) $items[$id + 0x100] = $item;
foreach ($special_item_names as $id => $item) $items[$id + 0x8d0] = $item;
	
?>
