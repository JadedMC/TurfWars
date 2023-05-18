package net.jadedmc.turfwars;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Manages the configurable settings in the plugin.
 */
public class SettingsManager {
    private FileConfiguration config;
    private final File configFile;
    private FileConfiguration arenas;
    private final File arenasFile;

    /**
     * Loads or Creates configuration files.
     * @param plugin Instance of the plugin.
     */
    public SettingsManager(Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
        if(!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
    }

    /**
     * Get the arena configuration file.
     * @return Arena configuration file.
     */
    public FileConfiguration getArenas() {
        return arenas;
    }

    /**
     * Get the config.yml FileConfiguration.
     * @return config.yml FileConfiguration.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * THis updates the arenas file in case changes are made.
     */
    public void reloadArenas() {
        saveArenas();
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
    }

    /**
     * Update the configuration files.
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Allows us to save the arena config file after changes are made.
     */
    public void saveArenas() {
        try {
            arenas.save(arenasFile);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}