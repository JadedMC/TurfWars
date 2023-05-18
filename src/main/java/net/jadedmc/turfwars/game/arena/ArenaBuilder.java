package net.jadedmc.turfwars.game.arena;

import net.jadedmc.turfwars.TurfWars;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;

public class ArenaBuilder {
    private final TurfWars plugin;
    private final String id;
    private String name;

    private final Collection<Location> team1Spawns = new ArrayList<>();
    private final Collection<Location> team2Spawns = new ArrayList<>();

    private Block team1Bounds1;
    private Block team1Bounds2;
    private Block team2Bounds1;
    private Block team2Bounds2;
    private Location waitingArea;

    public ArenaBuilder(TurfWars plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTeam1Spawn(Location location) {
        team1Spawns.add(location);
    }

    public void addTeam2Spawn(Location location) {
        team2Spawns.add(location);
    }

    public void setTeam1Bounds1(Block block) {
        team1Bounds1 = block;
    }

    public void setTeam1Bounds2(Block block) {
        team1Bounds2 = block;
    }

    public void setTeam2Bounds1(Block block) {
        team2Bounds1 = block;
    }

    public void setTeam2Bounds2(Block block) {
        team2Bounds2 = block;
    }

    public void setWaitingArea(Location waitingArea) {
        this.waitingArea = waitingArea;
    }

    public void save() {
        // Creates a spot for the new arena.
        ConfigurationSection arenaSection;
        if(plugin.getSettingsManager().getArenas().getConfigurationSection("Arenas") == null) {
            arenaSection = plugin.getSettingsManager().getArenas().createSection("Arenas").createSection(id);
        }
        else {
            arenaSection = plugin.getSettingsManager().getArenas().getConfigurationSection("Arenas").createSection(id);
        }

        arenaSection.set("Name", name);

        // Save waiting area.
        {
            ConfigurationSection waitingAreaSection = arenaSection.createSection("waitingArea");
            waitingAreaSection.set("World", waitingArea.getWorld().getName());
            waitingAreaSection.set("X", waitingArea.getX());
            waitingAreaSection.set("Y", waitingArea.getY());
            waitingAreaSection.set("Z", waitingArea.getZ());
            waitingAreaSection.set("Yaw", (double) waitingArea.getYaw());
            waitingAreaSection.set("Pitch", (double) waitingArea.getPitch());
        }

        // Save Team 1
        {
            ConfigurationSection team = arenaSection.createSection("teams.1");

            ConfigurationSection bounds1 = team.createSection("bounds1");
            bounds1.set("World", waitingArea.getWorld().getName());
            bounds1.set("X", team1Bounds1.getX());
            bounds1.set("Y", team1Bounds1.getY());
            bounds1.set("Z", team1Bounds1.getZ());

            ConfigurationSection bounds2 = team.createSection("bounds2");
            bounds2.set("World", waitingArea.getWorld().getName());
            bounds2.set("X", team1Bounds2.getX());
            bounds2.set("Y", team1Bounds2.getY());
            bounds2.set("Z", team1Bounds2.getZ());

            ConfigurationSection spawns = team.createSection("spawns");
            int count = 1;
            for(Location location : team1Spawns) {
                ConfigurationSection spawn = spawns.createSection(count + "");
                spawn.set("World", waitingArea.getWorld().getName());
                spawn.set("X", location.getX());
                spawn.set("Y", location.getY());
                spawn.set("Z", location.getZ());
                spawn.set("Yaw", (double) location.getYaw());
                spawn.set("Pitch", (double) location.getPitch());

                count++;
            }
        }

        // Save Team 2
        {
            ConfigurationSection team = arenaSection.createSection("teams.2");

            ConfigurationSection bounds1 = team.createSection("bounds1");
            bounds1.set("World", waitingArea.getWorld().getName());
            bounds1.set("X", team2Bounds1.getX());
            bounds1.set("Y", team2Bounds1.getY());
            bounds1.set("Z", team2Bounds1.getZ());

            ConfigurationSection bounds2 = team.createSection("bounds2");
            bounds2.set("World", waitingArea.getWorld().getName());
            bounds2.set("X", team2Bounds2.getX());
            bounds2.set("Y", team2Bounds2.getY());
            bounds2.set("Z", team2Bounds2.getZ());

            ConfigurationSection spawns = team.createSection("spawns");
            int count = 1;
            for(Location location : team2Spawns) {
                ConfigurationSection spawn = spawns.createSection(count + "");
                spawn.set("World", waitingArea.getWorld().getName());
                spawn.set("X", location.getX());
                spawn.set("Y", location.getY());
                spawn.set("Z", location.getZ());
                spawn.set("Yaw", (double) location.getYaw());
                spawn.set("Pitch", (double) location.getPitch());

                count++;
            }
        }

        // Saves and updates arenas.yml
        plugin.getSettingsManager().reloadArenas();

        // Loads arena into memory.
        plugin.getArenaManager().loadArena(id);

        // Clears the current arena builder.
        plugin.getArenaManager().setArenaBuilder(null);
    }
}