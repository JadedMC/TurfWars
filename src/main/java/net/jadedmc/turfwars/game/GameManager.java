package net.jadedmc.turfwars.game;

import net.jadedmc.jadedchat.utils.ChatUtils;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import net.jadedmc.turfwars.game.arena.ArenaChunkGenerator;
import net.jadedmc.turfwars.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Manages all active games.
 */
public class GameManager {
    private final TurfWars plugin;
    private final Collection<Game> games = new HashSet<>();

    /**
     * Creates the Game Manager and loads all available arenas.
     * @param plugin Instance of the plugin.
     */
    public GameManager(TurfWars plugin) {
        this.plugin = plugin;
    }

    /**
     * Manually add a game. Used in duels.
     * @param game Game to add.
     */
    public void addGame(Game game) {
        games.add(game);
    }

    /**
     * Finds a game for a player to join and adds them to it.
     * @param player Player to add to the game.
     */
    public void addToGame(Player player) {

        // Remove the player from their previous game if they are in one.
        Game previous = getGame(player);
        if(previous != null) {
            previous.removePlayer(player);
        }

        ChatUtils.chat(player, "&aSending you to the game...");

        findGame().thenAccept(game -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                game.addPlayer(player);
            });
        });
    }

    public CompletableFuture<Game> createGame(Arena arena) {
        UUID gameUUID = UUID.randomUUID();

        // Makes a copy of the arena with the generated uuid.
        CompletableFuture<File> arenaCopy = arena.arenaFile().createCopy(gameUUID.toString());

        // Creates the game.
        CompletableFuture<Game> gameCreation = CompletableFuture.supplyAsync(() -> {
           plugin.getServer().getScheduler().runTask(plugin, () -> {
               WorldCreator worldCreator = new WorldCreator(gameUUID.toString());
               worldCreator.generator(new ArenaChunkGenerator());
               Bukkit.createWorld(worldCreator);
           });

            // Wait for the world to be generated.
            boolean loaded = false;
            World world = null;
            while(!loaded) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for(World w : Bukkit.getWorlds()) {
                    if(w.getName().equals(gameUUID.toString())) {
                        loaded = true;
                        world = w;
                        break;
                    }
                }
            }

            Game game = new Game(plugin, arena, world);
            this.addGame(game);

            return game;
        });

        return arenaCopy.thenCompose(file -> gameCreation);
    }

    /**
     * Deletes a game that is no longer needed.
     * This also deletes its temporary world folder.
     * @param game Game to delete.
     */
    public void deleteGame(Game game) {
        String worldName = game.world().getName();
        games.remove(game);
        File worldFolder = game.world().getWorldFolder();
        Bukkit.unloadWorld(game.world(), false);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            System.out.println("Deleting " + worldName);
            FileUtils.deleteDirectory(worldFolder);
        });
    }

    public CompletableFuture<Game> findGame() {
        List<Game> possibleGames = new ArrayList<>();

        for(Game possibleGame : games) {
            // Make sure the game hasn't already started.
            if(possibleGame.getGameState() != GameState.WAITING && possibleGame.getGameState() != GameState.COUNTDOWN) {
                continue;
            }

            if(possibleGame.getPlayers().size() > 23) {
                continue;
            }

            possibleGames.add(possibleGame);
        }

        // Shuffles list of possible games.
        Collections.shuffle(possibleGames);

        // Returns null if no games are available.
        if(possibleGames.size() == 0) {
            return createGame(plugin.arenaManager().randomArena());
        }

        List<Game> possibleGamesWithPlayers = new ArrayList<>();
        for(Game game : possibleGames) {
            if(game.getPlayers().size() == 0) {
                continue;
            }

            possibleGamesWithPlayers.add(game);
        }

        // If there is a game with players waiting, return that one.
        if(!possibleGamesWithPlayers.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> possibleGamesWithPlayers.get(0));
        }

        // Otherwise, returns the top game of the shuffled list.
        return CompletableFuture.supplyAsync(() -> possibleGames.get(0));
    }

    /**
     * Gets the game the player is currently in.
     * If they are not in a game, returns null.
     * @param player Player to get game of.
     * @return Game they are in.
     */
    public Game getGame(Player player) {
        for(Game game : games) {
            if(game.getPlayers().contains(player)) {
                return game;
            }

            if(game.getSpectators().contains(player)) {
                return game;
            }
        }

        return null;
    }

    public Collection<Game> getGames() {
        return games;
    }

    public List<Game> getActiveGames() {
        List<Game> activeGames = new ArrayList<>();

        for(Game game : getGames()) {
            if(game.getGameState() == GameState.BUILD || game.getGameState() == GameState.FIGHT) {
               activeGames.add(game);
            }
        }

        return activeGames;
    }
}