package net.jadedmc.turfwars.game.kit.kits;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.Game;
import net.jadedmc.turfwars.game.kit.Kit;
import net.jadedmc.turfwars.utils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class MarksmanKit extends Kit {

    public MarksmanKit(TurfWars plugin) {
        super(plugin, "Marksman");
    }

    @Override
    public void applyKit(Player player, Game game) {
        ItemBuilder bow = new ItemBuilder(Material.BOW)
                .setDisplayName("&aMarksman's Bow")
                .addLore("&7Max Arrows: &a" + getMaxArrows())
                .addLore("&7Arrow Regen: &a" + getArrowRegenDelay())
                .addLore("")
                .addLore("&8&oUnrivaled in archery. One hit kills")
                .addLore("&8&oanyone.")
                .setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE);
        player.getInventory().setItem(0, bow.build());

        player.getInventory().setItem(7, new ItemStack(Material.ARROW, 2));

        ItemBuilder wool = new ItemBuilder(Material.WOOL, 8).dye(game.getTeam(player).getTeamColor().blockColor());
        player.getInventory().setItem(1, wool.build());

        //player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&aKit Selector").build());
    }
}
