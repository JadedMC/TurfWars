package net.jadedmc.turfwars.listeners;

import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedchat.features.channels.events.ChannelSwitchEvent;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChannelSwitchListener implements Listener {
    private final TurfWars plugin;

    public ChannelSwitchListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitch(ChannelSwitchEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            if(event.getToChannel().name().equalsIgnoreCase("GAME")) {
                event.setToChannel(JadedChat.getDefaultChannel());
            }
        }
        else {
            if(event.getToChannel().equals(JadedChat.getDefaultChannel())) {
                event.setToChannel(JadedChat.getChannel("GAME"));
            }
        }
    }
}
