package net.jadedmc.turfwars.gui;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.GameState;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import net.jadedmc.turfwars.utils.gui.CustomGUI;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class SpectateGUI extends CustomGUI {

    public SpectateGUI(TurfWars plugin) {
        super(54, "Current Games");

        for(int i = 0; i < plugin.getGameManager().getActiveGames().size(); i++) {
            Game game = plugin.getGameManager().getActiveGames().get(i);


            ItemBuilder item = new ItemBuilder(Material.BOW)
                    .setDisplayName("&a" + game.getArena().getName())
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES);

            for(Player p : game.getPlayers()) {
                item.addLore("&7  - " + p.getName());
            }

            setItem(i, item.build(), (p, a) -> {
                p.closeInventory();
                if(game.getGameState() != GameState.BUILD && game.getGameState() != GameState.FIGHT) {
                    ChatUtils.chat(p, "&cError &8» &cThat match has ended.");
                    return;
                }

                game.addSpectator(p);
                game.sendMessage("&a" + p.getName() + " is now spectating.");
            });
        }
    }
}