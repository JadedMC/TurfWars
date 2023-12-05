package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.game.kit.kits.InfiltratorKit;
import net.jadedmc.turfwars.game.team.Team;
import net.jadedmc.turfwars.utils.MathUtils;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class PlayerMoveListener implements Listener {
    private final TurfWars plugin;
    private final Collection<UUID> exempt = new HashSet<>();

    public PlayerMoveListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(exempt.contains(player.getUniqueId())) {
            return;
        }

        Game game = plugin.getGameManager().getGame(event.getPlayer());

        if(game == null) {
            return;
        }

        if(game.getGameState() != GameState.BUILD && game.getGameState() != GameState.FIGHT) {
            return;
        }

        if(game.getSpectators().contains(player)) {
            return;
        }

        Team opposing = game.getOpposingTeam(event.getPlayer());

        if((plugin.getKitManager().getKit(player) instanceof InfiltratorKit) && game.getGameState() == GameState.FIGHT) {
            ((InfiltratorKit) plugin.getKitManager().getKit(player)).processSlowness(plugin, player, game);
            return;
        }

        if(opposing.isInBounds(event.getPlayer().getLocation())) {

            exempt.add(event.getPlayer().getUniqueId());

            player.playSound(player.getLocation(), Sound.NOTE_BASS, 2, 1);
            ChatUtils.chat(player, "<red>You cannot walk on the enemies turf!");

            Team team = game.getTeam(player);

            Vector vector = MathUtils.getTrajectory2d(player.getLocation(), team.getArenaTeam().randomSpawn(game.world()));
            vector.normalize();
            vector.multiply(2);
            vector.setY(vector.getY() + 0.8);

            if (vector.getY() > 1) {
                vector.setY(1);
            }

            if(player.isOnGround()) {
                vector.setY(vector.getY() + 0.2);
            }

            player.setVelocity(vector);
            player.setFallDistance(0);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> exempt.remove(player.getUniqueId()), 20);
        }
    }
}
