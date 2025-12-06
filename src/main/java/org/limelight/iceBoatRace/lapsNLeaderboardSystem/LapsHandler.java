package org.limelight.iceBoatRace.lapsNLeaderboardSystem;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.limelight.iceBoatRace.IceBoatRace;
import org.limelight.iceBoatRace.general.MessageHandler;
import org.limelight.iceBoatRace.objectClasses.Line;
import org.limelight.iceBoatRace.boatSystem.BoatHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.limelight.iceBoatRace.IceBoatRace.currentMap;
import static org.limelight.iceBoatRace.IceBoatRace.eventStatus;

public class LapsHandler implements Listener {
    public static Vector2f point1 = new Vector2f(-2885.5f, 2188.5f);
    public static Vector2f point2 = new Vector2f(-2903.5f, 2170.5f);
    public static Line finishLine = new Line(point1, point2);
    public static int maxLaps = 3;
    String finishMessage = ChatColor.GOLD +"FINISH!";
    String totalFinishMessage = ChatColor.GOLD +"RACE FINISH!";

    HashMap<Player, Boolean> playerBehindLine = new HashMap<Player, Boolean>();

    private static NamespacedKey playerLap;
    private static JavaPlugin plugin;

    public LapsHandler(JavaPlugin plugin){
        this.plugin = plugin;
        playerLap = new NamespacedKey(plugin,"playerLap");
    }

    // FUNCTIONS


    // EVENTS
    @EventHandler
    public void VehicleMoveEvent(VehicleMoveEvent event) {
        // Only run if race is in progress
        if ((eventStatus == IceBoatRace.EventStatus.OFF)) return;

        // Ensure vehicle is a boat
        if (!(event.getVehicle() instanceof Boat boat)) return;

        //Ensure the boat has a passenger
        List<Entity> passengers = boat.getPassengers();
        if (passengers.isEmpty()) return;

        //Ensure the driver is a player
        if (!(passengers.getFirst() instanceof Player player)) return;

        switch(eventStatus){
            //Goes into async for calculations
            case IceBoatRace.EventStatus.IN_PROGRESS -> Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
                float gradient = -(1/finishLine.getGradient());
                Line line1 = new Line(point1, gradient);
                Line line2 = new Line(point2, gradient);
                Vector2f playerPos = new Vector2f((float) boat.getX(), (float) boat.getZ());
                boolean inBox = line1.isBehindLine(playerPos) && !line2.isBehindLine(playerPos);


                /*
                inBox checks if the player is within 2 lines that go from point1 and point2 perpendicular to the finishLine,
                infinitely in either direction. Essentially checking if the player is within a rectangle of infinite width and
                length which is the magnitude of the line between point1 and point2

                --------------------------------
                               |  ^ line1
                Finish line -> |
                               |
                               |  v line2
                --------------------------------
                 */


                boolean behindLine = finishLine.isBehindLine(playerPos);

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

                                if (currentLap >= maxLaps){
                                    // Reset player currentLap and remove them from the race
                                    pdc.set(playerLap, PersistentDataType.INTEGER, 0);
                                    currentMap.players.remove(player);

                                    //Award the points to the players*-----+
                                    
                                    LeaderboardMain.awardPoints(player,LeaderboardMain.getAwardedPoints());

                                    //If no players left in the race then announce to all the race has finished
                                    if (currentMap.players.isEmpty()){
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            BoatHandler.despawnRacer(player);
                                            for (Player player1 : Bukkit.getOnlinePlayers()) {
                                                MessageHandler.sendTitlebar(player1,"",totalFinishMessage);
                                                player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 29);
                                            }
                                        });
                                    }
                                    //If there are players remaining
                                    else {
                                        // Send actionbar in sync and play sound to player to alert them that they have finished the race
                                        Bukkit.getScheduler().runTask(plugin, () -> {

                                            BoatHandler.despawnRacer(player);
                                            MessageHandler.sendTitlebar(player, "", finishMessage);
                                            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);

                                        });
                                    }

                                }
                                //If the players still have laps remaining
                                else {
                                    // Save players currentLap and announce new lap
                                    pdc.set(playerLap, PersistentDataType.INTEGER, currentLap);

                                    // Send actionbar in sync and play sound to player to alert them that they have passed a lap
                                    String message = "§aLap: " + currentLap;
                                    Bukkit.getScheduler().runTask(plugin, () -> {
                                        MessageHandler.sendActionbar(player, message);
                                        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    });
                                }



                            }
                        }

                        // The boat has just passed the lap line in the opposite direction
                        else if (behindLine) {

                            // Get the spawn location for a new boat
                            // The new spawn location is the current location of the boat with the normal of the lap line added to it
                            World world = player.getWorld();
                            Vector2f normal = finishLine.getNormal(world);
                            float yaw = Line.getAngle(normal.y, normal.x);
                            Location spawnLocation = new Location(world, boat.getLocation().x() - normal.x, boat.getLocation().y(), boat.getLocation().z() - normal.y, yaw, 0);


                            // Set behindLine to false since the boat should now be behind the line again
                            behindLine = false;

                            Bukkit.getScheduler().runTask(plugin,()-> {
                                // Remove the old boat in sync
                                boat.eject();
                                boat.remove();

                                // Spawn a new boat and place the player in it one tick later
                                // Run 1 tick later since player cannot be ejected and then seated again in the same tick
                                Bukkit.getScheduler().runTaskLater(plugin, () -> BoatHandler.spawnRacer(player, plugin, spawnLocation), 1);

                                // Play a sound effect and actionbar to alert the player that they cannot go this way.
                                // Run 2 ticks later to overwrite built in Minecraft actionbar "Press Left Shift to Dismount" caused by new boat spawning
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    String message = "§cCan't pass lap backwards!";
                                    MessageHandler.sendActionbar(player, message);
                                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                                }, 2);
                            });

                        }
                    }
                }

                // Update whether the player is behind the line or not in the playerBehindLine hashmap
                // (I am not using a PDC here since saving to a variable is more efficient
                // and the contents of behindLine variable does not need to be saved between restarts) - Kangawooo
                playerBehindLine.put(player, behindLine);
            });
            case IceBoatRace.EventStatus.COUNTDOWN -> {
                // Remove the old boat
                Location spawnLocation = boat.getLocation();
                boat.eject();
                boat.remove();

                // Spawn a new boat and place the player in it one tick later
                // Run 1 tick later since player cannot be ejected and then seated again in the same tick
                Bukkit.getScheduler().runTaskLater(plugin, () -> BoatHandler.spawnRacer(player, plugin, spawnLocation), 1);
            }
        }




    }
}
