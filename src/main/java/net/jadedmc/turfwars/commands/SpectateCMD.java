package net.jadedmc.turfwars.commands;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.gui.SpectateGUI;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCMD implements CommandExecutor {
    private final TurfWars plugin;

    public SpectateCMD(TurfWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            new SpectateGUI(plugin).open((Player) sender);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
            return true;
        }

        Game game = plugin.getGameManager().getGame(target);
        if(game == null) {
            ChatUtils.chat(sender, "&cError &8» &cThat player is not in a game.");
            return true;
        }

        Player player = (Player) sender;

        if(plugin.getGameManager().getGame(player) != null) {
            ChatUtils.chat(sender, "&cError &8» &cYou are already spectating someone!");
            return true;
        }

        game.addSpectator(player);
        game.sendMessage("&a" + player.getName() + " is now spectating.");

        return true;
    }
}