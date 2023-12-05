package net.jadedmc.turfwars.game.kit.kits;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.kit.Kit;
import net.jadedmc.turfwars.game.team.Team;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;

public class InfiltratorKit extends Kit {
    private final Collection<Player> slownessCooldown = new HashSet<>();

    public InfiltratorKit(TurfWars plugin) {
        super(plugin, "Infiltrator");
        setArrowRegenDelay(8);
        setMaxArrows(1);
        setMaxWool(4);
    }

    @Override
    public void applyKit(Player player, Game game) {
        ItemBuilder sword = new ItemBuilder(Material.IRON_SWORD)
                .setDisplayName("&aInfiltrator's Sword")
                .setUnbreakable(true)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE);
        player.getInventory().setItem(0, sword.build());

        ItemBuilder bow = new ItemBuilder(Material.BOW)
                .setDisplayName("&aInfiltrator's Bow")
                .addLore("&7Max Arrows: &a" + getMaxArrows())
                .addLore("&7Arrow Regen: &a" + getArrowRegenDelay())
                .setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE);
        player.getInventory().setItem(1, bow.build());

        player.getInventory().setItem(7, new ItemStack(Material.ARROW, 1));

        ItemBuilder wool = new ItemBuilder(Material.WOOL, 8).dye(game.getTeam(player).getTeamColor().blockColor());
        player.getInventory().setItem(2, wool.build());

        player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&aKit Selector").build());
    }

    public void processSlowness(TurfWars plugin, Player player, Game game) {
        Team team = game.getTeam(player);

        // If the player is in their own turf, remove the slowness effect.
        if(team.isInBounds(player.getLocation())) {
            slownessCooldown.remove(player);
            player.removePotionEffect(PotionEffectType.SLOW);
            return;
        }

        if(!game.getOpposingTeam(player).isInBounds(player.getLocation())) {
            return;
        }

        // Ignore if there is a cool down.
        if(slownessCooldown.contains(player)) {
            return;
        }

        // Add the player to the cool down.
        slownessCooldown.add(player);

        // Give the player Slowness 1 if they do not have slowness.
        if(!player.hasPotionEffect(PotionEffectType.SLOW)) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if(!slownessCooldown.contains(player)) {
                    return;
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 0));
                slownessCooldown.remove(player);
            }, 100);

            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            // Ignore if the player no longer is in the cool down.
            if(!slownessCooldown.contains(player)) {
                return;
            }

            int amplifier = 0;
            for(PotionEffect effect : player.getActivePotionEffects()) {
                if(effect.getAmplifier() > amplifier) {
                    amplifier = effect.getAmplifier();
                }
            }

            player.removePotionEffect(PotionEffectType.SLOW);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, amplifier + 1));
            slownessCooldown.remove(player);
        }, 100);
    }
}