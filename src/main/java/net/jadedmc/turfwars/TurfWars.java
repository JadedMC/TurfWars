package net.jadedmc.turfwars;

import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedchat.features.channels.channel.ChatChannel;
import net.jadedmc.jadedchat.features.channels.channel.ChatChannelBuilder;
import net.jadedmc.jadedchat.features.channels.fomat.ChatFormatBuilder;
import net.jadedmc.turfwars.commands.*;
import net.jadedmc.turfwars.game.DuelManager;
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
    private DuelManager duelManager;

    private KitManager kitManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Initialize an audiences instance for the plugin
        this.adventure = BukkitAudiences.create(this);
        new ChatUtils(this);

        settingsManager = new SettingsManager(this);
        arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        gameManager = new GameManager(this);
        kitManager = new KitManager(this);
        duelManager = new DuelManager(this);

        getCommand("admin").setExecutor(new AdminCMD(this));
        getCommand("arena").setExecutor(new ArenaCMD(this));
        getCommand("play").setExecutor(new PlayCMD(this));
        getCommand("spectate").setExecutor(new SpectateCMD(this));
        getCommand("duel").setExecutor(new DuelCMD(this));

        getServer().getPluginManager().registerEvents(new ChannelMessageSendListener(this), this);
        getServer().getPluginManager().registerEvents(new ChannelSwitchListener(this), this);
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

        // Generate game channel.
        if(!JadedChat.channelExists("GAME")) {
            ChatChannel gameChannel = new ChatChannelBuilder("GAME")
                    .setDisplayName("<green>GAME</green>")
                    .addChatFormat(new ChatFormatBuilder("default")
                            .addSection("team", "%tw_team_prefix% ")
                            .addSection("prefix", "%luckperms_prefix%")
                            .addSection("player", "<gray>%player_name%")
                            .addSection("seperator", "<dark_gray> » ")
                            .addSection("message", "<gray><message>")
                            .build())
                    .build();
            gameChannel.saveToFile("game.yml");
            JadedChat.loadChannel(gameChannel);
        }
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
    public ArenaManager arenaManager() {
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

    public DuelManager duelManager() {
        return duelManager;
    }
}