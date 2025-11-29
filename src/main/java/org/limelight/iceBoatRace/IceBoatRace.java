package org.limelight.iceBoatRace;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;


public final class IceBoatRace extends JavaPlugin implements Listener {
    record EventMap(String name,String difficulty,Location spawnLocation,Location[] finishLine) {}

    public static String eventStatus;
    public static List<EventMap> avalibleMaps;
    public static EventMap currentMap;



    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
