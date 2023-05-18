package net.jadedmc.turfwars.game;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.utils.DateUtils;
import net.jadedmc.turfwars.utils.scoreboard.CustomScoreboard;
import net.jadedmc.turfwars.utils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

public class GameScoreboard extends CustomScoreboard {
    private final Game game;

    public GameScoreboard(TurfWars plugin, Player player, Game game) {
        super(player);
        this.game = game;

        CustomScoreboard.getPlayers().put(player.getUniqueId(), this);
        update(player);
    }

    public void update(Player player) {
        ScoreHelper helper;

        if(ScoreHelper.hasScore(player)) {
            helper = ScoreHelper.getByPlayer(player);
        }
        else {
            helper = ScoreHelper.createScore(player);
        }

        switch (game.getGameState()) {
            case WAITING,COUNTDOWN -> {
                helper.setTitle("&a&lTURF WARS");
                helper.setSlot(10, "&7" + DateUtils.currentDateToString());
                helper.setSlot(9, "");
                helper.setSlot(8, "&fMap: &a" + game.getArena().getName());
                helper.setSlot(7, "&fPlayers: &a" + game.getPlayers().size() + "&f/&a" + 8);
                helper.setSlot(6, "");

                helper.setSlot(5, "&fKit: &a" + game.getKit(player).getName());
                helper.setSlot(4, "");

                if(game.getGameState() == GameState.COUNTDOWN) {
                    helper.setSlot(3, "&fStarting in &a" + game.getGameCountdown().getSeconds() +  "s");
                }
                else {
                    helper.setSlot(3, "&fWaiting for players");
                }

                helper.setSlot(2, "");
                helper.setSlot(1, "&ajadedmc.net");
            }

            case BUILD -> {
                helper.setTitle("&a&lTURF WARS");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString());
                helper.setSlot(14, "");
                helper.setSlot(13, "&f" + game.getTeam1().getLines() + " " + game.getTeam1().getTeamColor().chatColor() + game.getTeam1().getTeamColor().getName());
                helper.setSlot(12, "");
                helper.setSlot(11, "&f" + game.getTeam2().getLines() + " " + game.getTeam2().getTeamColor().chatColor() + game.getTeam2().getTeamColor().getName());
                helper.setSlot(10, "");
                helper.setSlot(9, "&e&lBuild Time");
                helper.setSlot(8, "&f" + game.getRoundCountdown());
                helper.setSlot(7, "");
                helper.removeSlot(6);
                helper.removeSlot(5);
                helper.removeSlot(4);
                helper.removeSlot(3);
                helper.removeSlot(2);
                helper.removeSlot(2);
                helper.setSlot(1, "&ajadedmc.net");
            }

            case FIGHT -> {
                helper.setTitle("&a&lTURF WARS");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString());
                helper.setSlot(14, "");
                helper.setSlot(13, "&f" + game.getTeam1().getLines() + " " + game.getTeam1().getTeamColor().chatColor() + game.getTeam1().getTeamColor().getName());
                helper.setSlot(12, "");
                helper.setSlot(11, "&f" + game.getTeam2().getLines() + " " + game.getTeam2().getTeamColor().chatColor() + game.getTeam2().getTeamColor().getName());
                helper.setSlot(10, "");
                helper.setSlot(9, "&e&lCombat Time");
                helper.setSlot(8, "&f" + game.getRoundCountdown());
                helper.setSlot(7, "");
                helper.removeSlot(6);
                helper.removeSlot(5);
                helper.removeSlot(4);
                helper.removeSlot(3);
                helper.removeSlot(2);
                helper.removeSlot(2);
                helper.setSlot(1, "&ajadedmc.net");
            }

            case END -> {
                helper.setTitle("&a&lTURF WARS");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString());
                helper.setSlot(14, "");
                helper.setSlot(13, "&f" + game.getTeam1().getLines() + " " + game.getTeam1().getTeamColor().chatColor() + game.getTeam1().getTeamColor().getName());
                helper.setSlot(12, "");
                helper.setSlot(11, "&f" + game.getTeam2().getLines() + " " + game.getTeam2().getTeamColor().chatColor() + game.getTeam2().getTeamColor().getName());
                helper.setSlot(10, "");
                helper.setSlot(9, "&e&lGame End");
                helper.setSlot(8, "");
                helper.removeSlot(7);
                helper.removeSlot(6);
                helper.removeSlot(5);
                helper.removeSlot(4);
                helper.removeSlot(3);
                helper.removeSlot(2);
                helper.removeSlot(2);
                helper.setSlot(1, "&ajadedmc.net");
            }

            default -> {
                helper.setTitle("&a&lTURF WARS");
                helper.setSlot(2, "");
                helper.setSlot(1, "&ajadedmc.net");
            }
        }
    }
}