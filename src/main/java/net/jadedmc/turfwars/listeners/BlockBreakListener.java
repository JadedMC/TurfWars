package net.jadedmc.turfwars.listeners;

import com.cryptomorin.xseries.XBlock;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.game.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final TurfWars plugin;

    public BlockBreakListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        if(game.getGameState() == GameState.BUILD || game.getGameState() == GameState.FIGHT) {
            Team team = game.getTeam(player);

            if(event.getBlock().getType() == Material.WOOL && XBlock.getColor(event.getBlock()) == team.getTeamColor().blockColor()) {
                game.removeBlock(event.getBlock());
                return;
            }
        }

        event.setCancelled(true);
    }
}
