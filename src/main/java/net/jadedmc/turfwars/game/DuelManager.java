package net.jadedmc.turfwars.game;

import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages the creation of duel requests and duel games.
 */
public class DuelManager {
    private final TurfWars plugin;
    private final Map<Player, Map.Entry<Player, String>> duelRequests = new HashMap<>();

    /**
     * Creates the Duel manager.
     * @param plugin Instance of the plugin.
     */
    public DuelManager(final TurfWars plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a player accepts a duel request.
     * @param sender The sender of the duel request.
     * @param receiver The receiver of the duel request.
     */
    public void acceptDuelRequest(Player sender, Player receiver) {
        // Exit if the duel request does not exist anymore.
        if(!hasDuelRequest(sender, receiver)) {
            return;
        }

        String map = duelRequests.get(sender).getValue();

        // Remove the duel request from the duel requests map.
        duelRequests.remove(sender);

        // If given a specific map, create a game.
        if(plugin.arenaManager().getArena(map) != null) {
            Arena arena = plugin.arenaManager().getArena(map);

            if(arena == null) {
                return;
            }

            plugin.getGameManager().createGame(arena).thenAccept(game -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    List<Player> senderPlayers = new ArrayList<>();
                    List<Player> receiverPlayers = new ArrayList<>();

                    // Check for the party of the duel sender.
                    Party senderParty = JadedParty.partyManager().getParty(sender);
                    if(senderParty != null) {
                        for(UUID playerUUID : senderParty.getPlayers()) {
                            Player player = Bukkit.getPlayer(playerUUID);

                            // Make sure the player is online.
                            if(player == null) {
                                continue;
                            }

                            // Remove the player from their game if they are in one.
                            Game memberGame = plugin.getGameManager().getGame(player);
                            if(memberGame != null) {
                                memberGame.removePlayer(player);
                            }

                            senderPlayers.add(player);
                        }
                    }
                    else {
                        senderPlayers.add(sender);
                    }

                    // Check for the party of the duel receiver.
                    Party receiverParty = JadedParty.partyManager().getParty(receiver);
                    if(receiverParty != null) {
                        for(UUID playerUUID : receiverParty.getPlayers()) {
                            Player player = Bukkit.getPlayer(playerUUID);

                            // Make sure the player is online.
                            if(player == null) {
                                continue;
                            }

                            // Remove the player from their game if they are in one.
                            Game memberGame = plugin.getGameManager().getGame(player);
                            if(memberGame != null) {
                                memberGame.removePlayer(player);
                            }

                            receiverPlayers.add(player);
                        }
                    }
                    else {
                        receiverPlayers.add(receiver);
                    }

                    plugin.getGameManager().addGame(game);
                    senderPlayers.forEach(game::addPlayer);
                    receiverPlayers.forEach(game::addPlayer);
                    game.startCountdown();
                });
            });
        }
    }

    /**
     * Add a duel request to the storage.
     * @param player Player sending the duel request.
     * @param target Player receiving the duel request.
     * @param map Map the duel is for.
     */
    public void addDuelRequest(Player player, Player target, String map) {
        duelRequests.put(player, new AbstractMap.SimpleEntry<>(target, map));

        // Makes the request expire after a minute.
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(duelRequests.containsKey(player) && duelRequests.get(player).getKey().equals(target)) {
                ChatUtils.chat(player, "&aYour duel request to &f" + target.getName() + "&a has expired.");
                duelRequests.remove(player);
            }
        }, 1200);

        String mapDisplayName;
        switch (map) {
            case "any" -> mapDisplayName = "Random Map";
            default -> mapDisplayName = plugin.arenaManager().getArena(map).name();
        }

        // Sends a message to the sender.
        ChatUtils.chat(player, "&aDuel request sent to &f" + target.getName() + " &ain &f" + mapDisplayName + "&a.");

        // Sends a message to the target.
        ChatUtils.chat(target, "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        ChatUtils.centeredChat(target, "&a&lDuel Request");
        ChatUtils.chat(target, "");
        ChatUtils.centeredChat(target, "&f" + player.getName() + " &7wants to duel you in &f" + mapDisplayName + "&7!");
        ChatUtils.chat(target, "");
        ChatUtils.chat(target, "  <dark_gray>» <click:suggest_command:'/duel accept " + player.getName() + "'><hover:show_text:'<green>Click to accept'><green>/duel accept " + player.getName() + "</hover></click>");
        ChatUtils.chat(target, "  <dark_gray>» <click:suggest_command:'/duel deny " + player.getName() + "'><hover:show_text:'<red>Click to deny'><red>/duel deny " + player.getName() + "</hover></click>");
        ChatUtils.chat(target, "");
        ChatUtils.chat(target, "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    /**
     * Checks if a duel request exists.
     * @param sender Sender of the duel request.
     * @param receiver Receiver of the duel request.
     * @return Whether the request exists.
     */
    public boolean hasDuelRequest(Player sender, Player receiver) {
        if(!duelRequests.containsKey(sender)) {
            return false;
        }

        return duelRequests.get(sender).getKey() == receiver;
    }

    /**
     * Get if a player has an active duel request.
     * @param player Player to check.
     * @return Whether they have a duel request active already.
     */
    public boolean hasDuelRequest(Player player) {
        return duelRequests.containsKey(player);
    }
}