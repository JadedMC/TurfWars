/*
 * This file is part of TurfWars, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.turfwars.game;

import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
import net.jadedmc.turfwars.game.lobby.LobbyScoreboard;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import net.jadedmc.turfwars.game.kit.Kit;
import net.jadedmc.turfwars.game.lobby.LobbyUtils;
import net.jadedmc.turfwars.game.team.Team;
import net.jadedmc.turfwars.game.team.TeamColor;
import net.jadedmc.turfwars.utils.LocationUtils;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Game {
    private final TurfWars plugin;
    private final Arena arena;
    private GameState gameState;
    private int round;
    private Team team1;
    private Team team2;
    private final List<Player> players = new ArrayList<>();
    private GameCountdown gameCountdown;
    private RoundCountdown roundCountdown;
    private final Collection<Block> placedBlocks = new HashSet<>();
    private final Map<Player, Integer> kills = new HashMap<>();
    private final Map<Player, Integer> deaths = new HashMap<>();
    private final Set<Player> spectators = new HashSet<>();
    private final World world;

    public Game(final TurfWars plugin, final Arena arena, final World world) {
        this.plugin = plugin;
        this.arena = arena;
        this.world = world;

        this.gameState = GameState.WAITING;
        this.gameCountdown = new GameCountdown(plugin, this);
        round = 0;
        roundCountdown = new RoundCountdown(plugin);

        // Set world game rules.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(6000);
        });
    }

    public void forceStartGame(Player player) {
        sendMessage("");
        sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas force-started the game!");
        sendMessage("");

        gameCountdown.setSeconds(5);
        gameCountdown.start();
        gameState = GameState.COUNTDOWN;
    }

    public void startCountdown() {
        if(gameState == GameState.COUNTDOWN) {
            return;
        }

        gameCountdown.setSeconds(5);
        gameCountdown.start();
        gameState = GameState.COUNTDOWN;
    }

    public void startGame() {
        // Create the two teams.
        team1 = new Team(arena.team1(), TeamColor.RED, world);
        team2 = new Team(arena.team2(), TeamColor.AQUA, world);

        // Randomize players and add them to teams.
        Collections.shuffle(players);
        List<List<Player>> teams = Lists.partition(players, players.size() / 2);
        //team1.addPlayers(teams.get(0));
        //team2.addPlayers(teams.get(1));
        createTeams();

        team1.spawn();
        team2.spawn();

        for(Player player : players) {
            plugin.getKitManager().getKit(player).applyKit(player, this);
            player.spigot().setCollidesWithEntities(true);
            JadedChat.setChannel(player, JadedChat.getChannel("GAME"));
            player.setGameMode(GameMode.SURVIVAL);
        }

        sendMessage("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sendCenteredMessage("<green><bold>Turf Wars");
        sendMessage("");
        sendMessage("<white>You have <green>40 seconds <white>to build your <aqua>fort<white>!");
        sendMessage("<white>Each <red>kill <white>advances your turf forwards.");
        sendMessage("<white>Take over <yellow>All The Turf <white>to win!");
        sendMessage("");
        sendMessage("<green>Map - <white>" + arena.name() + " <dark_gray>by <white>" + arena.builders());
        sendMessage("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        startBuild();
    }

    public void startBuild() {
        gameState = GameState.BUILD;

        int length = 20;
        int woolAmount = 32;

        if(round == 0) {
            length = 40;
            woolAmount = 64;
        }

        if(round != 0) {
            sendMessage("");
            sendMessage("<bold><white>" + length + " Seconds of <green>Build Time <white>has begun!");
            sendMessage("");

            getPlayers().forEach(player -> player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1));
            getSpectators().forEach(player -> player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1));
        }

        roundCountdown = new RoundCountdown(plugin);
        roundCountdown.setSeconds(length);
        roundCountdown.start();

        for(Player player : getPlayers()) {
            int slot = player.getInventory().first(Material.WOOL);
            if(slot >= 0) {
                if(player.getInventory().getItem(slot).getAmount() < woolAmount) {
                    player.getInventory().getItem(slot).setAmount(woolAmount);
                }
            }
            else {
                ItemBuilder wool = new ItemBuilder(Material.WOOL, woolAmount).dye(getTeam(player).getTeamColor().blockColor());
                player.getInventory().addItem(wool.build());
            }
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(gameState == GameState.BUILD) {
                roundCountdown.stop();
                startCombat();
            }
        }, length*20);
    }

    public void startCombat() {
        gameState = GameState.FIGHT;
        round++;

        int length = 90;
        roundCountdown = new RoundCountdown(plugin);
        roundCountdown.setSeconds(length);
        roundCountdown.start();

        sendMessage("");
        sendMessage("1 Kill = " + round + " Turf Lines");
        sendMessage("<bold><white>" + length + " Seconds of <yellow>Combat Time <white>has begun!");
        sendMessage("");

        getPlayers().forEach(player -> player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1));
        getSpectators().forEach(player -> player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1));

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(gameState == GameState.FIGHT) {
                roundCountdown.stop();

                if(round >= 8 && (team1.getLines() > team2.getLines())) {
                    endGame(team1);
                }
                else if(round >= 8 && (team2.getLines() > team1.getLines())) {
                    endGame(team2);
                }
                else {
                    startBuild();
                }
            }
        }, length*20);
    }

    public void endGame(Team winner) {
        gameState = GameState.END;
        roundCountdown.stop();

        sendMessage("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sendCenteredMessage("<green><bold>Winner - " + ChatUtils.replaceChatColor(winner.getTeamColor().chatColor()) + winner.getTeamColor().getName());
        sendMessage("");
        /*
        sendCenteredMessage(team1.getTeamColor().chatColor() + "<bold>" + team1.getTeamColor().getName());
        for(Player player : team1.getPlayers()) {
            sendCenteredMessage(team1.getTeamColor() + player.getName() + "<dark_gray>[" + kills.get(player) + "-" + deaths.get(player) + "]");
        }

        sendMessage("");
        sendCenteredMessage(team2.getTeamColor().chatColor() + "<bold>" + team2.getTeamColor().getName());
        for(Player player : team2.getPlayers()) {
            sendCenteredMessage(team2.getTeamColor() + player.getName() + "<dark_gray>[" + kills.get(player) + "-" + deaths.get(player) + "]");
        }
        */

        if(winner.getPlayers().size() > 0) {
            Player mvp = winner.getPlayers().get(0);
            for(Player player : winner.getPlayers()) {
                if(player.equals(mvp)) {
                    continue;
                }

                int score = kills.get(player) - deaths.get(player);
                int mvpScore = kills.get(mvp) - deaths.get(mvp);
                int mvpKills = getKills(mvp);
                int kills = getKills(player);

                if(score > mvpScore) {
                    mvp = player;
                }

                if(score == mvpScore) {
                    if(kills > mvpKills) {
                        mvp = player;
                    }
                }
            }

            sendCenteredMessage("MVP: " + ChatUtils.replaceChatColor(winner.getTeamColor().chatColor()) + mvp.getName() + " <dark_gray>[" + kills.get(mvp) + "-" + deaths.get(mvp) + "]");
        }
        else {
            sendCenteredMessage("MVP: <gray>NONE");
        }

        Team loser = getOpposingTeam(winner);

        if(loser.getPlayers().size() > 0) {
            Player lmvp = loser.getPlayers().get(0);
            for(Player player : loser.getPlayers()) {
                if(player.equals(lmvp)) {
                    continue;
                }

                int score = kills.get(player) - deaths.get(player);
                int lmvpScore = kills.get(lmvp) - deaths.get(lmvp);
                int lmvpKills = getKills(lmvp);
                int kills = getKills(player);

                if(score > lmvpScore) {
                    lmvp = player;
                }

                if(score == lmvpScore) {
                    if(kills > lmvpKills) {
                        lmvp = player;
                    }
                }
            }

            sendCenteredMessage("LMVP: " + ChatUtils.replaceChatColor(loser.getTeamColor().chatColor()) + lmvp.getName() + " <dark_gray>[" + kills.get(lmvp) + "-" + deaths.get(lmvp) + "]");
        }
        else {
            sendCenteredMessage("LMVP: <gray>NONE");
        }




        sendMessage("");

        sendMessage("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            round = 0;
            gameCountdown = new GameCountdown(plugin, this);

            for(Block block : placedBlocks) {
                block.setType(Material.AIR);
            }
            placedBlocks.clear();

            team1.getPlayers().clear();
            team2.getPlayers().clear();

            players.forEach(player -> LobbyUtils.sendToLobby(plugin, player));

            List<Player> tempSpectators = new ArrayList<>(spectators);
            tempSpectators.forEach(this::removeSpectator);

            players.clear();
            kills.clear();
            deaths.clear();

            plugin.getGameManager().deleteGame(this);
        }, 5*20);
    }

    // -----------------------------------------------------------------------------------------------------
    public void createTeams() {
        List<Player> tempPlayers = new ArrayList<>(players);
        Collections.shuffle(tempPlayers);

        List<ArrayList<Player>> teams = new ArrayList<>();
        List<Party> parties = new ArrayList<>();
        List<Player> soloPlayers = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            System.out.println("Added Team: " + i);
            teams.add(new ArrayList<>());
        }

        // Loops through all players looking for parties.
        for(Player player : players) {
            Party party = JadedParty.partyManager().getParty(player);

            // Makes sure the player has a party.
            if(party == null) {
                // If they don't, add them to the solo players list.
                soloPlayers.add(player);
                System.out.println("Solo player added: " + player.getName());
                continue;
            }

            // Makes sure the party isn't already listed.
            if(parties.contains(party)) {
                continue;
            }

            parties.add(party);
        }

        // Loop through parties to assign them to teams.
        for(Party party : parties) {

            // Finds the smallest party available to put the party.
            List<Player> smallestTeam = teams.get(0);
            // Loop through each team to find the smallest.
            for(List<Player> team : teams) {
                if(team.size() < smallestTeam.size()) {
                    smallestTeam = team;
                }
            }

            // Adds  all players in the party to the smallest team.
            for(UUID member : party.getPlayers()) {
                smallestTeam.add(Bukkit.getPlayer(member));
            }
        }

        // Shuffle solo players.
        Collections.shuffle(soloPlayers);

        // Loop through solo players to assign them teams.
        while(soloPlayers.size() > 0) {
            List<Player> smallestTeam = teams.get(0);

            // Loop through each team to find the smallest.
            for(List<Player> team : teams) {
                if(team.size() < smallestTeam.size()) {
                    smallestTeam = team;
                }
            }

            // Adds the player to the smallest team.
            smallestTeam.add(soloPlayers.get(0));
            soloPlayers.remove(soloPlayers.get(0));
        }

        // Creates the team objects.
        int arenaTeamNumber = 0;
        for(List<Player> teamPlayers : teams) {
            arenaTeamNumber++;

            if(arenaTeamNumber == 1) {
                team1.addPlayers(teamPlayers);
            }
            else {
                team2.addPlayers(teamPlayers);
            }
        }
    }

    public void addBlock(Block block) {
        placedBlocks.add(block);
    }

    public void addDeath(Player player) {
        deaths.put(player, deaths.get(player) + 1);
    }

    public void addKill(Player player) {
        kills.put(player, kills.get(player) + 1);
    }

    public void addPlayer(Player player) {
        // If not, just adds themselves.
        players.add(player);
        kills.put(player, 0);
        deaths.put(player, 0);

        plugin.getKitManager().addPlayer(player, plugin.getKitManager().getKit("marksman"));

        player.getInventory().clear();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.getInventory().setItem(0, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&aKit Selector").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.BED).setDisplayName("&c&lLeave").build());

        player.teleport(arena.waitingArea(world));
        sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%jadedcore_rank_color%") + player.getName() + " &ahas joined the game! (&f"+ players.size() + "&a/&f" + 8 + "&a)");
        new GameScoreboard(plugin, player, this).addPlayer(player);

        // Checks if the game is at least 75% full.
        if(players.size() >= 4 && gameCountdown.getSeconds() == 30 && gameState == GameState.WAITING) {
            // If so, starts the countdown.
            gameState = GameState.COUNTDOWN;
            gameCountdown.start();
        }

        // Checks if the game is 100% full.
        if(players.size() >= 6 && gameCountdown.getSeconds() > 5 && gameState == GameState.WAITING) {
            // If so, shortens the countdown to 5 seconds.
            gameCountdown.setSeconds(5);
        }
    }

    /**
     * Add a spectator to the game.
     * @param player Spectator to add.
     */
    public void addSpectator(Player player) {
        spectators.add(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        new GameScoreboard(plugin, player, this).addPlayer(player);
        JadedChat.setChannel(player, JadedChat.getChannel("GAME"));

        player.teleport(arena.spectatorSpawn(world));

        // Prevents player from interfering.
        player.spigot().setCollidesWithEntities(false);

        ItemStack leave = new ItemBuilder(Material.BED)
                .setDisplayName("&cLeave Match")
                .build();
        player.getInventory().setItem(8, leave);

        // Delayed to prevent TeleportFix from making visible again.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Player pl : getPlayers()) {
                pl.hidePlayer(player);
            }

            for(Player spectator : getSpectators()) {
                spectator.hidePlayer(player);
            }
        }, 2);
    }

    public Arena getArena() {
        return arena;
    }

    public Collection<Block> getPlacedBlocks() {
        return placedBlocks;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getRound() {
        return round;
    }

    public RoundCountdown getRoundCountdown() {
        return roundCountdown;
    }

    public Team getOpposingTeam(Player player) {
        if(team1.getPlayers().contains(player)) {
            return team2;
        }
        else {
            return team1;
        }
    }

    public Team getOpposingTeam(Team team) {
        if(team.equals(team1)) {
            return team2;
        }
        else {
            return team1;
        }
    }

    /**
     * Get all current spectators.
     * @return All current spectators.
     */
    public Set<Player> getSpectators() {
        return spectators;
    }

    public Team getTeam(Player player) {
        if(team1.getPlayers().contains(player)) {
            return team1;
        }

        if(team2.getPlayers().contains(player)) {
            return team2;
        }

        return null;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void removeBlock(Block block) {
        placedBlocks.remove(block);
    }

    public void sendMessage(String message) {
        for (Player player : players) {
            ChatUtils.chat(player, message);
        }

        for(Player player : spectators) {
            ChatUtils.chat(player, message);
        }
    }

    public void sendCenteredMessage(String message) {
        for (Player player : players) {
            ChatUtils.centeredChat(player, message);
        }

        for(Player player : spectators) {
            ChatUtils.centeredChat(player, message);
        }
    }

    public void playerDisconnect(Player player) {
        if(spectators.contains(player)) {
           removeSpectator(player);
           return;
        }

        if(gameState == GameState.BUILD || gameState == GameState.FIGHT) {
            sendMessage("&8Quit> &7" + player.getName() + " disconnected");

            Team team = getTeam(player);
            Team opposing = getOpposingTeam(player);
            removePlayer(player);

            if(team.getPlayers().size() == 0) {
                endGame(opposing);
            }
        }
        else {
            removePlayer(player);
        }
    }

    public void playerKilled(Player player, Player killer) {
        Team team = getTeam(player);
        Team opposingTeam = getOpposingTeam(player);

        addKill(killer);
        addDeath(player);

        ChatUtils.chat(player, "<blue>Death> <gray>You were killed by " + ChatUtils.replaceChatColor(opposingTeam.getTeamColor().chatColor()) + killer.getName() + "<gray>.");
        ChatUtils.chat(killer, "<blue>Death> <gray>You killed " + ChatUtils.replaceChatColor(team.getTeamColor().chatColor()) + player.getName() + "<gray>.");

        player.getInventory().clear();
        team.killPlayer(player);

        for(int i = 0; i < round; i++) {
            team.decreaseBounds();
            opposingTeam.increaseBounds();

            if(team.getLines() == 0) {
                endGame(opposingTeam);
                return;
            }
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            team.respawn(player);
            plugin.getKitManager().getKit(player).applyKit(player, this);
        }, 4*20);
    }

    public void removePlayer(Player player) {
        if(spectators.contains(player)) {
            removeSpectator(player);
            return;
        }

        players.remove(player);
        kills.remove(player);
        deaths.remove(player);

        LobbyUtils.sendToLobby(plugin, player);

        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN) {
            sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas left the game! (&f"+ players.size() + "&a/&f8&a)");

            if(getPlayers().size() < 4 && gameState == GameState.COUNTDOWN) {
                sendMessage("&cNot enough players! Countdown reset.");
                gameCountdown.cancel();
                gameCountdown = new GameCountdown(plugin, this);
                gameState = GameState.WAITING;
            }

            // If the game is empty, delete it.
            if(players.size() == 0) {
                System.out.println("0 Players detected");
                plugin.getGameManager().deleteGame(this);
            }
        }
        else {
            getTeam(player).removePlayer(player);
        }
    }

    /**
     * Remove a spectator from the game.
     * @param player Spectator to remove.
     */
    public void removeSpectator(Player player) {
        spectators.remove(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setMaxHealth(20.0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.teleport(LocationUtils.getSpawn(plugin));
        player.spigot().setCollidesWithEntities(true);
        JadedChat.setChannel(player, JadedChat.getDefaultChannel());

        // Clears arrows from the player. Requires craftbukkit.
        //((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);

        new LobbyScoreboard(plugin, player);

        for(Player pl : Bukkit.getOnlinePlayers()) {
            pl.showPlayer(player);
        }
    }

    public GameCountdown getGameCountdown() {
        return gameCountdown;
    }

    public int getKills(Player player) {
        return kills.get(player);
    }

    public int getDeaths(Player player) {
        return deaths.get(player);
    }

    public Kit getKit(Player player) {
        return plugin.getKitManager().getKit(player);
    }

    public World world() {
        return world;
    }
}