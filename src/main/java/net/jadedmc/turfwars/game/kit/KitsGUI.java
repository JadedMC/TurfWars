package net.jadedmc.turfwars.game.kit;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import net.jadedmc.turfwars.utils.gui.CustomGUI;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitsGUI extends CustomGUI {

    public KitsGUI(TurfWars plugin) {
        super(45, "Kits");
        filler();

        ItemBuilder marksman = new ItemBuilder(Material.BOW)
                .setDisplayName("&a&lMarksman")
                .addLore("&7Unrivaled in archery. One hit kills anyone.")
                .addLore("")
                .addLore("&7Receive &a1 &7wool every &a4.0 &7seconds, Max &a8&7.")
                .addLore("&7Receive &a1 &7arrow every &a2.0 &7seconds, Max &a2&7.")
                .addLore("")
                .addLore("&a&lClick to Select");
        setItem(20, marksman.build(), (player,a) -> {
            plugin.getKitManager().addPlayer(player, plugin.getKitManager().getKit("marksman"));
            ChatUtils.chat(player, "<green>Kit set to <white>Marksman<green>.");

            Game game = plugin.getGameManager().getGame(player);

            if(game.getGameState() == GameState.BUILD || game.getGameState() == GameState.FIGHT) {
                player.getInventory().clear();
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                plugin.getKitManager().getKit(player).applyKit(player, game);
            }

            player.closeInventory();
        });

        ItemBuilder infiltrator = new ItemBuilder(Material.IRON_SWORD)
                .setDisplayName("&a&lInfiltrator")
                .addLore("&7Able to travel onto enemy turf, but you")
                .addLore("&7must return to your turf fast, or receive")
                .addLore("&7Slowness.")
                .addLore("")
                .addLore("&7Receive &a1 &7wool every &a4.0 &7seconds, Max &a4&7.")
                .addLore("&7Receive &a1 &7arrow every &a8.0 &7seconds, Max &a1&7.")
                .addLore("")
                .addLore("&a&lClick to Select")
                .addFlag(ItemFlag.HIDE_ATTRIBUTES);
        setItem(22, infiltrator.build(), (player,a) -> {
            plugin.getKitManager().addPlayer(player, plugin.getKitManager().getKit("infiltrator"));
            ChatUtils.chat(player, "<green>Kit set to <white>Infiltrator<green>.");

            Game game = plugin.getGameManager().getGame(player);

            if(game.getGameState() == GameState.BUILD || game.getGameState() == GameState.FIGHT) {
                player.getInventory().clear();
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                plugin.getKitManager().getKit(player).applyKit(player, game);
            }

            player.closeInventory();
        });
    }

    private void filler() {
        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData());
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);

        int[] fillerSlots = new int[]{0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};

        for(int slot : fillerSlots) {
            setItem(slot, filler);
        }
    }
}