package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {
    private final TurfWars plugin;

    public EntityDamageByEntityListener(TurfWars plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        arrowDamage(event);
        meleeDamage(event);
    }

    private void arrowDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        if(!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }

        if(!(arrow.getShooter() instanceof Player shooter)) {
            return;
        }

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        if(game.getGameState() != GameState.FIGHT) {
            event.setCancelled(true);
            return;
        }

        if(game.getTeam(player).equals(game.getTeam(shooter))) {
            event.setCancelled(true);
            return;
        }

        //
        if(game.getTeam(player).getLines() > 14 && !game.getTeam1().isInBounds(player.getLocation()) && !game.getTeam2().isInBounds(player.getLocation())) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0);
        game.playerKilled(player, shooter);
    }

    private void meleeDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        if(!(event.getDamager() instanceof Player killer)) {
            return;
        }

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        if(game.getSpectators().contains(player) || game.getSpectators().contains(killer)) {
            event.setCancelled(true);
            return;
        }

        if(game.getGameState() != GameState.FIGHT) {
            event.setCancelled(true);
            return;
        }

        if(game.getTeam(player).equals(game.getTeam(killer))) {
            event.setCancelled(true);
            return;
        }

        if(player.getHealth() <= event.getDamage()) {
            event.setDamage(0);
            game.playerKilled(player, killer);
        }
    }
}