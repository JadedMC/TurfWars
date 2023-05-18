package net.jadedmc.turfwars.game;

import com.cryptomorin.xseries.XSound;
import net.jadedmc.turfwars.TurfWars;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdown {
    private final TurfWars plugin;
    private final BukkitRunnable task;
    private int seconds;
    private final Game game;

    public GameCountdown(TurfWars plugin, Game game) {
        this.plugin = plugin;
        this.game = game;

        seconds = 30;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(seconds == 0) {
                    stop();
                }

                if(seconds <= 5 && seconds > 0) {
                    game.sendMessage("<green>Game is starting in <white>" + seconds + "s<green>.");

                    for (Player player : game.getPlayers()) {
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }
                }

                seconds--;
            }
        };
    }

    /**
     * Get the seconds left in the countdown.
     * @return Seconds left in the countdown.
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Start the timer.
     */
    public void start() {
        task.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Set the amount of seconds on the countdown.
     * @param seconds Seconds to set.
     */
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * Stop the timer.
     */
    public void stop() {
        task.cancel();
        game.startGame();
    }

    public void cancel() {
        if(seconds == 30) {
            return;
        }

        task.cancel();
    }
}