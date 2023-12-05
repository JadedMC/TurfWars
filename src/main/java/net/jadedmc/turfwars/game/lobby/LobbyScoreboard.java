package net.jadedmc.turfwars.game.lobby;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.utils.DateUtils;
import net.jadedmc.turfwars.utils.scoreboard.CustomScoreboard;
import net.jadedmc.turfwars.utils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

/**
 * This class creates and displays the lobby scoreboard.
 */
public class LobbyScoreboard extends CustomScoreboard {
    private final TurfWars plugin;

    /**
     * Links the player with the scoreboard.
     * @param plugin Instance of the plugin.
     * @param player Player to create scoreboard for.
     */
    public LobbyScoreboard(TurfWars plugin, Player player) {
        super(player);
        this.plugin = plugin;

        CustomScoreboard.getPlayers().put(player.getUniqueId(), this);
        update(player);
    }

    /**
     * Updates the scoreboard for a specific player.
     * @param player Player to update scoreboard for.
     */
    public void update(Player player) {
        ScoreHelper helper;

        if(ScoreHelper.hasScore(player)) {
            helper = ScoreHelper.getByPlayer(player);
        }
        else {
            helper = ScoreHelper.createScore(player);
        }

        // Gets the

        // Sets up the scoreboard.
        helper.setTitle("&a&lTurf Wars");
        helper.setSlot(11, "&7" + DateUtils.currentDateToString());
        helper.setSlot(5, "");
        helper.setSlot(4, "&aCoins: " + "&60");
        helper.setSlot(3, "&aWins: &f0");
        helper.setSlot(2, "");
        helper.setSlot(1, "&aplay.jadedmc.net");
    }
}