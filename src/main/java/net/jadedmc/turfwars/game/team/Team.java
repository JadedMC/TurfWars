package net.jadedmc.turfwars.game.team;

import com.cryptomorin.xseries.XBlock;
import net.jadedmc.turfwars.game.arena.ArenaTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {
    private final ArenaTeam arenaTeam;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> deadPlayers = new ArrayList<>();
    private final TeamColor teamColor;
    private Block bounds1;
    private Block bounds2;
    private final World world;

    /**
     * Creates a team.
     * @param arenaTeam Arena data for the team.
     */
    public Team(ArenaTeam arenaTeam, TeamColor teamColor, World world) {
        this.arenaTeam = arenaTeam;
        this.teamColor = teamColor;
        this.world = world;

        bounds1 = arenaTeam.bounds1(world);
        bounds2 = arenaTeam.bounds2(world);
    }

    /**
     * Add a list of players to the team.
     * @param players Players to add.
     */
    public void addPlayers(List<Player> players) {
        this.players.addAll(players);
    }

    public ArenaTeam getArenaTeam() {
        return arenaTeam;
    }

    public int getLines() {
        return Math.abs(bounds1.getZ() - bounds2.getZ());
    }

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Teleport all team members to the team spawn points.
     */
    public void spawn() {
        int spawnPoint = 0;

        for(Player player : players) {
            Location spawn = arenaTeam.spawns(world).get(spawnPoint);
            player.teleport(spawn);

            if(spawnPoint < arenaTeam.spawns(world).size() - 1) {
                spawnPoint++;
            }
            else {
                spawnPoint = 0;
            }
        }
    }

    /**
     * Respawn a player to a random team spawn point.
     * @param player Player to respawn.
     */
    public void respawn(Player player) {
        deadPlayers.remove(player);
        player.teleport(arenaTeam.randomSpawn(world));
        player.setHealth(20);

        for(Player viewer : Bukkit.getOnlinePlayers()) {
            if(viewer.equals(player)) {
                continue;
            }

            viewer.showPlayer(player);
        }

        player.spigot().setCollidesWithEntities(true);
    }

    public void decreaseBounds() {
        if(bounds2.getZ() > bounds1.getZ()) {
            bounds2 = bounds2.getRelative(0, 0, -1);
        }
        else {
            bounds2 = bounds2.getRelative(0,0,1);
        }
    }

    public void increaseBounds() {
        if(bounds2.getZ() > bounds1.getZ()) {
            bounds2 = bounds2.getRelative(0,0,1);
        }
        else {
            bounds2 = bounds2.getRelative(0,0,-1);
        }

        int minX = Math.min(bounds1.getX(), bounds2.getX());
        int maxX = Math.max(bounds1.getX(), bounds2.getX());

        // Loop through each block in the new row to change it's color.
        for(int i = minX; i <= maxX; i++) {
            Block block = bounds1.getWorld().getBlockAt(i, bounds1.getY(), bounds2.getZ());
            XBlock.setColor(block, teamColor.blockColor());

            // Set above wool blocks to air.
            for(int j = 1; j < 6; j++) {
                Block up = block.getRelative(BlockFace.UP, j);
                if(up.getType() == Material.WOOL) {
                    up.setType(Material.AIR);
                }
            }
        }
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * Remove a player from the team.
     * @param player Player to remove.
     */
    public void removePlayer(Player player) {
        getPlayers().remove(player);
        deadPlayers.remove(player);
    }

    public boolean isInBounds(Location location) {
        int minX = Math.min(bounds1.getX(), bounds2.getX());
        int maxX = Math.max(bounds1.getX(), bounds2.getX());
        int minZ = Math.min(bounds1.getZ(), bounds2.getZ());
        int maxZ = Math.max(bounds1.getZ(), bounds2.getZ());

        return (location.getBlockX() >= minX && location.getBlockX() <= maxX && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ);
    }

    public void killPlayer(Player player) {
        deadPlayers.add(player);

        for(Player viewer : Bukkit.getOnlinePlayers()) {
            if(viewer.equals(player)) {
                continue;
            }

            viewer.hidePlayer(player);
        }

        player.spigot().setCollidesWithEntities(false);
    }

    public List<Player> deadPlayers() {
        return deadPlayers;
    }
}