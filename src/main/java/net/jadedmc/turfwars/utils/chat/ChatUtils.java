package net.jadedmc.turfwars.utils.chat;

import net.jadedmc.turfwars.TurfWars;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Some methods to make sending chat messages easier.
 */
public class ChatUtils {
    private static TurfWars plugin;
    private final static int CENTER_PX = 154;

    /**
     * Initialize the ChatUtils class.
     * @param pl Instance of the plugin.
     */
    public ChatUtils(TurfWars pl) {
        plugin = pl;
    }

    /**
     * A quick way to send a CommandSender a colored message.
     * @param sender CommandSender to send message to.
     * @param message The message being sent.
     */
    public static void chat(CommandSender sender, String message) {
        plugin.adventure().sender(sender).sendMessage(translate(message));
    }

    /**
     * Sender a centered chat message to a CommandSender.
     * @param sender Command Sender
     * @param message Message
     */
    public static void centeredChat(CommandSender sender, String message) {
        String filteredMessage = MiniMessage.miniMessage().stripTags(message);

        if(filteredMessage == null || filteredMessage.equals("")) sender.sendMessage("");

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : filteredMessage.toCharArray()) {
            if(c == '§') {
                previousCode = true;
            }
            else if(previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }
            else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        ChatUtils.chat(sender, sb.toString() + message);
    }

    /**
     * Translates a String to a colorful String using methods in the BungeeCord API.
     * @param message Message to translate.
     * @return Translated Message.
     */
    public static Component translate(String message) {
        return MiniMessage.miniMessage().deserialize(replaceLegacy(message));
    }

    /**
     * Replaces legacy color codes with their MiniMessage equivalent.
     * @param message Message to replace codes in.
     * @return Message with codes replaced.
     */
    public static String replaceLegacy(String message) {
        return message.replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<u>")
                .replace("&o", "<i>")
                .replace("&r", "<reset>");
    }

    public static String replaceChatColor(ChatColor chatColor) {
        switch (chatColor) {
            case BLACK -> {
                return "<black>";
            }
            case DARK_BLUE -> {
                return "<dark_blue>";
            }
            case DARK_GREEN -> {
                return "<dark_green>";
            }
            case DARK_AQUA -> {
                return "<dark_aqua>";
            }
            case DARK_RED -> {
                return "<dark_red>";
            }
            case DARK_PURPLE -> {
                return "<dark_purple>";
            }
            case GOLD -> {
                return "<gold>";
            }
            case GRAY -> {
                return "<gray>";
            }
            case DARK_GRAY -> {
                return "<dark_gray>";
            }
            case BLUE -> {
                return "<blue>";
            }
            case GREEN -> {
                return "<green>";
            }
            case AQUA -> {
                return "<aqua>";
            }
            case RED -> {
                return "<red>";
            }
            case LIGHT_PURPLE -> {
                return "<light_purple>";
            }
            case YELLOW -> {
                return "<yellow>";
            }
            case WHITE -> {
                return "<white>";
            }
            default -> {
                return "<reset>";
            }
        }
    }
}