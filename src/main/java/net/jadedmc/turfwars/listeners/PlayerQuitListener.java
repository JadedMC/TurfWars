package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This class runs a listener that is called whenever a player leaves the server.
 * This is important for removing players from any collections they are stored in.
 */
public class PlayerQuitListener implements Listener {
    private final TurfWars plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerQuitListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        // Removes a player from a game if they are in one.
        Game game = plugin.getGameManager().getGame(player);
        if(game != null) {
            game.playerDisconnect(player);
        }
    }
}