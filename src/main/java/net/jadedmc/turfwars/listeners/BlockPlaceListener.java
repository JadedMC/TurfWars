package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.game.team.Team;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    private final TurfWars plugin;

    public BlockPlaceListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        if(game.getGameState() == GameState.BUILD || game.getGameState() == GameState.FIGHT) {
            Team team = game.getTeam(player);

            if(!team.getArenaTeam().isInBounds(event.getBlock().getLocation())) {
                ChatUtils.chat(player, "<red> You can only build on your own turf!");
            }
            else {
                boolean aboveTurf = false;

                Block block = event.getBlock();

                for(int i = 0; i < 5; i++) {
                   block = block.getRelative(BlockFace.DOWN);
                   if(block.getType() == Material.STAINED_CLAY) {
                       aboveTurf = true;
                       break;
                   }
                }

                if(aboveTurf) {
                    plugin.getKitManager().getKit(player).regenWool(player, game);
                    game.addBlock(event.getBlock());
                    return;
                }

                ChatUtils.chat(player, "<red>You cannot build that high above your turf!");
            }
        }

        event.setCancelled(true);
    }
}
