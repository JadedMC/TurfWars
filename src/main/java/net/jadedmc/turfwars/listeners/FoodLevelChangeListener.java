package net.jadedmc.turfwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onEvent(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

}