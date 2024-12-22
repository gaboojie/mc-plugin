package org.gabooj.misc;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MiscCommands implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Server server;

    public MiscCommands(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to issue this command.");
            return true;
        }
        Player player = (Player) commandSender;

        if (label.equalsIgnoreCase("info")) {
            infoCommand(player);
        } else if (label.equalsIgnoreCase("rules")) {
            rulesCommand(player);
        } else if (label.equalsIgnoreCase("commands")) {
            showCommandsCommand(player);
        }
        return true;
    }

    public void infoCommand(Player player) {
        for (int i = 0; i < 10; i++) player.sendMessage("");
        player.sendMessage(header("SERVER INFO"));

        // Sign Info
        player.sendMessage("\n");
        player.sendMessage(header("Sign Shop"));
        player.sendMessage(body("To place a sign shop, you must claim the chunk the chest is within using /claim."));
        player.sendMessage(body("You need to use both an oak sign and an oak chest to make the shop."));
        player.sendMessage(body("The sign in your shop should be formatted to look like:"));
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + " Selling 40");
        player.sendMessage(ChatColor.GOLD + " OAK_LOG");
        player.sendMessage(ChatColor.GOLD + " For 3");
        player.sendMessage(ChatColor.GOLD + " DIAMOND");
        player.sendMessage("");
        player.sendMessage(body("In this example, the shop would be selling 40 oak logs to buy 3 diamonds."));

        // Claim Info
        player.sendMessage("\n");
        player.sendMessage(header("Claims"));
        player.sendMessage(body("Use /claim to claim and /unclaim to unclaim the chunk you are within. This will prevent other players from griefing/stealing from your land. It won't protect any mobs in your land, though."));
        player.sendMessage(body("Use /map to get a map of claimed chunks near you."));
        player.sendMessage(body("Use /friend to manage who has access to your claimed chunks. By default, only you will have access."));
        player.sendMessage(body("You can only claim chunks within the first 10,000 blocks in the Overworld."));

        // Custom Item info
        player.sendMessage("\n");
        player.sendMessage(header("Custom Items"));
        player.sendMessage(body("To improve quality of life on the server, some custom items have been added and can be crafted:"));
        player.sendMessage(ChatColor.GOLD + " Builder's Wand" + ChatColor.GREEN + " - Extends all blocks of a given type to make building large projects easier.");
        player.sendMessage(ChatColor.GOLD + " Tree Feller" + ChatColor.GREEN + " - Shift and break a log to break all connected logs.");
        player.sendMessage(ChatColor.GOLD + " Night vision goggles" + ChatColor.GREEN + " - To make it fair to the players not using the Full-Bright mod, this item can be crafted to give you unlimited night vision.");
        player.sendMessage(ChatColor.GOLD + " Kinetic Elytra" + ChatColor.GREEN + " - An elytra that removes slamming into wall damage, so players don't randomly die 10,000 blocks in the Nether and quit (@Colin).");
        player.sendMessage(ChatColor.GOLD + " Tombstone" + ChatColor.GREEN + " - Craftable item that will place a chest with your loot where you die (@Alex).");
        player.sendMessage(ChatColor.GOLD + " Shulker Box" + ChatColor.GREEN + " - Right click any shulker box in the air to open it's inventory (@Alex).");

        // Hard++ Info
        player.sendMessage("\n");
        player.sendMessage(header(ChatColor.RED + "" + ChatColor.BOLD + "Hard++" + ChatColor.GREEN + "" + ChatColor.BOLD));
        player.sendMessage(body("In an effort to make MC more challenging, this world will increase in difficulty the farther you travel from the world origin."));
        player.sendMessage(body("The world is on regular HARD mode for the first 500 blocks from spawn, then gets harder for the next 500 blocks, and becomes even harder after 1000 blocks from spawn."));
        player.sendMessage(body("Bosses, particularly the ender dragon, are much harder."));
        player.sendMessage(body("If you want something more peaceful, live close to spawn."));
        player.sendMessage(body("Be careful. The world is on " + ChatColor.RED + "Hard++" + ChatColor.GREEN + " mode."));

        // General Info
        player.sendMessage("\n");
        player.sendMessage(header("General Info"));
        player.sendMessage(body("Use /rules to get a list of server rules."));
        player.sendMessage(body("Use /commands to get a list of custom server commands."));
        player.sendMessage(body("Use /chat to change how your chat is displayed."));
        player.sendMessage(body("Farms that abuse game mechanics (like tnt duping/breaking unbreakable blocks) have been disabled."));
        player.sendMessage(body("The server seed isn't shared, so tools like ChunkBase can't be used."));
        player.sendMessage(body("If you have any questions/want something to change about the server, ask the owner (DaGabeyWabey)."));
    }

    public void showCommandsCommand(Player player) {
        player.sendMessage("\n");
        player.sendMessage(header("Custom Commands"));
        player.sendMessage("\n");

        player.sendMessage(ChatColor.GOLD + "[Land claiming]");
        player.sendMessage(commandLine("/claim", "To claim the chunk you are currently on"));
        player.sendMessage(commandLine("/unclaim", "To unclaim the chunk you are currently on"));
        player.sendMessage(commandLine("/map", "To display claims around you as a map"));
        player.sendMessage(commandLine("/friend", "To manage who has access to your claims\n"));

        player.sendMessage(ChatColor.GOLD + "[Misc]");
        player.sendMessage(commandLine("/chat", "To manage how your chat is displayed"));
        player.sendMessage(commandLine("/mail", "To send mail to players (even when they are offline)"));
        player.sendMessage(commandLine("/rules", "To list server rules"));
        player.sendMessage(commandLine("/commands", "To list server commands"));
        player.sendMessage(commandLine("/info", "To get server info"));

        player.sendMessage(ChatColor.GREEN + "\nIf you would like extra commands to be added, ask the owner (DaGabeyWabey).");
    }

    public void rulesCommand(Player player) {
        player.sendMessage("\n");
        player.sendMessage(header("Rules"));
        player.sendMessage(body("No griefing/stealing/killing without reason, but pranking/trolling is fine."));
        player.sendMessage(body("No lag machines/farms that create extensive amounts of lag."));
        player.sendMessage(body("No item/block/tnt dupers."));
        player.sendMessage(body("Client-side mods/textures are fine, but they shouldn't give you an unfair advantage, i.e. no x-ray."));
        player.sendMessage(body("Don't claim chunks that are not yours (like when someone forgot to claim their land)."));
        player.sendMessage(body("No slurs."));
        player.sendMessage(ChatColor.RED + "If you see anyone violating these rules, report them to DaGabeyWabey.");
    }

    public String body(String text) {
        return ChatColor.GREEN + " - " + text;
    }

    public String commandLine(String command, String label) {
        return ChatColor.GREEN + command + " - " + label;
    }

    public String header(String text) {
        return ChatColor.GREEN + "" + ChatColor.BOLD + "[" + text + "]";
    }





}
