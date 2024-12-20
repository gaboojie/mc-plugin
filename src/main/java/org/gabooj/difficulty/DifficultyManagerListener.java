package org.gabooj.difficulty;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class DifficultyManagerListener implements Listener {

    private final JavaPlugin plugin;
    private final Server server;
    private final Random random;

    private final int DIFFICULTY_STEP = 1000;
    private final ChatColor[] colorByDifficultNames = new ChatColor[] {
            ChatColor.YELLOW,
            ChatColor.RED,
            ChatColor.DARK_RED
    };

    public DifficultyManagerListener(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.random = new Random();

        server.getPluginManager().registerEvents(this, plugin);
    }

    // Progressive Difficulty System
    // - 0: Base difficulty
    // - 1: Moderately harder
    // - 2: Hard

    public int getDifficulty(Location loc) {
        int x = Math.abs(loc.getBlockX());
        int y = Math.abs(loc.getBlockY());
        int z = Math.abs(loc.getBlockZ());

        int difficulty = (Math.max(x, Math.max(y, z)) / DIFFICULTY_STEP);

        return Math.min(Math.max(difficulty, 0), 2);
    }




    // Mob spawn event
    // With switch

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            event.getEntity().setCustomName("");
        }
    }

    @EventHandler
    public void onHostileMobSpawn(EntitySpawnEvent event) {
        switch (event.getEntityType()) {
            case CREEPER -> handle_creeper((Creeper) event.getEntity());
        }

        if (event.getEntity() instanceof LivingEntity entity) {
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

    //
    //
    //

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

//    public void handle_zombie(Zombie zombie) {
//        int difficulty = getDifficulty(zombie.getLocation());
//        if (difficulty == 0) return;
//
//        if (difficulty == 1) {
//            // Update weapon
//            boolean updateWeapon = random.nextBoolean();
//            if (updateWeapon) {
//
//            }
//
//            // Update armor
//        } else {
//
//        }
//
//    }


}
