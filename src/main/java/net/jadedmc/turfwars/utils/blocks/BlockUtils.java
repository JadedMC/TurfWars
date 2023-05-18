package net.jadedmc.turfwars.utils.blocks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A collection of utilities to help with managing blocks.
 */
public class BlockUtils {

    /**
     * Get a block based on a configured location.
     * @param config ConfigurationSection of the block.
     * @return Block at that location.
     */
    public static Block fromConfig(ConfigurationSection config) {
        World world = Bukkit.getWorld(config.getString("World"));
        int x = config.getInt("X");
        int y = config.getInt("Y");
        int z = config.getInt("Z");

        return world.getBlockAt(x, y, z);
    }
}