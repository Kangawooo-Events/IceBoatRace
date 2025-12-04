package org.limelight.iceBoatRace.boatSystem;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.limelight.iceBoatRace.IceBoatRace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.limelight.iceBoatRace.IceBoatRace.eventStatus;

public class boatListener implements Listener {

    private final JavaPlugin plugin;
    private static NamespacedKey boatTypeKey;

    boatListener(JavaPlugin plugin){
        this.plugin = plugin;
        boatTypeKey = new NamespacedKey(plugin,"boatType");
    }

    //A set of all defined boatTypes - used to see if a string is a valid boat type
    public static final Set<String> BoatTypes = new HashSet<>(Arrays.asList( new String[] {
            "oak_boat",
            "spruce_boat",
            "birch_boat",
            "jungle_boat",
            "acacia_boat",
            "dark_oak_boat",
            "mangrove_boat",
            "cherry_boat",
            "pale_oak_boat",
            "bamboo_raft"
    }));

    //Gets the EntityType based on the string stored in NamespacedKey(plugin,"boat")
    public static EntityType getBoatEntity(String boatType){
        return switch(boatType){
            case "oak_boat" -> EntityType.OAK_BOAT;
            case "spruce_boat"-> EntityType.SPRUCE_BOAT;
            case "birch_boat"-> EntityType.BIRCH_BOAT;
            case "jungle_boat"-> EntityType.JUNGLE_BOAT;
            case "acacia_boat"-> EntityType.ACACIA_BOAT;
            case "dark_oak_boat"-> EntityType.DARK_OAK_BOAT;
            case "mangrove_boat"-> EntityType.MANGROVE_BOAT;
            case "cherry_boat"-> EntityType.CHERRY_BOAT;
            case "pale_oak_boat"-> EntityType.PALE_OAK_BOAT;
            case "bamboo_raft" -> EntityType.BAMBOO_RAFT;
            default -> null;
        };
    }

    //Gets the Material based on the string stored in NamespacedKey(plugin,"boat") (could be useful for the voting system)
    public static Material getBoatMaterial(String boatType){
        return switch(boatType){
            case "oak_boat" -> Material.OAK_BOAT;
            case "spruce_boat"-> Material.SPRUCE_BOAT;
            case "birch_boat"-> Material.BIRCH_BOAT;
            case "jungle_boat"-> Material.JUNGLE_BOAT;
            case "acacia_boat"-> Material.ACACIA_BOAT;
            case "dark_oak_boat"-> Material.DARK_OAK_BOAT;
            case "mangrove_boat"-> Material.MANGROVE_BOAT;
            case "cherry_boat"-> Material.CHERRY_BOAT;
            case "pale_oak_boat"-> Material.PALE_OAK_BOAT;
            case "bamboo_raft" -> Material.BAMBOO_RAFT;
            default -> null;
        };
    }

    //Gets the players boatType as an EntityType stored in NamespacedKey(plugin,"boat")
    public static @NotNull EntityType getRacerBoatEntity(Player player,NamespacedKey key){
        PersistentDataContainer data = player.getPersistentDataContainer();

        //Checks if there's no boat stored in the players persistent data container and replaces it with the default oak boat
        if (!data.has(key,PersistentDataType.STRING)){
            data.set(key,PersistentDataType.STRING,"oak_boat");
        }

        //Checks if there's an invalid boat stored in the players persistent data container and replaces it with the default oak boat
        else if (BoatTypes.contains(data.get(key,PersistentDataType.STRING))){
            data.set(key,PersistentDataType.STRING,"oak_boat");
        }

        String boatType = data.get(key,PersistentDataType.STRING);

        assert boatType != null;
        return Objects.requireNonNull(getBoatEntity(boatType));
    }

    //Validates and changes the players boatType stored in the players persistent data container with the key NamespacedKey(plugin,"boat")
    public static void changePlayerBoat(Player player,String boat,NamespacedKey key){
        if (BoatTypes.contains(boat)){
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING,boat);
        }
    }

    //Put the player in adventure and teleport them to inside a boat at that location
    public static void spawnRacer(Player player, Location location,NamespacedKey key){
        player.setGameMode(GameMode.ADVENTURE);
        Vehicle boat = (Vehicle) location.getWorld().spawnEntity(location,getRacerBoatEntity(player,key));
        boat.addPassenger(player);
    }

    //Put the player in adventure and teleport them to inside a boat at that location
    public void respawnRacer(Player player,NamespacedKey key){
        Entity oldBoat = player.getVehicle();
        if (oldBoat != null) {
            Location location = oldBoat.getLocation();
            oldBoat.remove();
            spawnRacer(player,location,key);
        }
    }

    //Puts the player in spectator mode and destroy their vehicle after the last lap
    public void despawnRacer(Player player){
        Entity oldBoat = player.getVehicle();
        if (oldBoat != null){
            oldBoat.remove();
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    //Doesn't allow the player to leave a boat while in game (might change this to never)
    @EventHandler
    public void VehicleLeaveEvent(VehicleExitEvent event){
        if (eventStatus.equals("ingame")){
            event.setCancelled(true);
        }
    }

    //Doesn't allow the player to enter a boat while in game (might change this to never)
    @EventHandler
    public void VehicleLeaveEvent(VehicleEnterEvent event){
        if (eventStatus.equals("ingame")){
            event.setCancelled(true);
        }
    }
}
