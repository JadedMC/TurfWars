package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class EntityShootBowListener implements Listener {
    private final TurfWars plugin;



    public EntityShootBowListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        if(game.getGameState() == GameState.FIGHT) {
            plugin.getKitManager().getKit(player).regenArrow(player, game);
            return;
        }

        if(game.getGameState() == GameState.BUILD) {
            ChatUtils.chat(player, "<red>You cannot fight during build time!");
        }

        event.setCancelled(true);
        player.updateInventory();
    }
}
