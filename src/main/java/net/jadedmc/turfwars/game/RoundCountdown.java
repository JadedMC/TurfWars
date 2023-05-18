package net.jadedmc.turfwars.game;

import net.jadedmc.turfwars.TurfWars;
import org.bukkit.scheduler.BukkitRunnable;

public class RoundCountdown {
    private final TurfWars plugin;
    private final BukkitRunnable task;
    private int seconds;

    public RoundCountdown(TurfWars plugin) {
        this.plugin = plugin;

        seconds = 30;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(seconds == -5) {
                    stop();
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
    }

    public void cancel() {
        if(seconds == 30) {
            return;
        }

        task.cancel();
    }

    public String toString() {
        if(seconds >= 60 && seconds < 120) {
            return "1 Minute";
        }

        if(seconds >= 60) {
            return (seconds/60) + " Minutes";
        }

        return seconds + " Seconds";
    }
}