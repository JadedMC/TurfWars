package net.jadedmc.turfwars.game.kit;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.kit.kits.InfiltratorKit;
import net.jadedmc.turfwars.game.kit.kits.MarksmanKit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KitManager {
    private final Map<String, Kit> kits = new HashMap<>();
    private final Map<Player, Kit> playerKits = new HashMap<>();

    public KitManager(TurfWars plugin) {
        kits.put("marksman", new MarksmanKit(plugin));
        kits.put("infiltrator", new InfiltratorKit(plugin));
    }

    public void addPlayer(Player player, Kit kit) {
        playerKits.put(player, kit);
    }

    public void removePlayer(Player player) {
        playerKits.remove(player);
    }

    public Kit getKit(String kit) {
        return kits.get(kit.toLowerCase());
    }

    public Kit getKit(Player player) {
        return playerKits.get(player);
    }
}
