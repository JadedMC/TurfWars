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

import net.jadedmc.turfwars.utils.LocationUtils;
import net.jadedmc.turfwars.utils.blocks.BlockUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ArenaTeam {
    private final List<Location> spawns = new ArrayList<>();
    private final ConfigurationSection config;
    private Block bounds1;
    private Block bounds2;

    public ArenaTeam(ConfigurationSection config) {
        this.config = config;

        // Load the blocks of the team.
        bounds1 = BlockUtils.fromConfig(config.getConfigurationSection("bounds1"));
        bounds2 = BlockUtils.fromConfig(config.getConfigurationSection("bounds2"));

        // Load the spawns of the team.
        ConfigurationSection spawnsSection = config.getConfigurationSection("spawns");
        for(String spawnID : spawnsSection.getKeys(false)) {
            ConfigurationSection spawn = spawnsSection.getConfigurationSection(spawnID);
            spawns.add(LocationUtils.fromConfig(spawn));
        }
    }

    public Block bounds1(World world) {
        return world.getBlockAt(LocationUtils.replaceWorld(world, bounds1.getLocation()));
    }

    public Block bounds2(World world) {
        return world.getBlockAt(LocationUtils.replaceWorld(world, bounds2.getLocation()));
    }

    public Location randomSpawn(World world) {
        List<Location> worldSpawns = new ArrayList<>();

        for(Location spawn : spawns) {
            worldSpawns.add(LocationUtils.replaceWorld(world, spawn));
        }

        Collections.shuffle(worldSpawns);

        return worldSpawns.get(0);
    }

    public List<Location> spawns(World world) {
        List<Location> worldSpawns = new ArrayList<>();

        for(Location spawn : spawns) {
            worldSpawns.add(LocationUtils.replaceWorld(world, spawn));
        }

        return worldSpawns;
    }
}