package net.jadedmc.turfwars;

import net.jadedmc.turfwars.commands.AdminCMD;
import net.jadedmc.turfwars.commands.ArenaCMD;
import net.jadedmc.turfwars.commands.PlayCMD;
import net.jadedmc.turfwars.game.GameManager;
import net.jadedmc.turfwars.game.arena.ArenaManager;
import net.jadedmc.turfwars.game.kit.KitManager;
import net.jadedmc.turfwars.listeners.*;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import net.jadedmc.turfwars.utils.gui.GUIListeners;
import net.jadedmc.turfwars.utils.scoreboard.ScoreboardUpdate;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class TurfWars extends JavaPlugin {
    private ArenaManager arenaManager;
    private BukkitAudiences adventure;
    private SettingsManager settingsManager;
    private GameManager gameManager;

    private KitManager kitManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Initialize an audiences instance for the plugin
        this.adventure = BukkitAudiences.create(this);
        new ChatUtils(this);

        settingsManager = new SettingsManager(this);
        arenaManager = new ArenaManager(this);
        gameManager = new GameManager(this);
        kitManager = new KitManager(this);

        getCommand("admin").setExecutor(new AdminCMD(this));
        getCommand("arena").setExecutor(new ArenaCMD(this));
        getCommand("play").setExecutor(new PlayCMD(this));

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityShootBowListener(this), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);

        new ScoreboardUpdate(this).runTaskTimer(this, 20L, 20L);

        new Placeholders(this).register();
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    /**
     * Retrieves the object managing arenas.
     * @return Arena Manager.
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }
}