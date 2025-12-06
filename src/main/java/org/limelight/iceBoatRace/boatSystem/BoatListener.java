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

import static org.limelight.iceBoatRace.IceBoatRace.currentMap;
import static org.limelight.iceBoatRace.IceBoatRace.eventStatus;

public class BoatListener implements Listener {

    private final JavaPlugin plugin;
    private static NamespacedKey boatTypeKey;

    public BoatListener(JavaPlugin plugin){
        this.plugin = plugin;
        boatTypeKey = new NamespacedKey(plugin,"boatType");
    }

    //Doesn't allow the player to leave a boat while in game (might change this to never)
    @EventHandler
    public void VehicleLeaveEvent(VehicleExitEvent event){
        if (!eventStatus.equals("off")) event.setCancelled(true);
    }



    //Doesn't allow the player to enter a boat while in game (might change this to never)
    @EventHandler
    public void VehicleEnterEvent(VehicleEnterEvent event){
        if (eventStatus.equals("in_progress")) event.setCancelled(true);
    }

    //Removes the player from a race if they leave during a race
    @EventHandler
    public void PlayerLeave(PlayerQuitEvent event){
        if (!eventStatus.equals("off")){
            Player player = event.getPlayer();
            currentMap.players.remove(player);
            player.getPersistentDataContainer().set(new NamespacedKey(plugin,"playerLap"), PersistentDataType.INTEGER,0);
        }
    }
}
