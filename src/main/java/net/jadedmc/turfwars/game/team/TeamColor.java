package net.jadedmc.turfwars.game.team;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/**
 * Represents a color option for a team.
 */
public enum TeamColor {
    DARK_RED(ChatColor.DARK_RED, Color.MAROON, DyeColor.RED, "Dark Red"),
    RED(ChatColor.RED, Color.RED, DyeColor.RED, "Red"),
    ORANGE(ChatColor.GOLD, Color.ORANGE, DyeColor.ORANGE, "Orange"),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, DyeColor.YELLOW, "Yellow"),
    GREEN(ChatColor.GREEN, Color.LIME, DyeColor.LIME, "Lime"),
    DARK_GREEN(ChatColor.DARK_GREEN, Color.GREEN, DyeColor.GREEN, "Green"),
    AQUA(ChatColor.AQUA, Color.AQUA, DyeColor.LIGHT_BLUE, "Blue"),
    BLUE(ChatColor.BLUE, Color.BLUE, DyeColor.BLUE, "Blue"),
    DARK_BLUE(ChatColor.DARK_BLUE, Color.NAVY, DyeColor.BLUE, "Dark Blue"),
    PURPLE(ChatColor.DARK_PURPLE, Color.PURPLE, DyeColor.PURPLE, "Purple"),
    PINK(ChatColor.LIGHT_PURPLE, Color.FUCHSIA, DyeColor.PINK, "Pink"),
    BLACK(ChatColor.BLACK, Color.BLACK, DyeColor.BLACK, "Black"),
    WHITE(ChatColor.WHITE, Color.WHITE, DyeColor.WHITE, "White"),
    GRAY(ChatColor.GRAY, Color.SILVER, DyeColor.SILVER, "Silver"),
    DARK_GRAY(ChatColor.DARK_GRAY, Color.GRAY, DyeColor.GRAY, "Gray");

    private final ChatColor chatColor;
    private final Color leatherColor;
    private final DyeColor blockColor;
    private final String name;

    /**
     * Creates the team color.
     * @param chatColor Chat color of the team.
     * @param leatherColor Leather color of the team.
     * @param blockColor Block color of the team.
     * @param name The name of the team.
     */
    TeamColor(ChatColor chatColor, Color leatherColor, DyeColor blockColor, String name) {
        this.chatColor = chatColor;
        this.leatherColor = leatherColor;
        this.blockColor = blockColor;
        this.name = name;
    }

    /**
     * Gets the Block color of the team.
     * @return Color of the blocks that the team uses.
     */
    public DyeColor blockColor() {
        return blockColor;
    }

    /**
     * Gets the chat color of a team.
     * @return Chat color of the team.
     */
    public ChatColor chatColor() {
        return chatColor;
    }

    /**
     * Gets the leather color of a team.
     * @return Leather color of the team.
     */
    public Color leatherColor() {
        return leatherColor;
    }

    public String getName() {
        return name;
    }
}