package net.jadedmc.turfwars.listeners;

import net.jadedmc.jadedchat.features.channels.events.ChannelMessageSendEvent;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChannelMessageSendListener implements Listener {
    private final TurfWars plugin;

    public ChannelMessageSendListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMessage(ChannelMessageSendEvent event) {
        if(!event.getChannel().name().equalsIgnoreCase("GAME")) {
            return;
        }

        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        event.setViewers(game.getPlayers());
    }
}
