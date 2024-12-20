package org.gabooj.chat;

import org.bukkit.ChatColor;

public class PlayerChatSettings {

    public String prefix;
    public String nickname;
    public ChatColor prefix_color, name_color, message_color;
    public ChatColor prefix_format, name_format, message_format;

    public PlayerChatSettings(String prefix, String nickname, ChatColor prefix_color, ChatColor name_color, ChatColor message_color, ChatColor prefix_format, ChatColor name_format, ChatColor message_format) {
        this.prefix = prefix;
        this.nickname = nickname;
        this.prefix_color = prefix_color;
        this.name_color = name_color;
        this.message_color = message_color;
        this.prefix_format = prefix_format;
        this.name_format = name_format;
        this.message_format = message_format;
    }
}
