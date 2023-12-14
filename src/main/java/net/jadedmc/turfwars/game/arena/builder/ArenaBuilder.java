package net.jadedmc.turfwars.game.arena.builder;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
    private Location spectatorSpawn;
    private String builders;
    private boolean editMode = false;

    public ArenaBuilder(TurfWars plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        builders = "JadedMC";
    }

    public ArenaBuilder(TurfWars plugin, Arena arena) {
        this.plugin = plugin;
        this.id = arena.id();
        this.builders = arena.builders();
        this.waitingArea = arena.waitingArea(Bukkit.getWorld(id));
        this.spectatorSpawn = arena.spectatorSpawn(Bukkit.getWorld(id));
        this.name = arena.name();

        team1Bounds1 = arena.team1().bounds1(Bukkit.getWorld(id));
        team1Bounds2 = arena.team1().bounds2(Bukkit.getWorld(id));
        team2Bounds1 = arena.team2().bounds1(Bukkit.getWorld(id));
        team2Bounds2 = arena.team2().bounds2(Bukkit.getWorld(id));

        team1Spawns.addAll(arena.team1().spawns(Bukkit.getWorld(id)));
        team2Spawns.addAll(arena.team2().spawns(Bukkit.getWorld(id)));

        editMode = true;
    }

    /**
     * Get if the arena builder is in edit mode.
     * @return If in edit mode.
     */
    public boolean editMode() {
        return editMode;
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

    /**
     * Sets the location where spectators should spawn.
     * @param spectatorSpawn Spawn point for spectators.
     */
    public void spectatorSpawn(Location spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
    }

    public void save() {
        try {
            File file = new File(plugin.getDataFolder(), "/arenas/" + id + ".yml");
            if(file.exists()) {
                file.delete();
            }

            file.createNewFile();

            FileConfiguration arenaSection = YamlConfiguration.loadConfiguration(file);

            arenaSection.set("name", name);

            // Save waiting area.
            {
                ConfigurationSection waitingAreaSection = arenaSection.createSection("waitingArea");
                waitingAreaSection.set("World", Bukkit.getWorlds().get(0).getName());
                waitingAreaSection.set("X", waitingArea.getX());
                waitingAreaSection.set("Y", waitingArea.getY());
                waitingAreaSection.set("Z", waitingArea.getZ());
                waitingAreaSection.set("Yaw", (double) waitingArea.getYaw());
                waitingAreaSection.set("Pitch", (double) waitingArea.getPitch());
            }

            // Spectator Spawn Location
            // TODO: Make it not the waiting area
            {
                ConfigurationSection waitingSection = arenaSection.createSection("spectatorSpawn");
                waitingSection.set("world", Bukkit.getWorlds().get(0).getName());
                waitingSection.set("x", waitingArea.getX());
                waitingSection.set("y", waitingArea.getY());
                waitingSection.set("z", waitingArea.getZ());
                waitingSection.set("yaw", waitingArea.getYaw());
                waitingSection.set("pitch", waitingArea.getPitch());
            }

            // Save Team 1
            {
                ConfigurationSection team = arenaSection.createSection("teams.1");

                ConfigurationSection bounds1 = team.createSection("bounds1");
                bounds1.set("World", Bukkit.getWorlds().get(0).getName());
                bounds1.set("X", team1Bounds1.getX());
                bounds1.set("Y", team1Bounds1.getY());
                bounds1.set("Z", team1Bounds1.getZ());

                ConfigurationSection bounds2 = team.createSection("bounds2");
                bounds2.set("World", Bukkit.getWorlds().get(0).getName());
                bounds2.set("X", team1Bounds2.getX());
                bounds2.set("Y", team1Bounds2.getY());
                bounds2.set("Z", team1Bounds2.getZ());

                ConfigurationSection spawns = team.createSection("spawns");
                int count = 1;
                for(Location location : team1Spawns) {
                    ConfigurationSection spawn = spawns.createSection(count + "");
                    spawn.set("World", Bukkit.getWorlds().get(0).getName());
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
                bounds1.set("World", Bukkit.getWorlds().get(0).getName());
                bounds1.set("X", team2Bounds1.getX());
                bounds1.set("Y", team2Bounds1.getY());
                bounds1.set("Z", team2Bounds1.getZ());

                ConfigurationSection bounds2 = team.createSection("bounds2");
                bounds2.set("World", Bukkit.getWorlds().get(0).getName());
                bounds2.set("X", team2Bounds2.getX());
                bounds2.set("Y", team2Bounds2.getY());
                bounds2.set("Z", team2Bounds2.getZ());

                ConfigurationSection spawns = team.createSection("spawns");
                int count = 1;
                for(Location location : team2Spawns) {
                    ConfigurationSection spawn = spawns.createSection(count + "");
                    spawn.set("World", Bukkit.getWorlds().get(0).getName());
                    spawn.set("X", location.getX());
                    spawn.set("Y", location.getY());
                    spawn.set("Z", location.getZ());
                    spawn.set("Yaw", (double) location.getYaw());
                    spawn.set("Pitch", (double) location.getPitch());

                    count++;
                }
            }

            arenaSection.save(file);

            // Loads arena into memory.
            plugin.arenaManager().loadArena(id);

            // Clears the current arena builder.
            //plugin.arenaManager().arenaBuilder(null);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}