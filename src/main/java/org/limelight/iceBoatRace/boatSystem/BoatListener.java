package org.limelight.iceBoatRace.boatSystem;


import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static org.limelight.iceBoatRace.IceBoatRace.eventStatus;

public class BoatListener implements Listener {

    private final JavaPlugin plugin;
    private static NamespacedKey boatTypeKey;

    BoatListener(JavaPlugin plugin){
        this.plugin = plugin;
        boatTypeKey = new NamespacedKey(plugin,"boatType");
    }

    //Doesn't allow the player to leave a boat while in game (might change this to never)
    @EventHandler
    public void VehicleLeaveEvent(VehicleExitEvent event){
        if (eventStatus.equals("in_progress")){
            event.setCancelled(true);
        }
    }

    //Doesn't allow the player to enter a boat while in game (might change this to never)
    @EventHandler
    public void VehicleEnterEvent(VehicleEnterEvent event){
        if (eventStatus.equals("in_progress")){
            event.setCancelled(true);
        }
    }
}
