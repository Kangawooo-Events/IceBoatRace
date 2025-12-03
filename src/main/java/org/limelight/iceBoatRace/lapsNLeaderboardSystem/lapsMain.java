package org.limelight.iceBoatRace.lapsNLeaderboardSystem;
import org.limelight.iceBoatRace.boatSystem.boatListener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.List;

public class lapsMain implements Listener {
    Vector2f test_point_1 = new Vector2f(-2885.5f, 2188.5f);
    Vector2f test_point_2 = new Vector2f(-2903.5f, 2170.5f);

    HashMap<Player, Boolean> playerBehindLine = new HashMap<Player, Boolean>();

    private static NamespacedKey playerLap;
    private static JavaPlugin plugin;

    public lapsMain(JavaPlugin plugin){
        this.plugin = plugin;
        playerLap = new NamespacedKey(plugin,"playerLap");
    }

    // FUNCTIONS
    public static boolean isBehindLine(Vector2f point1, Vector2f point2, float gradient) {
        // Check if point1 is behind the line that sits on point2 with the included gradient
        return point1.y < gradient * (point1.x - point2.x) + point2.y;
    }

    @EventHandler @Deprecated
    public void VehicleMoveEvent(VehicleMoveEvent event) {
        // Ensure vehicle is a boat
        if (!(event.getVehicle() instanceof Boat boat)) return;

        //Ensure the boat has a passenger
        List<Entity> passengers = boat.getPassengers();
        if (passengers.isEmpty()) return;

        //Ensure the driver is a player
        if (!(passengers.getFirst() instanceof Player player)) return;

        float gradient = Math.abs(test_point_1.x - test_point_2.x) / Math.abs(test_point_1.y - test_point_2.y);
        Vector2f playerPos = new Vector2f((float) boat.getX(), (float) boat.getZ());
        boolean inBox = isBehindLine(playerPos, test_point_1, -(1/gradient)) && !isBehindLine(playerPos, test_point_2, -(1/gradient));


        boolean behindLine = isBehindLine(playerPos, test_point_1, gradient);

        // If the boat is not within rectangle perpendicular to the lap line, then do not continue
        if (inBox) {
            // If the boat has its data in the playerBehindLine continue
            if (playerBehindLine.get(player) != null) {
                boolean oldBehindLine = playerBehindLine.get(player);

                // If the boat used to be behind the lap line
                if (oldBehindLine) {

                    // If the boat is now in front of the lap line
                    if (!behindLine) {
                        PersistentDataContainer pdc = player.getPersistentDataContainer();

                        // If the player does not yet have their lap saved in their PDC, set the lap to 1
                        // Otherwise, get their lap from the playerLap key and increment it
                        int currentLap = 1;
                        if (pdc.get(playerLap, PersistentDataType.INTEGER) != null) {
                            currentLap = pdc.get(playerLap, PersistentDataType.INTEGER) + 1;
                        }

                        // Save players currentLap and announce new lap
                        pdc.set(playerLap, PersistentDataType.INTEGER, currentLap);
                        Bukkit.broadcastMessage(player.getName() + " Lap: " + currentLap);
                    }
                } else if (behindLine) {
                    boat.eject();
                    boat.remove();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> boatListener.spawnRacer(player, boat.getLocation()), 10);
                }
            }
        }
        // Update whether the player is behind the line or not in the playerBehindLine hashmap
        // (I am not using a PDC here since saving to a variable is more efficient
        // and the contents of behindLine variable does not need to be saved between restarts) - Kangawooo
        playerBehindLine.put(player, behindLine);
    }
}
