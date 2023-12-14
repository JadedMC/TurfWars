package net.jadedmc.turfwars.commands;

import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.gui.DuelGUI;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Runs the /duel command, which allows players to duel each other.
 */
public class DuelCMD implements CommandExecutor {
    private final TurfWars plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public DuelCMD(final TurfWars plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return true;
        }

        // Make sure the player is using the command properly.
        if(args.length < 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/duel [accept|player]");
            return true;
        }

        // Check for sub commands.
        switch (args[0].toLowerCase()) {

            // Processes the "accept" sub command.
            case "accept" -> {
                // Makes sure the player includes another player..
                if(args.length < 2) {
                    ChatUtils.chat(sender, "&cUsage &8» &c/duel accept [player]");
                    return true;
                }

                Player opponent = Bukkit.getPlayer(args[1]);

                // Make sure the player is still online.
                if(opponent == null) {
                    ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
                    return true;
                }

                // Make sure the duel request exists.
                if(!plugin.duelManager().hasDuelRequest(opponent, player)) {
                    ChatUtils.chat(sender, "&cError &8» &cThat person has not sent a duel request.");
                    return true;
                }

                // Makes sure the player is not in a match already.
                if(plugin.getGameManager().getGame(player) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cYou are in a match already.");
                    return true;
                }

                // Make sure the opponent is not already in a match.
                if(plugin.getGameManager().getGame(opponent) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cThey are in a match already.");
                    return true;
                }

                // Makes sure the player is the party leader if they are in a party.
                Party targetParty = JadedParty.partyManager().getParty(opponent);
                if(targetParty != null && (!targetParty.getLeader().equals(opponent.getUniqueId()))) {
                    ChatUtils.chat(sender, "&cError &8» &cThat player is in a party.");
                    return true;
                }

                // Accepts the Duel request.
                ChatUtils.chat(sender, "&aDuel request has been accepted.");
                ChatUtils.chat(opponent, "&f" + player.getName() + " &ahas accepted your duel request.");
                plugin.duelManager().acceptDuelRequest(opponent, player);
            }

            // If not any of the above, then they must be sending a duel request.
            default -> {
                // Make sure they don't have an active duel request already.
                if(plugin.duelManager().hasDuelRequest(player)) {
                    ChatUtils.chat(sender, "&cError &8» &cYou already have an active duel request.");
                    return true;
                }

                Player opponent = Bukkit.getPlayer(args[0]);

                // Make sure the target is online.
                if(opponent == null) {
                    ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
                    return true;
                }

                // Make sure the player isn't dueling themselves.
                if(opponent.equals(player)) {
                    ChatUtils.chat(sender, "&cError &8» &cYou cannot duel yourself!");
                    return true;
                }

                // Make sure the player isn't in a game already.
                if(plugin.getGameManager().getGame(player) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cYou are in a game already.");
                    return true;
                }

                // Make sure the target isn't in a game already.
                if(plugin.getGameManager().getGame(opponent) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cThey are in a game already.");
                    return true;
                }

                // Opens the duel gui.
                new DuelGUI(plugin, player, opponent).open(player);
            }
        }

        return true;
    }
}