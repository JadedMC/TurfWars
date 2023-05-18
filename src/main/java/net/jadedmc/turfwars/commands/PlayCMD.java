package net.jadedmc.turfwars.commands;

import net.jadedmc.turfwars.TurfWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayCMD implements CommandExecutor {
    private final TurfWars plugin;

    public PlayCMD(TurfWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        plugin.getGameManager().addToGame(player);
        return true;
    }
}