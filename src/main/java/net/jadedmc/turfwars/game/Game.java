package net.jadedmc.turfwars.game;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.turfwars.LobbyScoreboard;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import net.jadedmc.turfwars.game.kit.Kit;
import net.jadedmc.turfwars.game.team.Team;
import net.jadedmc.turfwars.game.team.TeamColor;
import net.jadedmc.turfwars.utils.LocationUtils;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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

    public Game(TurfWars plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;

        this.gameState = GameState.WAITING;
        this.gameCountdown = new GameCountdown(plugin, this);
        round = 0;
        roundCountdown = new RoundCountdown(plugin);
    }

    public void forceStartGame(Player player) {
        sendMessage("");
        sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas force-started the game!");
        sendMessage("");

        gameCountdown.setSeconds(5);
        gameCountdown.start();
        gameState = GameState.COUNTDOWN;
    }

    public void startGame() {
        // Create the two teams.
        team1 = new Team(arena.getTeam1(), TeamColor.RED);
        team2 = new Team(arena.getTeam2(), TeamColor.AQUA);

        team1.updateBlocks();
        team2.updateBlocks();

        // Randomize players and add them to teams.
        Collections.shuffle(players);
        List<List<Player>> teams = Lists.partition(players, players.size() / 2);
        team1.addPlayers(teams.get(0));
        team2.addPlayers(teams.get(1));

        team1.spawn();
        team2.spawn();

        for(Player player : players) {
            plugin.getKitManager().getKit(player).applyKit(player, this);
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
        sendMessage("<green>Map - <white>" + arena.getName() + " <dark_gray>by <white>" + arena.getAuthor());
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

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(gameState == GameState.FIGHT) {
                roundCountdown.stop();
                startBuild();
            }
        }, length*20);
    }

    public void endGame(Team winner) {
        gameState = GameState.END;
        roundCountdown.stop();

        sendMessage("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sendCenteredMessage("<green><bold>Winner - " + ChatUtils.replaceChatColor(winner.getTeamColor().chatColor()) + winner.getTeamColor().getName());
        sendMessage("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : getPlayers()) {
                ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                ChatUtils.centeredChat(player, "&a&lReward Summary");
                ChatUtils.chat(player, "");
                ChatUtils.chat(player, "  &7You Earned:");
                ChatUtils.chat(player, "    &f• &6" + 0 + " Turf Wars Coins");
                ChatUtils.chat(player, "    &f• &b" + 0 + " Turf Wars Experience");
                ChatUtils.chat(player, "");
                ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            }
        }, 3*20);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            round = 0;
            gameCountdown = new GameCountdown(plugin, this);

            arena.reset();
            team1.updateBlocks();
            team2.updateBlocks();

            for(Block block : placedBlocks) {
                block.setType(Material.AIR);
            }
            placedBlocks.clear();

            team1.getPlayers().clear();
            team2.getPlayers().clear();

            for(Player player : players) {
                plugin.getKitManager().removePlayer(player);
                new LobbyScoreboard(plugin, player).addPlayer(player);
                player.teleport(LocationUtils.getSpawn(plugin));
                JadedChat.setChannel(player, JadedChat.getDefaultChannel());
                player.setGameMode(GameMode.ADVENTURE);
            }

            getSpectators().forEach(this::removeSpectator);

            players.clear();
            kills.clear();
            deaths.clear();

            gameState = GameState.WAITING;
        }, 5*20);
    }

    // -----------------------------------------------------------------------------------------------------
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

        player.teleport(arena.getWaitingArea());
        sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas joined the game! (&f"+ players.size() + "&a/&f" + 8 + "&a)");
        new GameScoreboard(plugin, player, this).addPlayer(player);

        // Checks if the game is at least 75% full.
        if(players.size() >= 4 && gameCountdown.getSeconds() == 30) {
            // If so, starts the countdown.
            gameCountdown.start();
            gameState = GameState.COUNTDOWN;
        }

        // Checks if the game is 100% full.
        if(players.size() >= 6 && gameCountdown.getSeconds() > 5) {
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

        player.teleport(arena.getTeam1().getRandomSpawn());
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

        team.respawn(player);
        plugin.getKitManager().getKit(player).applyKit(player, this);

        for(int i = 0; i < round; i++) {
            team.decreaseBounds();
            opposingTeam.increaseBounds();

            if(team.getLines() == 0) {
                endGame(opposingTeam);
                return;
            }
        }
    }

    public void removePlayer(Player player) {
        new LobbyScoreboard(plugin, player).addPlayer(player);
        players.remove(player);
        kills.remove(player);
        deaths.remove(player);
        player.teleport(LocationUtils.getSpawn(plugin));

        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN) {
            sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas left the game! (&f"+ players.size() + "&a/&f8&a)");

            if(getPlayers().size() < 4 && gameState == GameState.COUNTDOWN) {
                sendMessage("&cNot enough players! Countdown reset.");
                gameCountdown.cancel();
                gameCountdown = new GameCountdown(plugin, this);
                gameState = GameState.WAITING;
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
}