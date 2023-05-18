package net.jadedmc.turfwars.commands;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCMD implements CommandExecutor {
    private final TurfWars plugin;

    public AdminCMD(TurfWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            return true;
        }

        Player player = (Player) sender;

        switch (args[0]) {
            case "forcestart":
                Game game = plugin.getGameManager().getGame(player);

                if(game == null) {
                    return true;
                }

                if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                    return true;
                }

                if(game.getPlayers().size() == 1) {
                    return true;
                }

                game.forceStartGame(player);
                break;
        }

        return true;
    }
}