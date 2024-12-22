package org.gabooj.difficulty;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class DifficultyListener implements Listener {

    // Progressive Difficulty System
    // - 0: Base difficulty
    // - 1: Moderately harder
    // - 2: Hard

    private final JavaPlugin plugin;
    private final Server server;
    private final Random random;

    private final int DIFFICULTY_STEP = 1000;
    private final ChatColor[] colorByDifficultNames = new ChatColor[] {
            ChatColor.YELLOW,
            ChatColor.RED,
            ChatColor.DARK_RED
    };
    private final Material[] medium_melee_weapon = new Material[] {
            Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_HOE, Material.WOODEN_SHOVEL,
            Material.STONE_SWORD, Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SHOVEL,
            Material.GOLDEN_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE, Material.GOLDEN_HOE, Material.GOLDEN_SHOVEL,
            Material.IRON_SWORD, Material.IRON_AXE
    };

    private final Material[] hard_melee_weapon = new Material[] {
            Material.STONE_SWORD, Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SHOVEL,
            Material.GOLDEN_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE, Material.GOLDEN_HOE, Material.GOLDEN_SHOVEL,
            Material.IRON_SWORD, Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_HOE, Material.IRON_SHOVEL,
            Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.DIAMOND_SHOVEL,
    };

    private final Material[] medium_armor_ordered = new Material[] {
            Material.LEATHER_HELMET, Material.LEATHER_HELMET, Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
            Material.LEATHER_CHESTPLATE, Material.LEATHER_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
            Material.LEATHER_LEGGINGS, Material.LEATHER_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
            Material.LEATHER_BOOTS, Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS
    };

    private final Material[] hard_armor_ordered = new Material[] {
            Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.TURTLE_HELMET,
            Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS,
            Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS,
    };

    public DifficultyListener(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.random = new Random();

        server.getPluginManager().registerEvents(this, plugin);
    }

    public int getDifficulty(Location loc) {
        int x = Math.abs(loc.getBlockX());
        int y = Math.abs(loc.getBlockY());
        int z = Math.abs(loc.getBlockZ());

        int difficulty = (Math.max(x, Math.max(y, z)) / DIFFICULTY_STEP);

        return Math.min(Math.max(difficulty, 0), 2);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            event.getEntity().setCustomName("");
        }
    }

    @EventHandler
    public void onHostileMobSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            switch (event.getEntityType()) {
                case CREEPER -> handle_creeper((Creeper) event.getEntity());
                case ZOMBIE, ZOMBIE_VILLAGER, PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN -> handle_melee_and_armor(entity);
                case SKELETON -> handle_bow_and_armor(entity);
                case SPIDER -> handle_spider((Spider) event.getEntity());
                case CAVE_SPIDER, PILLAGER -> make_faster(entity);
                case SLIME, MAGMA_CUBE, GUARDIAN, ELDER_GUARDIAN -> make_stronger(entity);
                case ENDERMAN, BREEZE, GHAST, BLAZE -> make_resistant(entity);
            }
            updateDisplayName(entity, entity.getHealth());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            updateDisplayName(entity, entity.getHealth() - event.getFinalDamage());
        }
    }

    public void updateDisplayName(LivingEntity entity, double health) {
        // Get color of name
        ChatColor color = null;
        if (isPassive(entity)) {
            color = ChatColor.GREEN;
        } else {
            color = colorByDifficultNames[getDifficulty(entity.getLocation())];
        }

        // Get hearts
        int hearts = (int) health / 2;
        boolean hasHalfHeart = ((int) health) % 2 == 1;

        // Update name
        StringBuilder displayName = new StringBuilder();
        displayName.append(color + "");
        displayName.append("♥".repeat(Math.min(Math.max(0, hearts), 20)));
        if (hasHalfHeart || displayName.isEmpty()) {
            displayName.append("♡");
        }
        entity.setCustomName(displayName.toString());
    }

    public boolean isPassive(Entity entity) {
        return switch (entity.getType()) {
            case COW, SHEEP, CHICKEN, PIG, RABBIT, HORSE, DONKEY, MULE,
                 CAT, PARROT, OCELOT, PANDA, LLAMA, TURTLE, FOX, DOLPHIN,
                 WOLF, AXOLOTL, BEE, VILLAGER, WANDERING_TRADER -> true;
            default -> false;
        };
    }

    public void handle_spider(Spider spider) {
        int difficulty = getDifficulty(spider.getLocation());
        if (difficulty == 0) return;

        boolean addInvisibility = false;
        if (difficulty == 1)  {
            int randNum = random.nextInt(8);
            addInvisibility = (randNum == 0);
        } else {
            int randNum = random.nextInt(4);
            addInvisibility = (randNum == 0);
        }

        // Apply the Invisibility effect to the spider
        if (addInvisibility) {
            PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1);
            spider.addPotionEffect(invisibility);
        }
    }


    public void handle_creeper(Creeper creeper) {
        int difficulty = getDifficulty(creeper.getLocation());

        if (difficulty == 0) {
            creeper.setExplosionRadius(3);
        } else if (difficulty == 1) {
            creeper.setExplosionRadius(4);
        } else {
            creeper.setExplosionRadius(5);
        }
    }

    public void make_resistant(LivingEntity entity) {
        PotionEffect strength = new PotionEffect(PotionEffectType.RESISTANCE, 10000, 1);
        entity.addPotionEffect(strength);
    }

    public void make_stronger(LivingEntity entity) {
        PotionEffect strength = new PotionEffect(PotionEffectType.STRENGTH, 10000, 1);
        entity.addPotionEffect(strength);
    }

    public void make_faster(LivingEntity entity) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 10000, 1);
        entity.addPotionEffect(speed);
    }

    public void handle_bow_and_armor(LivingEntity entity) {
        int difficulty = getDifficulty(entity.getLocation());
        if (difficulty == 0) return;

        // Set weapon
        ItemStack stack = getRandomBow(difficulty);
        if (stack != null) {
            entity.getEquipment().setItemInMainHand(stack);
        }

        // Update armor
        Material helmet = getArmorAtIndex(difficulty, 0);
        if (helmet != null) {
            entity.getEquipment().setHelmet(toItemStack(helmet));
        }

        Material chestplate = getArmorAtIndex(difficulty, 1);
        if (chestplate != null) {
            entity.getEquipment().setChestplate(toItemStack(chestplate));
        }

        Material leggings = getArmorAtIndex(difficulty, 2);
        if (leggings != null) {
            entity.getEquipment().setLeggings(toItemStack(leggings));
        }

        Material boots = getArmorAtIndex(difficulty, 3);
        if (boots != null) {
            entity.getEquipment().setBoots(toItemStack(boots));
        }
    }

    public void handle_melee_and_armor(LivingEntity entity) {
        int difficulty = getDifficulty(entity.getLocation());
        if (difficulty == 0) return;

        // Update weapon
        Material weapon = getRandomMeleeWeaponMaterial(difficulty);
        if (weapon != null) {
            entity.getEquipment().setItemInMainHand(toItemStack(weapon));
        }

        // Update armor
        Material helmet = getArmorAtIndex(difficulty, 0);
        if (helmet != null) {
            entity.getEquipment().setHelmet(toItemStack(helmet));
        }

        Material chestplate = getArmorAtIndex(difficulty, 1);
        if (chestplate != null) {
            entity.getEquipment().setChestplate(toItemStack(chestplate));
        }

        Material leggings = getArmorAtIndex(difficulty, 2);
        if (leggings != null) {
            entity.getEquipment().setLeggings(toItemStack(leggings));
        }

        Material boots = getArmorAtIndex(difficulty, 3);
        if (boots != null) {
            entity.getEquipment().setBoots(toItemStack(boots));
        }
    }

    @EventHandler
    public void onDragonChangeEvent(EnderDragonChangePhaseEvent event) {
        Location loc = event.getEntity().getLocation();
        World world = loc.getWorld();
        if (event.getCurrentPhase() == EnderDragon.Phase.CIRCLING) {
            world.spawnEntity(loc, EntityType.END_CRYSTAL);
        } else if (event.getCurrentPhase() == EnderDragon.Phase.FLY_TO_PORTAL) {
            for (int i = 0; i < 5; i++) {
                Location newLocation = new Location(loc.getWorld(), loc.getBlockX() + random.nextInt(50) - 25, loc.getBlockY(), loc.getBlockZ() + random.nextInt(50) - 25);
                FallingBlock fallingBlock = world.spawnFallingBlock(newLocation, Material.LAVA.createBlockData());
                fallingBlock.setHurtEntities(true);
                fallingBlock.setDropItem(false);
            }
        } else if (event.getCurrentPhase() == EnderDragon.Phase.LEAVE_PORTAL) {
            for (int i = 0; i < 5; i++) {
                Location newLocation = new Location(loc.getWorld(), loc.getBlockX() + random.nextInt(50) - 25, loc.getBlockY(), loc.getBlockZ() + random.nextInt(50) - 25 );
                FallingBlock fallingBlock = world.spawnFallingBlock(newLocation, Material.WATER.createBlockData());
                fallingBlock.setHurtEntities(true);
                fallingBlock.setDropItem(false);
            }
        } else if (event.getCurrentPhase() == EnderDragon.Phase.LAND_ON_PORTAL) {
            for (int i = 0; i < 5; i++) {
                Location newLocation = new Location(loc.getWorld(), loc.getBlockX() + random.nextInt(50) - 25, loc.getBlockY(), loc.getBlockZ() + random.nextInt(50) - 25 );
                world.spawnEntity(newLocation, EntityType.WITHER_SKELETON);
            }
        } else if (event.getCurrentPhase() == EnderDragon.Phase.BREATH_ATTACK) {
            Location newLocation = new Location(loc.getWorld(), loc.getBlockX() + random.nextInt(50) - 25, loc.getBlockY(), loc.getBlockZ() + random.nextInt(50) - 25 );
            world.spawnEntity(newLocation, EntityType.GHAST);
        }
    }

    // Listen when an end dragon is targeting a player

    public ItemStack toItemStack(Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        stack.setItemMeta(meta);
        return stack;
    }

    public Material getArmorAtIndex(int difficulty, int index) {
        if (difficulty == 0) {
            return null;
        } else if (difficulty == 1) {
            int updateArmor = random.nextInt(16);

            if (updateArmor == 0) {
                int armor_index = random.nextInt(5) + 5 * index;
                return medium_armor_ordered[armor_index];
            } else return null;
        } else {
            int updateArmor = random.nextInt(8);

            if (updateArmor == 0) {
                int armor_index = random.nextInt(5) + 5 * index;
                return medium_armor_ordered[armor_index];
            } else return null;
        }
    }

    public ItemStack getRandomBow(int difficulty) {
        ItemStack stack = new ItemStack(Material.BOW);
        if (difficulty == 0) {
            return stack;
        } else if (difficulty == 1) {
            int updateBow = random.nextInt(4);
            if (updateBow == 0) {
                int randomEnchant = random.nextInt(4);

                if (randomEnchant == 0) {
                    int level = random.nextInt(3) + 1;
                    stack.addUnsafeEnchantment(Enchantment.POWER, level);
                } else if (randomEnchant == 1) {
                    stack.addUnsafeEnchantment(Enchantment.INFINITY, 1);
                } else if (randomEnchant == 2) {
                    stack.addUnsafeEnchantment(Enchantment.FLAME, 1);
                } else {
                    int level = random.nextInt(3) + 1;
                    stack.addUnsafeEnchantment(Enchantment.PUNCH, level);
                }
            }
        } else {
            int updateBow = random.nextInt(2);
            if (updateBow == 0) {
                int randomEnchant = random.nextInt(4);

                if (randomEnchant == 0) {
                    int level = random.nextInt(3) + 1;
                    stack.addUnsafeEnchantment(Enchantment.POWER, level);
                } else if (randomEnchant == 1) {
                    stack.addUnsafeEnchantment(Enchantment.INFINITY, 1);
                } else if (randomEnchant == 2) {
                    stack.addUnsafeEnchantment(Enchantment.FLAME, 1);
                } else {
                    int level = random.nextInt(3) + 1;
                    stack.addUnsafeEnchantment(Enchantment.PUNCH, level);
                }
            }
        }
        return stack;
    }

    public Material getRandomMeleeWeaponMaterial(int difficulty) {
        if (difficulty == 0) {
            return null;
        } else if (difficulty == 1) {
            int updateWeapon = random.nextInt(4);

            if (updateWeapon == 0) {
                return medium_melee_weapon[random.nextInt(medium_melee_weapon.length)];
            } else return null;
        } else {
            int updateWeapon = random.nextInt(2);

            if (updateWeapon == 0) {
                return hard_melee_weapon[random.nextInt(hard_melee_weapon.length)];
            } else return null;
        }
    }

    //
    // Wither
    //

    @EventHandler
    public void onWitherHeadShoot(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            WitherSkull skull = (WitherSkull) event.getEntity();
            if (skull.getShooter() instanceof Wither && random.nextInt(10) == 0) {
                Location loc = ((Wither) skull.getShooter()).getLocation();
                loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
            }
        }
    }

}
