package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.game.kit.KitsGUI;
import net.jadedmc.turfwars.utils.ItemUtils;
import net.jadedmc.turfwars.utils.LocationUtils;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final TurfWars plugin;

    public PlayerInteractListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Exit if the item is null.
        if(event.getItem() == null)
            return;

        // Exit if item meta is null.
        if(event.getItem().getItemMeta() == null)
            return;

        String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

        if(item == null) {
            return;
        }

        switch (item) {
            case "Kit Selector" -> {
                Game game = plugin.getGameManager().getGame(player);
                if(game == null) {
                    return;
                }

                switch (game.getGameState()) {
                    case WAITING, COUNTDOWN -> {
                        new KitsGUI(plugin).open(player);
                    }
                    case BUILD, FIGHT -> {
                        if(game.getTeam1().getArenaTeam().isInBounds(player.getLocation()) || game.getTeam2().getArenaTeam().isInBounds(player.getLocation())) {
                            ChatUtils.chat(player, "<red>You must be in your spawn to change your kit.");
                            return;
                        }

                        new KitsGUI(plugin).open(player);
                    }
                }
            }

            case "Leave" -> {
                Game game = plugin.getGameManager().getGame(player);

                if (game == null) {
                    return;
                }

                game.removePlayer(player);
                player.teleport(LocationUtils.getSpawn(plugin));
                ItemUtils.giveLobbyItems(player);
            }
        }
    }
}