package net.jadedmc.turfwars.game.kit;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

public abstract class Kit {
    private final TurfWars plugin;
    private final Collection<Player> arrowRegen = new HashSet<>();
    private final Collection<Player> woolRegen = new HashSet<>();

    private final String name;
    private int maxArrows = 2;
    private int arrowRegenDelay = 2;
    private int maxWool = 8;
    private int woolRegenDelay = 6;

    public Kit(TurfWars plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract void applyKit(Player player, Game game);

    public int getArrowRegenDelay() {
        return arrowRegenDelay;
    }

    public int getMaxArrows() {
        return maxArrows;
    }

    public int getMaxWool() {
        return maxWool;
    }

    public int getWoolRegenDelay() {
        return woolRegenDelay;
    }

    public void regenArrow(Player player, Game game) {
        if(arrowRegen.contains(player)) {
            return;
        }

        arrowRegen.add(player);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(game == null || game.getGameState() == GameState.END) {
                return;
            }

            if(player.getInventory().contains(Material.ARROW, maxArrows)) {
                return;
            }

            if(player.getInventory().first(Material.ARROW) < 0) {
                player.getInventory().setItem(8, new ItemStack(Material.ARROW));
                return;
            }

            arrowRegen.remove(player);

            player.getInventory().addItem(new ItemStack(Material.ARROW));

            if(!player.getInventory().contains(Material.ARROW, maxArrows)) {
                regenArrow(player, game);
            }
        }, arrowRegenDelay*20);
    }

    public void regenWool(Player player, Game game) {
        if(woolRegen.contains(player)) {
            return;
        }

        woolRegen.add(player);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(game == null || game.getGameState() == GameState.END) {
                return;
            }

            if(player.getInventory().contains(Material.WOOL, maxWool)) {
                return;
            }

            woolRegen.remove(player);

            ItemBuilder wool = new ItemBuilder(Material.WOOL, 1).dye(game.getTeam(player).getTeamColor().blockColor());
            player.getInventory().addItem(wool.build());

            if(!player.getInventory().contains(Material.WOOL, maxWool)) {
                regenWool(player, game);
            }
        }, woolRegenDelay*20);
    }

    public void setArrowRegenDelay(int arrowRegenDelay) {
        this.arrowRegenDelay = arrowRegenDelay;
    }

    public void setWoolRegenDelay(int woolRegenDelay) {
        this.woolRegenDelay = woolRegenDelay;
    }

    public void setMaxArrows(int maxArrows) {
        this.maxArrows = maxArrows;
    }

    public void setMaxWool(int maxWool) {
        this.maxWool = maxWool;
    }
}