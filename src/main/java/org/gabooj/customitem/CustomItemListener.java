package org.gabooj.customitem;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.gabooj.landprotection.LandManager;

import java.util.*;

public class CustomItemListener implements Listener {

    private final JavaPlugin plugin;
    private final Server server;

    private final Set<Vector> directions;
    private static final HashMap<BlockFace, List<Vector>> perpendicularDirectionsMap = new HashMap<>();
    private static final HashMap<Player, Long> cooldowns = new HashMap<>();

    public CustomItemListener(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.directions = getDirections();

        server.getPluginManager().registerEvents(this, plugin);
        populatePerpendicularDirections();
    }

    @EventHandler
    public void onPlayerClickEntity(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.CLAY_BALL && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Pokeball")) {
            if (!LandManager.doesPlayerHaveAccessToChunkAt(event.getPlayer().getLocation(), event.getPlayer().getName())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use this item in this chunk.");
                return;
            }
            if (!(event.getRightClicked() instanceof LivingEntity)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can only use your Pokeball on living entities!");
                return;
            }
            Material material = getPokeballMaterial(event.getRightClicked().getType());
            if (material == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't use your Pokeball on this entity!");
            } else {
                // Give player spawn egg
                ItemStack toGive = new ItemStack(material);
                toGive.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
                ItemMeta meta = toGive.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + "Spawn Egg");
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                toGive.setItemMeta(meta);
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), toGive);

                // Remove entity
                event.getRightClicked().remove();

                // Message player
                event.getPlayer().sendMessage(ChatColor.GOLD + "Successfully captured the specimen.");

                // Remove player item
                PlayerInventory inv = event.getPlayer().getInventory();
                if (inv.getItemInMainHand().getAmount() > 1) {
                    inv.getItemInMainHand().setAmount(inv.getItemInMainHand().getAmount()-1);
                } else {
                    inv.setItemInMainHand(new ItemStack(Material.AIR));
                }
            }
        }
    }

    public Material getPokeballMaterial(EntityType type) {
        return switch (type) {
            case ZOMBIE -> Material.ZOMBIE_SPAWN_EGG;
            case ZOMBIE_VILLAGER -> Material.ZOMBIE_VILLAGER_SPAWN_EGG;
            case ZOMBIE_HORSE -> Material.ZOMBIE_HORSE_SPAWN_EGG;
            case ZOMBIFIED_PIGLIN -> Material.ZOMBIFIED_PIGLIN_SPAWN_EGG;
            case COW -> Material.COW_SPAWN_EGG;
            case SHEEP -> Material.SHEEP_SPAWN_EGG;
            case PARROT -> Material.PARROT_SPAWN_EGG;
            case PIG -> Material.PIG_SPAWN_EGG;
            case RABBIT -> Material.RABBIT_SPAWN_EGG;
            case CAVE_SPIDER -> Material.CAVE_SPIDER_SPAWN_EGG;
            case ARMADILLO -> Material.ARMADILLO_SPAWN_EGG;
            case AXOLOTL -> Material.AXOLOTL_SPAWN_EGG;
            case SLIME -> Material.SLIME_SPAWN_EGG;
            case BAT -> Material.BAT_SPAWN_EGG;
            case ALLAY -> Material.ALLAY_SPAWN_EGG;
            case BEE -> Material.BEE_SPAWN_EGG;
            case BLAZE -> Material.BLAZE_SPAWN_EGG;
            case BOGGED -> Material.BOGGED_SPAWN_EGG;
            case CAT -> Material.CAT_SPAWN_EGG;
            case BREEZE -> Material.BREEZE_SPAWN_EGG;
            case CAMEL -> Material.CAMEL_SPAWN_EGG;
            case SPIDER -> Material.SPIDER_SPAWN_EGG;
            case CREEPER -> Material.CREEPER_SPAWN_EGG;
            case ENDERMAN -> Material.ENDERMAN_SPAWN_EGG;
            case CHICKEN -> Material.CHICKEN_SPAWN_EGG;
            case CREAKING -> Material.CREAKING_SPAWN_EGG;
            case TROPICAL_FISH -> Material.TROPICAL_FISH_SPAWN_EGG;
            case COD -> Material.COD_SPAWN_EGG;
            case DOLPHIN -> Material.DOLPHIN_SPAWN_EGG;
            case DROWNED -> Material.DROWNED_SPAWN_EGG;
            case DONKEY -> Material.DONKEY_SPAWN_EGG;
            case ELDER_GUARDIAN -> Material.ELDER_GUARDIAN_SPAWN_EGG;
            case ENDERMITE -> Material.ENDERMITE_SPAWN_EGG;
            case EVOKER -> Material.EVOKER_SPAWN_EGG;
            case PILLAGER, ILLUSIONER -> Material.PILLAGER_SPAWN_EGG;
            case FOX -> Material.FOX_SPAWN_EGG;
            case FROG -> Material.FROG_SPAWN_EGG;
            case GHAST -> Material.GHAST_SPAWN_EGG;
            case GLOW_SQUID -> Material.GLOW_SQUID_SPAWN_EGG;
            case SQUID -> Material.SQUID_SPAWN_EGG;
            case GOAT -> Material.GOAT_SPAWN_EGG;
            case GUARDIAN -> Material.GUARDIAN_SPAWN_EGG;
            case HOGLIN -> Material.HOGLIN_SPAWN_EGG;
            case ZOGLIN -> Material.ZOGLIN_SPAWN_EGG;
            case PIGLIN -> Material.PIGLIN_SPAWN_EGG;
            case PIGLIN_BRUTE -> Material.PIGLIN_BRUTE_SPAWN_EGG;
            case HORSE -> Material.HORSE_SPAWN_EGG;
            case HUSK -> Material.HUSK_SPAWN_EGG;
            case IRON_GOLEM -> Material.IRON_GOLEM_SPAWN_EGG;
            case LLAMA -> Material.LLAMA_SPAWN_EGG;
            case MAGMA_CUBE -> Material.MAGMA_CUBE_SPAWN_EGG;
            case MOOSHROOM -> Material.MOOSHROOM_SPAWN_EGG;
            case MULE -> Material.MULE_SPAWN_EGG;
            case OCELOT -> Material.OCELOT_SPAWN_EGG;
            case PANDA -> Material.PANDA_SPAWN_EGG;
            case PHANTOM -> Material.PHANTOM_SPAWN_EGG;
            case POLAR_BEAR -> Material.POLAR_BEAR_SPAWN_EGG;
            case PUFFERFISH -> Material.PUFFERFISH_SPAWN_EGG;
            case RAVAGER -> Material.RAVAGER_SPAWN_EGG;
            case SALMON -> Material.SALMON_SPAWN_EGG;
            case SHULKER -> Material.SHULKER_SPAWN_EGG;
            case SILVERFISH -> Material.SILVERFISH_SPAWN_EGG;
            case SKELETON -> Material.SKELETON_SPAWN_EGG;
            case SKELETON_HORSE -> Material.SKELETON_HORSE_SPAWN_EGG;
            case SNIFFER -> Material.SNIFFER_SPAWN_EGG;
            case SNOW_GOLEM -> Material.SNOW_GOLEM_SPAWN_EGG;
            case STRAY -> Material.STRAY_SPAWN_EGG;
            case STRIDER -> Material.STRIDER_SPAWN_EGG;
            case TADPOLE -> Material.TADPOLE_SPAWN_EGG;
            case WOLF -> Material.WOLF_SPAWN_EGG;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SPAWN_EGG;
            case TRADER_LLAMA -> Material.TRADER_LLAMA_SPAWN_EGG;
            case WANDERING_TRADER -> Material.WANDERING_TRADER_SPAWN_EGG;
            case TURTLE -> Material.TURTLE_SPAWN_EGG;
            case VEX -> Material.VEX_SPAWN_EGG;
            case VINDICATOR -> Material.VINDICATOR_SPAWN_EGG;
            case WITCH -> Material.WITCH_SPAWN_EGG;
            default -> null;
        };
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.discoverRecipe(CustomItemCrafting.buildersWandKey);
        player.discoverRecipe(CustomItemCrafting.gogglesKey);
        player.discoverRecipe(CustomItemCrafting.kineticKey);
        player.discoverRecipe(CustomItemCrafting.treeFellerDiamondKey);
        player.discoverRecipe(CustomItemCrafting.treeFellerNetheriteKey);
        player.discoverRecipe(CustomItemCrafting.tombstoneKey);
        player.discoverRecipe(CustomItemCrafting.pokeballKey);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        ItemStack itemToRemove = null;

        // Determine if player has tombstone
        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.CHEST && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Tombstone")) {
                itemToRemove = item;
                break;
            }
        }

        // If player does not have tombstone, return with no message as this is a normal death
        if (itemToRemove == null) {
            return;
        }

        // Handle player out of world
        if (loc.getY() < -64 || loc.getY() > 320) {
            player.sendMessage(ChatColor.RED + "Unfortunately, because you were outside of the world, a tombstone could not be placed.");
            player.sendMessage(ChatColor.RED + "Your items were dropped and you died at X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ".");
            return;
        }

        // Handle player in claim that they do not have access to
        if (!LandManager.doesPlayerHaveAccessToChunkAt(loc, player.getName())) {
            player.sendMessage(ChatColor.RED + "Unfortunately, because you were inside a claim other than yours, a tombstone could not be placed.");
            player.sendMessage(ChatColor.RED + "Your items were dropped and you died at X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ".");
            return;
        }

        // Remove item
        if (itemToRemove.getAmount() > 1) {
            itemToRemove.setAmount(itemToRemove.getAmount()-1);
        } else event.getDrops().remove(itemToRemove);

        // Place chest
        Block leftBlock = loc.getWorld().getBlockAt(loc);
        Block rightBlock = leftBlock.getRelative(1, 0, 0);
        leftBlock.setType(Material.CHEST);
        rightBlock.setType(Material.CHEST);
        Chest left = (Chest) leftBlock.getState();
        Chest right = (Chest) rightBlock.getState();
        org.bukkit.block.data.type.Chest chestDataLeft = (org.bukkit.block.data.type.Chest) left.getBlockData();
        org.bukkit.block.data.type.Chest chestDataRight = (org.bukkit.block.data.type.Chest) right.getBlockData();
        chestDataLeft.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
        chestDataRight.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
        leftBlock.setBlockData(chestDataLeft, true);
        rightBlock.setBlockData(chestDataRight, true);

        // Add to chest
        Inventory leftInventory = left.getBlockInventory();
        Inventory rightInventory = right.getBlockInventory();
        for (int i = 0; i < event.getDrops().size(); i++) {
            ItemStack item = event.getDrops().get(i);

            if (i < leftInventory.getSize()) {
                rightInventory.setItem(i, item);
            } else {
                leftInventory.setItem(i - leftInventory.getSize(), item);
            }
        }

        // Remove drops
        event.getDrops().clear();

        // Message player
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[TOMBSTONE ACTIVATED] " + ChatColor.GOLD + "Your items were stored at X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ".");
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();

        if (event.getSlotType() == PlayerArmorChangeEvent.SlotType.HEAD) {
           if (newItem != null && newItem.getType() == Material.NETHERITE_HELMET && doesItemHaveName(newItem, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Night Vision Goggles")) {
               PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false);
               player.addPotionEffect(effect);
           } else {
               if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                   player.removePotionEffect(PotionEffectType.NIGHT_VISION);
               }
           }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL && event.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.ELYTRA && doesItemHaveName(chestplate, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Kinetic Elytra")) {
                event.setCancelled(true);
            }
        }
    }

    public boolean doesItemHaveName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            return meta.getDisplayName().equalsIgnoreCase(name);
        } else return false;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();

        if (event.getPlayer().isSneaking() && isLog(event.getBlock()) && item != null && (item.getType() == Material.DIAMOND_AXE || item.getType() == Material.NETHERITE_AXE)) {
            if (doesItemHaveName(item, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Tree Feller") && LandManager.doesPlayerHaveAccessToChunkAt(event.getBlock().getLocation(), event.getPlayer().getName())) {
                breakAllLogsNextTo(event.getBlock(), event.getPlayer(), item);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null && item.getType() == Material.STICK && doesItemHaveName(item, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Builder's Wand") && event.getClickedBlock() != null) {
            extendBlocks(event.getClickedBlock(), event.getPlayer(), event.getBlockFace());
        }
    }


    public boolean isLog(Block block) {
        return switch (block.getType()) {
            case OAK_LOG, SPRUCE_LOG, CHERRY_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, CRIMSON_STEM, WARPED_STEM, MANGROVE_LOG, MANGROVE_ROOTS, MUDDY_MANGROVE_ROOTS, PALE_OAK_LOG -> true;
            default -> false;
        };
    }

    public void breakAllLogsNextTo(Block block, Player player, ItemStack item) {
        int durability = item.getType().getMaxDurability() - item.getDurability();
        int max_blocks_to_break = Math.min(Math.max(durability, 1), 25);
        if (max_blocks_to_break <= 1) {
            player.sendMessage(ChatColor.RED + "Your Tree Feller is about to break!");
            return;
        }

        Stack<Block> stack = new Stack<>();
        stack.push(block);
        int blocksBroken = 0;

        while (!stack.isEmpty() && blocksBroken < max_blocks_to_break) {
            Block blockOn = stack.pop();

            if (blockOn.getY() > -64 && blockOn.getY() < 320) {
                for (Vector direction : directions) {
                    Block adjacentBlock = blockOn.getRelative(direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
                    if (adjacentBlock.getType() == blockOn.getType()) {
                        stack.add(adjacentBlock);
                    }
                }
            }

            blocksBroken += 1;
            blockOn.breakNaturally();
        }

        item.setDurability((short) (item.getDurability() + blocksBroken));
    }

    public void extendBlocks(Block block, Player player, BlockFace face) {
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player) && (now - cooldowns.get(player)) < 500) {
            return;
        }
        cooldowns.put(player, now);

        List<Block> blocksToExtend = new ArrayList<>();
        blocksToExtend.add(block);
        int blocksAdded = 1;
        Stack<Block> stack = new Stack<>();
        stack.add(block);

        List<Vector> directions = perpendicularDirectionsMap.getOrDefault(face, new ArrayList<>());
        if (directions.isEmpty()) return;

        while (!stack.isEmpty() && blocksAdded < 64) {
            Block blockOn = stack.pop();

            // Search for more blocks to extend in each direction
            for (Vector direction : directions) {
                Block adjacentBlock = blockOn.getRelative(direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
                Block blockToPlace = adjacentBlock.getRelative(face);

                if (adjacentBlock.getType() == block.getType() && (blockToPlace.getType() == Material.AIR || blockToPlace.getType() == Material.WATER) && !blocksToExtend.contains(adjacentBlock)) {
                    blocksAdded += 1;
                    stack.add(adjacentBlock);
                    blocksToExtend.add(adjacentBlock);
                }
            }
        }

        // Ensure player has enough items
        if (!player.getInventory().contains(block.getType(), blocksAdded)) {
            player.sendMessage(ChatColor.RED + "You do not have enough blocks! (You need " + blocksAdded + " " + block.getType().toString() + ")");
            return;
        }

        // Place blocks
        for (Block blockOn : blocksToExtend) {
            Block blockToPlace = blockOn.getRelative(face);
            blockToPlace.setType(block.getType());
        }

        // Remove from player inventory
        removeFromInventory(player.getInventory(), block.getType(), blocksAdded);
    }

    private Set<Vector> getDirections() {
        Set<Vector> directions = new HashSet<>();
        directions.add(new Vector(1, 0, 0)); // Right
        directions.add(new Vector(-1, 0, 0)); // Left
        directions.add(new Vector(0, 1, 0)); // Up
        directions.add(new Vector(0, -1, 0)); // Down
        directions.add(new Vector(0, 0, 1)); // Forward
        directions.add(new Vector(0, 0, -1)); // Backward
        return directions;
    }

    private static void populatePerpendicularDirections() {
        perpendicularDirectionsMap.put(BlockFace.UP, createPerpendicularVectors(new Vector(0, 1, 0)));
        perpendicularDirectionsMap.put(BlockFace.DOWN, createPerpendicularVectors(new Vector(0, -1, 0)));
        perpendicularDirectionsMap.put(BlockFace.NORTH, createPerpendicularVectors(new Vector(0, 0, 1)));
        perpendicularDirectionsMap.put(BlockFace.SOUTH, createPerpendicularVectors(new Vector(0, 0, -1)));
        perpendicularDirectionsMap.put(BlockFace.EAST, createPerpendicularVectors(new Vector(1, 0, 0)));
        perpendicularDirectionsMap.put(BlockFace.WEST, createPerpendicularVectors(new Vector(-1, 0, 0)));
    }

    // Helper method to create the perpendicular vectors for a given block face vector
    private static List<Vector> createPerpendicularVectors(Vector direction) {
        List<Vector> perpendicularVectors = new ArrayList<>();

        if (direction.getX() == 0 && direction.getY() == 1 && direction.getZ() == 0) { // UP
            perpendicularVectors.add(new Vector(1, 0, 0));  // Positive X
            perpendicularVectors.add(new Vector(-1, 0, 0)); // Negative X
            perpendicularVectors.add(new Vector(0, 0, 1));  // Positive Z
            perpendicularVectors.add(new Vector(0, 0, -1)); // Negative Z
        } else if (direction.getX() == 0 && direction.getY() == -1 && direction.getZ() == 0) { // DOWN
            perpendicularVectors.add(new Vector(1, 0, 0));  // Positive X
            perpendicularVectors.add(new Vector(-1, 0, 0)); // Negative X
            perpendicularVectors.add(new Vector(0, 0, 1));  // Positive Z
            perpendicularVectors.add(new Vector(0, 0, -1)); // Negative Z
        } else if (direction.getX() == 0 && direction.getY() == 0 && direction.getZ() == 1) { // NORTH
            perpendicularVectors.add(new Vector(1, 0, 0));  // Positive X
            perpendicularVectors.add(new Vector(-1, 0, 0)); // Negative X
            perpendicularVectors.add(new Vector(0, 1, 0));  // Positive Y
            perpendicularVectors.add(new Vector(0, -1, 0)); // Negative Y
        } else if (direction.getX() == 0 && direction.getY() == 0 && direction.getZ() == -1) { // SOUTH
            perpendicularVectors.add(new Vector(1, 0, 0));  // Positive X
            perpendicularVectors.add(new Vector(-1, 0, 0)); // Negative X
            perpendicularVectors.add(new Vector(0, 1, 0));  // Positive Y
            perpendicularVectors.add(new Vector(0, -1, 0)); // Negative Y
        } else if (direction.getX() == 1 && direction.getY() == 0 && direction.getZ() == 0) { // EAST
            perpendicularVectors.add(new Vector(0, 1, 0));  // Positive Y
            perpendicularVectors.add(new Vector(0, -1, 0)); // Negative Y
            perpendicularVectors.add(new Vector(0, 0, 1));  // Positive Z
            perpendicularVectors.add(new Vector(0, 0, -1)); // Negative Z
        } else if (direction.getX() == -1 && direction.getY() == 0 && direction.getZ() == 0) { // WEST
            perpendicularVectors.add(new Vector(0, 1, 0));  // Positive Y
            perpendicularVectors.add(new Vector(0, -1, 0)); // Negative Y
            perpendicularVectors.add(new Vector(0, 0, 1));  // Positive Z
            perpendicularVectors.add(new Vector(0, 0, -1)); // Negative Z
        }

        return perpendicularVectors;
    }

    public void removeFromInventory(Inventory inventory, Material material, int amount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                // Update quantity of item
                int itemAmount = item.getAmount();
                int amountToRemove = Math.min(amount, itemAmount);
                item.setAmount(itemAmount - amountToRemove);
                amount -= amountToRemove;

                // If item is now empty, remove item
                if (itemAmount <= 0) {
                    inventory.setItem(i, null);
                }

                // If all items removed, stop removing
                if (amount <= 0) {
                    break;
                }
            }
        }
    }


}
