package org.limelight.iceBoatRace.lapsNLeaderboardSystem;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.limelight.iceBoatRace.IceBoatRace;
import org.limelight.iceBoatRace.general.MessageHandler;
import org.limelight.iceBoatRace.general.Line;
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

public class LapsHandler implements Listener {
    Vector2f point1 = new Vector2f(-2885.5f, 2188.5f);
    Vector2f point2 = new Vector2f(-2903.5f, 2170.5f);
    Line finishLine = new Line(point1, point2);

    HashMap<Player, Boolean> playerBehindLine = new HashMap<Player, Boolean>();

    private static NamespacedKey playerLap;
    private static JavaPlugin plugin;

    public LapsHandler(JavaPlugin plugin){
        this.plugin = plugin;
        playerLap = new NamespacedKey(plugin,"playerLap");
    }

    // FUNCTIONS
    public static ArrayList<Location> getSpawnLocations(World world, Vector2f point1, Vector2f point2, float spacing, int numPlayers) {
        Vector2f lineVector = Line.getVector(point1, point2);
        float lineMagnitude = Line.getMagnitude(lineVector.x, lineVector.y);
        Vector2f lineUnitVector = new Vector2f(lineVector.x / lineMagnitude, lineVector.y / lineMagnitude);
        Vector2f normalUnitVector = new Line(point1, point2).getNormal(world);

        float sideSpacing = (((lineMagnitude - (spacing / 2)) % spacing) + spacing / 2) / 2;
        Vector2f sideVector = new Vector2f(lineUnitVector.x * sideSpacing, lineUnitVector.y * sideSpacing);
        int columns = (int) ((lineMagnitude - sideSpacing) / spacing);

        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i <= numPlayers-1; i++) {
            int column = (i % (columns + 1));
            int row = (i / (columns + 1));

            // If it's the last row and there are fewer players left then there should be number of columns, add some spacing so that the last row is centered
            if (column == 0 && numPlayers - i < columns) {
                sideSpacing += ((columns - (numPlayers - i) + 1) * spacing) / 2;
                sideVector = new Vector2f(lineUnitVector.x * sideSpacing, lineUnitVector.y * sideSpacing);
            }

            float rowX = sideVector.x + (column * spacing * lineUnitVector.x);
            float rowZ = sideVector.y + (column * spacing * lineUnitVector.y);
            double columnX = (row * spacing * normalUnitVector.x);
            double columnZ = (row * spacing * normalUnitVector.y);

            double x = rowX + columnX + point2.x;
            double z = rowZ + columnZ + point2.y;
            int y = world.getHighestBlockYAt((int) x, (int) z) + 1;

            Location position = new Location(world, x, y, z);
            locations.add(position);
        }

        return locations;
    }

    // EVENTS
    @EventHandler
    public void VehicleMoveEvent(VehicleMoveEvent event) {
        // Only run if race is in progress
        if (!(IceBoatRace.eventStatus.matches("in_progress"))) return;

        // Ensure vehicle is a boat
        if (!(event.getVehicle() instanceof Boat boat)) return;

        //Ensure the boat has a passenger
        List<Entity> passengers = boat.getPassengers();
        if (passengers.isEmpty()) return;

        //Ensure the driver is a player
        if (!(passengers.getFirst() instanceof Player player)) return;

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

                        // Send actionbar and play sound to player to alert them that they have passed a lap
                        String message = "§aLap: " + currentLap;
                        MessageHandler.sendActionbar(player, message);
                        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                        // Save players currentLap and announce new lap
                        pdc.set(playerLap, PersistentDataType.INTEGER, currentLap);
                    }
                } else if (behindLine) { // The boat has just passed the lap line in the opposite direction
                    // Get the spawn location for a new boat
                    // The new spawn location is the current location of the boat with the normal of the lap line added to it
                    World world = player.getWorld();
                    Vector2f normal = finishLine.getNormal(world);
                    float yaw = Line.getAngle(normal.y, normal.x);
                    Location spawnLocation = new Location(world, boat.getLocation().x() - normal.x, boat.getLocation().y(), boat.getLocation().z() - normal.y, yaw, 0);

                    // Remove the old boat
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

                    // Set behindLine to false since the boat should now be behind the line again
                    behindLine = false;
                }
            }
        }
        // Update whether the player is behind the line or not in the playerBehindLine hashmap
        // (I am not using a PDC here since saving to a variable is more efficient
        // and the contents of behindLine variable does not need to be saved between restarts) - Kangawooo
        playerBehindLine.put(player, behindLine);
    }
}
