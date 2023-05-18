package net.jadedmc.turfwars.listeners;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class ProjectileHitListener implements Listener {
    private final TurfWars plugin;

    public ProjectileHitListener(TurfWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if(!(arrow.getShooter() instanceof Player player)) {
            return;
        }

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        BlockIterator blockIterator = new BlockIterator(arrow.getLocation().getWorld(), arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();

            if(block.getType() == Material.WOOL) {
                block.setType(Material.AIR);
            }
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, arrow::remove, 5);
    }
}
