package org.limelight.iceBoatRace.revivalSystem;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class revivalListener implements Listener {

    private final JavaPlugin plugin;
    private static Long delay = 60L;
    revivalListener(JavaPlugin plugin){
        this.plugin = plugin;
    }
    public boolean isFalling(Location location){
        World world = location.getWorld();

        return (
                location.offset(0,-1,0).toLocation(world).getBlock().getType().isAir() &&
                location.offset(0,-2,0).toLocation(world).getBlock().getType().isAir()
        );
    }

    @EventHandler
    public void VehicleMoveEvent(VehicleMoveEvent event){
        if (event.getVehicle() instanceof Boat boat){
            if (isFalling(event.getTo())){

                Location trackLocation = event.getFrom();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    boat.teleport(trackLocation);
                },delay);
            }
        }
    }
}
