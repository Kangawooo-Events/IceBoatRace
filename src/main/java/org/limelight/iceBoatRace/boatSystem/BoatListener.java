package org.limelight.iceBoatRace.boatSystem;


import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.limelight.iceBoatRace.IceBoatRace;

import static org.limelight.iceBoatRace.IceBoatRace.*;

public class BoatListener implements Listener {


    private static NamespacedKey boatTypeKey = new NamespacedKey(plugin,"boatType");;


    /*
    //Doesn't allow the player to leave a boat while in game (might change this to never)
    @EventHandler
    public void VehicleLeaveEvent(VehicleExitEvent event){
        if (!(eventStatus == IceBoatRace.EventStatus.OFF)) event.setCancelled(true);
    }



    //Doesn't allow the player to enter a boat while in game (might change this to never)
    @EventHandler
    public void VehicleEnterEvent(VehicleEnterEvent event){
        if (!(eventStatus == IceBoatRace.EventStatus.IN_PROGRESS || eventStatus == IceBoatRace.EventStatus.VOTING)) event.setCancelled(true);
    }*/

    //Removes the player from a race if they leave during a race
    @EventHandler
    public void PlayerLeave(PlayerQuitEvent event){
        if (eventStatus == IceBoatRace.EventStatus.OFF) return;
        Player player = event.getPlayer();
        BoatHandler.despawnRacer(player,plugin);

    }
}
