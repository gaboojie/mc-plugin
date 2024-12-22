package org.gabooj.chat;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatCommands implements CommandExecutor {

    private final Server server;
    private final JavaPlugin plugin;

    public ChatCommands(Server server, JavaPlugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    public void handleMailCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.GREEN + "Mail usage:\n1. '/mail read' - To read your mail\n2. '/mail send <name> <msg>' - To send a message to <name>\n3. '/mail clear' - To clear your mail");
            return;
        }
        String name = player.getName();
        if (args[0].equalsIgnoreCase("read")) {
            List<String> mail = ChatManager.playerMail.getOrDefault(name, new ArrayList<>());
            if (mail.isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "You do not have any mail.");
            } else {
                StringBuilder to_send = new StringBuilder(ChatColor.GOLD + "Mail:\n");
                for (int i = 0; i < mail.size(); i++) {
                    to_send.append((i + 1)).append(". ").append(mail.get(i)).append("\n");
                }
                player.sendMessage(to_send.toString());
            }
        } else if (args[0].equalsIgnoreCase("send")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.GOLD + "To send mail, use '/mail send <name> <msg>'.");
            } else {
                String message = player.getName() + ":";
                for (int i = 2; i < args.length; i++) {
                    message += " " + args[i];
                }
                if (message.contains(";")) {
                    player.sendMessage(ChatColor.RED + "Error: You cannot use a semicolon in your message (because semicolons are used in saving the mail).");
                    return;
                }
                player.sendMessage(ChatColor.GOLD + "Sent message to " + args[1] + ".");
                Player playerToMessage = getPlayerByName(args[1]);
                if (playerToMessage != null) {
                    playerToMessage.sendMessage(ChatColor.GOLD + player.getName() + " has sent you mail! Use '/mail read' to read your mail.");
                }
                List<String> messages = ChatManager.playerMail.getOrDefault(playerToMessage.getName(), new ArrayList<>());
                messages.add(message);
                ChatManager.playerMail.put(playerToMessage.getName(), messages);
            }
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (ChatManager.playerMail.containsKey(player.getName())) {
                ChatManager.playerMail.remove(player.getName());
            }
            player.sendMessage(ChatColor.GOLD + "You have cleared all your mail.");
        } else {
            player.sendMessage(ChatColor.RED + "Error: The mail command must be used like '/mail read', '/mail send <name> <msg>', or '/mail clear'.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Handle terminal executing command
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to issue this command!");
            return true;
        }
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("mail")) {
            handleMailCommand(player, args);
            return true;
        }
        if (!label.equalsIgnoreCase("chat")) return false;

        // Handle simple /chat command
        if (args.length == 0) {
            String message = ChatColor.BLUE + """
                    The /chat command is used to change how your chat is displayed. Example usages:
                    
                    /chat name set <nickname>
                    /chat name color red
                    /chat name format bold
                    /chat prefix set <prefix>
                    /chat prefix color blue
                    /chat prefix format reset
                    /chat message color light purple
                    /chat message format magic
                    """;
            sender.sendMessage(message);
            return true;
        }

        if (args[0].equalsIgnoreCase("name")) {
            if (args.length <= 2) {
                sender.sendMessage(ChatColor.BLUE + """
                        Use /chat name set <nickname>, /chat name color <color>, /chat name format <format>
                        
                        Your nickname can be anything, but is purely for display purposes.
                        The color must be one of the following: black, dark blue, dark green, dark aqua, dark red, dark purple, gold gray, dark gray, blue, green, aqua, red, light purple, yellow, and white.
                        The format must be one of the following: reset (for no format), bold, italic, underline, strikethrough, or obfuscated.
                        """);
            } else {
                if (args[1].equalsIgnoreCase("set")) {
                    // /chat name set <nickname>

                    // Handle nickname being nothing
                    if (args[2].isEmpty()) {
                        sender.sendMessage(ChatColor.RED + "Your nickname must be something!");
                        return true;
                    }

                    // Update nickname
                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    StringBuilder nickname = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        nickname.append(args[i]).append(" ");
                    }

                    settings.nickname = nickname.substring(0, nickname.length()-1);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your nickname has been updated!");
                } else if (args[1].equalsIgnoreCase("color")) {
                    // /chat name color <color>

                    // Get name color
                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    StringBuilder color = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        color.append(args[i]).append(" ");
                    }
                    String input_color = color.substring(0, color.length()-1);

                    // Make sure color is not empty
                    if (!isValidColorName(input_color)) {
                        sender.sendMessage(ChatColor.RED + "Error: You didn't provide a valid name color!");
                        return true;
                    }

                    // Update color
                    settings.name_color = ChatManager.getChatColorByName(input_color);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your color has been updated!");
                } else if (args[1].equalsIgnoreCase("format")) {
                    // /chat name format <color>

                    String input_color = args[2];
                    if (!isValidFormatName(input_color)) {
                        sender.sendMessage(ChatColor.RED + "Error: You didn't provide a valid name format!");
                        return true;
                    }

                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    settings.name_format = ChatManager.getChatFormatByName(input_color);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your format has been updated!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You must use one of the following:" +
                            " /chat name set <nickname>, " +
                            " /chat name color <color>, " +
                            " /chat name format <format>");
                }
            }
        } else if (args[0].equalsIgnoreCase("prefix")) {
            if (args.length <= 2) {
                sender.sendMessage(ChatColor.BLUE + """
                        Use /chat prefix set <nickname>, /chat prefix color <color>, /chat prefix format <format>
                        
                        Your prefix can be anything, but is purely for display purposes and will come before your name when you say anything in chat.
                        The color must be one of the following: black, dark blue, dark green, dark aqua, dark red, dark purple, gold gray, dark gray, blue, green, aqua, red, light purple, yellow, and white.
                        The format must be one of the following: reset (for no format), bold, italic, underline, strikethrough, or obfuscated.
                        """);
            } else {
                if (args[1].equalsIgnoreCase("set")) {
                    // /chat prefix set <nickname>

                    // Update prefix
                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    StringBuilder prefix = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        prefix.append(args[i]).append(" ");
                    }

                    settings.prefix = prefix.substring(0, prefix.length()-1);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your prefix has been updated!");
                } else if (args[1].equalsIgnoreCase("color")) {
                    // /chat prefix color <color>

                    // Get name color
                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    StringBuilder color = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        color.append(args[i]).append(" ");
                    }
                    String input_color = color.substring(0, color.length()-1);

                    // Make sure color is not empty
                    if (!isValidColorName(input_color)) {
                        sender.sendMessage(ChatColor.RED + "Error: You didn't provide a valid prefix color!");
                        return true;
                    }

                    // Update color
                    settings.prefix_color = ChatManager.getChatColorByName(input_color);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your prefix color has been updated!");
                } else if (args[1].equalsIgnoreCase("format")) {
                    // /chat prefix format <color>

                    String input_color = args[2];
                    if (!isValidFormatName(input_color)) {
                        sender.sendMessage(ChatColor.RED + "Error: You didn't provide a valid prefix format!");
                        return true;
                    }

                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    settings.prefix_format = ChatManager.getChatFormatByName(input_color);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your format has been updated!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You must use one of the following:" +
                            " /chat prefix set <prefix>, " +
                            " /chat prefix color <color>, " +
                            " /chat prefix format <format>");
                }
            }
        } else if (args[0].equalsIgnoreCase("message")) {
            if (args.length <= 2) {
                sender.sendMessage(ChatColor.BLUE + """
                        Use /chat message color <color>, /chat message format <format>
                        
                        The color must be one of the following: black, dark blue, dark green, dark aqua, dark red, dark purple, gold gray, dark gray, blue, green, aqua, red, light purple, yellow, and white.
                        The format must be one of the following: reset (for no format), bold, italic, underline, strikethrough, or obfuscated.
                        """);
            } else {
                if (args[1].equalsIgnoreCase("color")) {
                    // /chat message color <color>

                    // Get message color
                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    StringBuilder color = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        color.append(args[i]).append(" ");
                    }
                    String input_color = color.substring(0, color.length()-1);

                    // Make sure color is valid
                    if (!isValidColorName(input_color)) {
                        sender.sendMessage(ChatColor.RED + "Error: You didn't provide a valid message color!");
                        return true;
                    }

                    // Update color
                    settings.message_color = ChatManager.getChatColorByName(input_color);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your message color has been updated!");
                } else if (args[1].equalsIgnoreCase("format")) {
                    // /chat message format <color>

                    String input_color = args[2];
                    if (!isValidFormatName(input_color)) {
                        sender.sendMessage(ChatColor.RED + "Error: You didn't provide a valid message format!");
                        return true;
                    }

                    PlayerChatSettings settings = ChatManager.getChatSettingsForPlayer(player);
                    settings.message_format = ChatManager.getChatFormatByName(input_color);
                    ChatManager.chatSettings.put(player.getName(), settings);
                    save();
                    sender.sendMessage(ChatColor.GREEN + "Your message format has been updated!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You must use one of the following:" +
                            " /chat message set <prefix>, " +
                            " /chat message color <color>, " +
                            " /chat message format <format>");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error: The /chat command can only be used with 'name', 'prefix', or 'message' as its first argument.");
        }

        return true;
    }

    public boolean isValidColorName(String name) {
        return ChatManager.colorNameToChatColor.containsKey(name.toUpperCase());
    }

    public boolean isValidFormatName(String name) {
        return ChatManager.formatNameToChatColor.containsKey(name.toUpperCase());
    }

    public void save() {
        ChatManagerIO.writeData(ChatManager.chatSettings, plugin, server);
    }

    public Player getPlayerByName(String name) {
        for (Player player : server.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

}
