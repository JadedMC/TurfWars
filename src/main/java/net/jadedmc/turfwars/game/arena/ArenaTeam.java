package net.jadedmc.turfwars.game.arena;

import com.cryptomorin.xseries.XBlock;
import net.jadedmc.turfwars.utils.LocationUtils;
import net.jadedmc.turfwars.utils.blocks.BlockUtils;
import org.bukkit.Location;
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

    public Block getBounds1() {
        return bounds1;
    }

    public Block getBounds2() {
        return bounds2;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public boolean isInBounds(Location location) {
        int minX = Math.min(bounds1.getX(), bounds2.getX());
        int maxX = Math.max(bounds1.getX(), bounds2.getX());
        int minZ = Math.min(bounds1.getZ(), bounds2.getZ());
        int maxZ = Math.max(bounds1.getZ(), bounds2.getZ());

        return (location.getBlockX() >= minX && location.getBlockX() <= maxX && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ);
    }

    public void resetBounds() {
        bounds1 = BlockUtils.fromConfig(config.getConfigurationSection("bounds1"));
        bounds2 = BlockUtils.fromConfig(config.getConfigurationSection("bounds2"));
    }

    public void setBounds1(Block bounds1) {
        this.bounds1 = bounds1;
    }

    public void setBounds2(Block bounds2) {
        this.bounds2 = bounds2;
    }

    public Location getRandomSpawn() {
        List<Location> spawns = new ArrayList<>(getSpawns());
        Collections.shuffle(spawns);

        return spawns.get(0);
    }
}