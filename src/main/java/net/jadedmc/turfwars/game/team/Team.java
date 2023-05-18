package net.jadedmc.turfwars.game.team;

import com.cryptomorin.xseries.XBlock;
import net.jadedmc.turfwars.game.arena.ArenaTeam;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {
    private final ArenaTeam arenaTeam;
    private final List<Player> players = new ArrayList<>();
    private final TeamColor teamColor;

    /**
     * Creates a team.
     * @param arenaTeam Arena data for the team.
     */
    public Team(ArenaTeam arenaTeam, TeamColor teamColor) {
        this.arenaTeam = arenaTeam;
        this.teamColor = teamColor;
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
        return Math.abs(arenaTeam.getBounds1().getZ() - arenaTeam.getBounds2().getZ());
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
            Location spawn = arenaTeam.getSpawns().get(spawnPoint);
            player.teleport(spawn);

            if(spawnPoint < arenaTeam.getSpawns().size() - 1) {
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
        List<Location> spawns = new ArrayList<>(arenaTeam.getSpawns());
        Collections.shuffle(spawns);

        player.teleport(spawns.get(0));
        player.setHealth(20);
    }

    public void decreaseBounds() {
        if(arenaTeam.getBounds2().getZ() > arenaTeam.getBounds1().getZ()) {
            arenaTeam.setBounds2(arenaTeam.getBounds2().getRelative(0,0,-1));
        }
        else {
            arenaTeam.setBounds2(arenaTeam.getBounds2().getRelative(0,0,1));
        }
    }

    public void increaseBounds() {
        if(arenaTeam.getBounds2().getZ() > arenaTeam.getBounds1().getZ()) {
            arenaTeam.setBounds2(arenaTeam.getBounds2().getRelative(0,0,1));
        }
        else {
            arenaTeam.setBounds2(arenaTeam.getBounds2().getRelative(0,0,-1));
        }

        int minX = Math.min(arenaTeam.getBounds1().getX(), arenaTeam.getBounds2().getX());
        int maxX = Math.max(arenaTeam.getBounds1().getX(), arenaTeam.getBounds2().getX());

        // Loop through each block in the new row to change it's color.
        for(int i = minX; i <= maxX; i++) {
            Block block = arenaTeam.getBounds1().getWorld().getBlockAt(i, arenaTeam.getBounds1().getY(), arenaTeam.getBounds2().getZ());
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

    public void updateBlocks() {
        int minX = Math.min(arenaTeam.getBounds1().getX(), arenaTeam.getBounds2().getX());
        int maxX = Math.max(arenaTeam.getBounds1().getX(), arenaTeam.getBounds2().getX());
        int minZ = Math.min(arenaTeam.getBounds1().getZ(), arenaTeam.getBounds2().getZ());
        int maxZ = Math.max(arenaTeam.getBounds1().getZ(), arenaTeam.getBounds2().getZ());

        for(int z = minZ; z <= maxZ; z++) {
            for(int x = minX; x <= maxX; x++) {
                Block block = arenaTeam.getBounds1().getWorld().getBlockAt(x, arenaTeam.getBounds1().getY(), z);
                XBlock.setColor(block, teamColor.blockColor());
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
    }
}