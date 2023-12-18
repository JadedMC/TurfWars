package net.jadedmc.turfwars;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.game.team.Team;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.entity.Player;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
class Placeholders extends PlaceholderExpansion {
    private final TurfWars plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public Placeholders(TurfWars plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "tw";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if(identifier.equalsIgnoreCase( "game_displayname")) {

            Game game = plugin.getGameManager().getGame(player);

            if(game == null) {
                return "%jadedcore_rank_chat_prefix_legacy%&7" + player.getName();
            }

            if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                return "%jadedcore_rank_chat_prefix_legacy%&7" + player.getName();
            }

            if(game.getSpectators().contains(player)) {
                return "&7[SPEC] " + player.getName();
            }

            Team team = game.getTeam(player);
            return team.getTeamColor().chatColor() + player.getName() + " &8[" + game.getKills(player) + "-" + game.getDeaths(player) + "]";
        }

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return "";
        }

        if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
            return "";
        }

        switch (identifier) {

            case "game_kills" -> {
                return game.getKills(player) + "";
            }

            case "game_deaths" -> {
                return game.getDeaths(player) + "";
            }

            case "game_team" -> {
                Team team = game.getTeam(player);

                if(team == null) {
                    return "team3";
                }

                if(team.equals(game.getTeam1())) {
                    return "team1";
                }

                if(team.equals(game.getTeam2())) {
                    return "team2";
                }

                return "team3";
            }

            case "team_prefix" -> {

                if(game.getSpectators().contains(player)) {
                    return "<gray>[SPEC]";
                }

                Team team = game.getTeam(player);
                return ChatUtils.replaceChatColor(team.getTeamColor().chatColor()) + "[" + team.getTeamColor().getName().toUpperCase() + "]";
            }
        }

        return null;
    }
}