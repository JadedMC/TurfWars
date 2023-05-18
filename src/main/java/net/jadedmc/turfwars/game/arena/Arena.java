package net.jadedmc.turfwars.game.arena;

import net.jadedmc.turfwars.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class Arena {
    private final String id;
    private final String name;
    private final ArenaTeam team1;
    private final ArenaTeam team2;
    private final Location waitingArea;
    private final String author;

    public Arena(ConfigurationSection config) {
        this.id = config.getName();
        this.name = config.getString("Name");

        this.team1 = new ArenaTeam(config.getConfigurationSection("teams.1"));
        this.team2 = new ArenaTeam(config.getConfigurationSection("teams.2"));
        this.waitingArea = LocationUtils.fromConfig(config.getConfigurationSection("waitingArea"));

        if(!config.isSet("Author")) {
            author = "JadedMC";
        }
        else {
            author = config.getString("Author");
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArenaTeam getTeam1() {
        return team1;
    }

    public ArenaTeam getTeam2() {
        return team2;
    }

    public Location getWaitingArea() {
        return waitingArea;
    }

    public void reset() {
        team1.resetBounds();
        team2.resetBounds();
    }
}