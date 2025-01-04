package org.gabooj.customitem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class CustomItemCrafting {

    private final JavaPlugin plugin;
    private final Server server;

    public static NamespacedKey pokeballKey, buildersWandKey, treeFellerDiamondKey, treeFellerNetheriteKey, gogglesKey, kineticKey, tombstoneKey;

    public CustomItemCrafting(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;

        addCraftingRecipes();
    }

    public void addCraftingRecipes() {
        addTreeFellerRecipes();
        addBuildersWandRecipe();
        addKineticRecipe();
        addNightVisionGoogles();
        addStorageRecipe();
        addPokeballRecipe();
    }

    public void addPokeballRecipe() {
        // Pokeball
        ItemStack result = new ItemStack(Material.CLAY_BALL);
        result.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Right click an entity to sacrifice the entity for a spawn egg of the same type.");
        lore.add(ChatColor.LIGHT_PURPLE + "[Single-use]");

        ItemMeta meta = result.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Pokeball");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        result.setItemMeta(meta);

        pokeballKey = new NamespacedKey(plugin, "Pokeball");
        ShapedRecipe recipe = new ShapedRecipe(pokeballKey, result);

        // Add recipe
        recipe.shape("aaa", "aba", "aaa");
        recipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.DIAMOND));
        recipe.setIngredient('b', new RecipeChoice.MaterialChoice(Material.CLAY_BALL));
        server.addRecipe(recipe);
    }

    public void addStorageRecipe() {
        // Storage
        ItemStack result = new ItemStack(Material.CHEST);
        result.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Will place your items in a chest where you die. [Single-use]");
        lore.add(ChatColor.LIGHT_PURPLE + "Will not place your items if you are in someone else's claim, though.");

        ItemMeta meta = result.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Tombstone");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        result.setItemMeta(meta);

        tombstoneKey = new NamespacedKey(plugin, "Tombstone");
        ShapedRecipe recipe = new ShapedRecipe(tombstoneKey, result);

        // Add recipe
        recipe.shape("aaa", "aba", "aaa");
        recipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.ENDER_EYE));
        recipe.setIngredient('b', new RecipeChoice.MaterialChoice(Material.NETHER_STAR));
        server.addRecipe(recipe);
    }

    public void addBuildersWandRecipe() {
        // Builder's wand
        ItemStack result = new ItemStack(Material.STICK);
        result.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Right click on a block to extend all connected blocks.");
        setMeta(result, lore, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Builder's Wand");

        buildersWandKey = new NamespacedKey(plugin, "BuildersWand");
        ShapedRecipe recipe = new ShapedRecipe(buildersWandKey, result);

        // Add recipe
        recipe.shape("abc", "def", "ghi");
        recipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.OAK_LOG));
        recipe.setIngredient('b', new RecipeChoice.MaterialChoice(Material.BIRCH_LOG));
        recipe.setIngredient('c', new RecipeChoice.MaterialChoice(Material.DARK_OAK_LOG));
        recipe.setIngredient('d', new RecipeChoice.MaterialChoice(Material.SPRUCE_LOG));
        recipe.setIngredient('e', new RecipeChoice.MaterialChoice(Material.STICK));
        recipe.setIngredient('f', new RecipeChoice.MaterialChoice(Material.CHERRY_LOG));
        recipe.setIngredient('g', new RecipeChoice.MaterialChoice(Material.ACACIA_LOG));
        recipe.setIngredient('h', new RecipeChoice.MaterialChoice(Material.JUNGLE_LOG));
        recipe.setIngredient('i', new RecipeChoice.MaterialChoice(Material.MANGROVE_LOG));
        server.addRecipe(recipe);
    }

    public void addTreeFellerRecipes() {
        // Diamond axe
        ItemStack diamondResult = new ItemStack(Material.DIAMOND_AXE);
        ArrayList<String> diamondLore = new ArrayList<>();
        diamondLore.add(ChatColor.LIGHT_PURPLE + "Sneak and break a log to break all connected logs!");
        setMeta(diamondResult, diamondLore, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Tree Feller");

        treeFellerDiamondKey = new NamespacedKey(plugin, "TreeFellerDiamond");
        ShapedRecipe diamondRecipe = new ShapedRecipe(treeFellerDiamondKey, diamondResult);
        diamondRecipe.shape("aaa", "aaa", "aaa");
        diamondRecipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.DIAMOND_AXE));
        server.addRecipe(diamondRecipe);

        // Netherite axe
        ItemStack netheriteResult = new ItemStack(Material.NETHERITE_AXE);
        ArrayList<String> netheriteLore = new ArrayList<>();
        netheriteLore.add(ChatColor.LIGHT_PURPLE + "Sneak and break a log to break all connected logs!");
        setMeta(netheriteResult, netheriteLore, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Tree Feller");

        treeFellerNetheriteKey = new NamespacedKey(plugin, "TreeFellerNetherite");
        ShapedRecipe netheriteRecipe = new ShapedRecipe(treeFellerNetheriteKey, netheriteResult);
        netheriteRecipe.shape("aaa", "aaa", "aaa");
        netheriteRecipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.NETHERITE_AXE));
        server.addRecipe(netheriteRecipe);
    }

    public void addNightVisionGoogles() {
        // Goggles
        ItemStack result = new ItemStack(Material.NETHERITE_HELMET);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Gives permanent night vision while wearing.");
        setMeta(result, lore, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Night Vision Goggles");

        gogglesKey = new NamespacedKey(plugin, "Goggles");
        ShapedRecipe recipe = new ShapedRecipe(gogglesKey, result);
        recipe.shape("aaa", "aba", "aaa");
        recipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK));
        recipe.setIngredient('b', new RecipeChoice.MaterialChoice(Material.NETHERITE_HELMET));
        server.addRecipe(recipe);
    }

    public void addKineticRecipe() {
        // Kinetic
        ItemStack result = new ItemStack(Material.ELYTRA);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Immune to kinetic damage from using an elytra.");
        lore.add(ChatColor.LIGHT_PURPLE + "Does not make you immune to fall damage.");
        setMeta(result, lore, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Kinetic Elytra");

        kineticKey = new NamespacedKey(plugin, "KineticElytra");
        ShapedRecipe recipe = new ShapedRecipe(kineticKey, result);
        recipe.shape("aaa", "aba", "aaa");
        recipe.setIngredient('a', new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK));
        recipe.setIngredient('b', new RecipeChoice.MaterialChoice(Material.ELYTRA));
        server.addRecipe(recipe);
    }

    public void setMeta(ItemStack item, ArrayList<String> lore, String displayName) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
    }

    public void setNoEnchantmentsShown(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

}
