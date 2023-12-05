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
package net.jadedmc.turfwars.utils;

import net.jadedmc.turfwars.TurfWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A collection of utilities to help with managing locations.
 */
public class LocationUtils {

    /**
     * Get a location from a configuration section.
     * @param config ConfigurationSection for the location.
     * @return Location object stored.
     */
    public static Location fromConfig(ConfigurationSection config) {
        //World world = Bukkit.getWorld(config.getString("World"));
        World world = Bukkit.getWorlds().get(0);
        double x = config.getDouble("X");
        double y = config.getDouble("Y");
        double z = config.getDouble("Z");
        float yaw = (float) config.getDouble("Yaw");
        float pitch = (float) config.getDouble("Pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Get the spawn Location from the Config
     * @return Spawn Location
     */
    public static Location getSpawn(TurfWars plugin) {
        String world = plugin.getSettingsManager().getConfig().getString("Spawn.World");
        double x = plugin.getSettingsManager().getConfig().getDouble("Spawn.X");
        double y = plugin.getSettingsManager().getConfig().getDouble("Spawn.Y");
        double z = plugin.getSettingsManager().getConfig().getDouble("Spawn.Z");
        float pitch = (float) plugin.getSettingsManager().getConfig().getDouble("Spawn.Pitch");
        float yaw = (float) plugin.getSettingsManager().getConfig().getDouble("Spawn.Yaw");

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    /**
     * Gets the equivalent location of a given location, in a new world.
     * @param world World to get new location in.
     * @param location Location to get equivalent of.
     * @return New location.
     */
    public static Location replaceWorld(World world, Location location) {
        return new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Set the server spawn to the current location.
     * @param loc Location
     */
    public static void setSpawn(TurfWars plugin, Location loc) {
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float pitch = loc.getPitch();
        float yaw = loc.getYaw();

        plugin.getSettingsManager().getConfig().set("Spawn.World", world);
        plugin.getSettingsManager().getConfig().set("Spawn.X", x);
        plugin.getSettingsManager().getConfig().set("Spawn.Y", y);
        plugin.getSettingsManager().getConfig().set("Spawn.Z", z);
        plugin.getSettingsManager().getConfig().set("Spawn.Pitch", pitch);
        plugin.getSettingsManager().getConfig().set("Spawn.Yaw", yaw);
        plugin.getSettingsManager().getConfig().set("Spawn.Set", true);

        plugin.getSettingsManager().reloadConfig();
    }
}