/*
 * This file is part of TurfWars, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.turfwars.game.team;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;

/**
 * Represents a color option for a team.
 */
public enum TeamColor {
    DARK_RED(ChatColor.DARK_RED, DyeColor.RED, "Dark Red"),
    RED(ChatColor.RED, DyeColor.RED, "Red"),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, "Orange"),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "Yellow"),
    GREEN(ChatColor.GREEN, DyeColor.LIME, "Lime"),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, "Green"),
    AQUA(ChatColor.AQUA, DyeColor.LIGHT_BLUE, "Blue"),
    BLUE(ChatColor.BLUE, DyeColor.BLUE, "Blue"),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, "Dark Blue"),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, "Purple"),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, "Pink"),
    BLACK(ChatColor.BLACK, DyeColor.BLACK, "Black"),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, "White"),
    GRAY(ChatColor.GRAY, DyeColor.SILVER, "Silver"),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, "Gray");

    private final ChatColor chatColor;
    private final DyeColor blockColor;
    private final String name;

    /**
     * Creates the team color.
     * @param chatColor Chat color of the team.
     * @param blockColor Block color of the team.
     * @param name The name of the team.
     */
    TeamColor(ChatColor chatColor, DyeColor blockColor, String name) {
        this.chatColor = chatColor;
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

    public String getName() {
        return name;
    }
}