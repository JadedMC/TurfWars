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
package net.jadedmc.turfwars.game.arena;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.file.ArenaFile;
import net.jadedmc.turfwars.utils.FileUtils;
import net.jadedmc.turfwars.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

/**
 * Represents an area in which a game is played.
 */
public class Arena {
    private final String id;
    private final String builders;
    private final String name;
    private final ArenaFile arenaFile;
    private final File configFile;
    private final Location waitingArea;
    private final Location spectatorSpawn;
    private final ArenaTeam team1;
    private final ArenaTeam team2;


    /**
     * Creates the arena.
     * @param plugin Instance of the plugin.
     * @param configFile Configuration file for the arena.
     */
    public Arena(final TurfWars plugin, final File configFile) {
        this.configFile = configFile;
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        id = FileUtils.removeFileExtension(configFile.getName(), true);
        arenaFile = plugin.arenaManager().arenaFileManager().loadArenaFile(id);

        this.name = config.getString("name");


        if(config.isSet("builders")) {
            this.builders = config.getString("builders");
        }
        else {
            this.builders = "JadedMC";
        }

        waitingArea = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("waitingArea")));
        spectatorSpawn = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("spectatorSpawn")));

        this.team1 = new ArenaTeam(config.getConfigurationSection("teams.1"));
        this.team2 = new ArenaTeam(config.getConfigurationSection("teams.2"));
    }

    public ArenaFile arenaFile() {
        return arenaFile;
    }

    public String builders() {
        return builders;
    }

    public File configFile() {
        return configFile;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    /**
     * Get the spectator area of the arena in a specific world.
     * @param world World to get spectator spawn of.
     * @return Spectator spawn location.
     */
    public Location spectatorSpawn(World world) {
        return LocationUtils.replaceWorld(world, spectatorSpawn);
    }

    public ArenaTeam team1() {
        return team1;
    }

    public ArenaTeam team2() {
        return team2;
    }

    /**
     * Get the waiting area spawn.
     * Returns null if it doesn't have one.
     * @param world World to get the waiting area spawn of.
     * @return The arena's waiting area spawn.
     */
    public Location waitingArea(World world) {
        return LocationUtils.replaceWorld(world, waitingArea);
    }
}