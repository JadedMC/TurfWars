package net.jadedmc.turfwars.gui;

import com.cryptomorin.xseries.XMaterial;
import net.jadedmc.jadedchat.utils.ChatUtils;
import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import net.jadedmc.turfwars.utils.gui.CustomGUI;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Manages the DuelGUI, which allows a player to duel another.
 */
public class DuelGUI extends CustomGUI {

    /**
     * Creates the main duel GUI.
     * @param plugin Instance of the plugin.
     * @param player Player sending the duel request.
     * @param target Target of the duel request.
     */
    public DuelGUI(final TurfWars plugin, final Player player, final Player target) {
        super(45, "Duel " + target.getName());

        ItemStack filler = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setDisplayName(" ").build();
        int[] fillers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int i : fillers) {
            setItem(i, filler);
        }

        ItemStack randomMap = new ItemBuilder(Material.FIREWORK)
                .setDisplayName("&a&lRandom Map")
                .build();
        setItem(31, randomMap, (p, a) -> {
            if (target == null) {
                ChatUtils.chat(player, "&cError &8» &cThat player is not online!");
                player.closeInventory();
                return;
            }

            if (plugin.getGameManager().getGame(target) != null) {
                ChatUtils.chat(player, "&cError &8» &c/That player is currently in a game!");
                player.closeInventory();
                return;
            }

            plugin.duelManager().addDuelRequest(player, target, "random");
            player.closeInventory();
        });

        int slot = 9;
        for (Arena arena : plugin.arenaManager().getArenas()) {
            ItemBuilder builder = new ItemBuilder(Material.PAPER).setDisplayName("&a" + arena.name());
            setItem(slot, builder.build(), (p, a) -> {
                if (target == null) {
                    ChatUtils.chat(player, "&cError &8» &cThat player is not online!");
                    player.closeInventory();
                    return;
                }

                if (plugin.getGameManager().getGame(target) != null) {
                    ChatUtils.chat(player, "&cError &8» &c/That player is currently in a game!");
                    player.closeInventory();
                    return;
                }

                plugin.duelManager().addDuelRequest(player, target, arena.id());
                player.closeInventory();
            });
            slot++;
        }
    }
}