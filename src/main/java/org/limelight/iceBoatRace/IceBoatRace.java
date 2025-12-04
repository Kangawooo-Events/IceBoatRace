package org.limelight.iceBoatRace;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector2f;
import org.limelight.iceBoatRace.lapsNLeaderboardSystem.LapsHandler;
import org.limelight.iceBoatRace.mapVoteSystem.EndMapVoteCommand;
import org.limelight.iceBoatRace.mapVoteSystem.MapVoteCommand;
import org.limelight.iceBoatRace.mapVoteSystem.MapVoteInventory;

import java.util.List;


public final class IceBoatRace extends JavaPlugin implements Listener {
    record EventMap(String name, String difficulty, Location spawnLocation, Vector2f[] finishLine, World world) {}

    public static String eventStatus;
    public static List<EventMap> availableMaps;
    public static EventMap currentMap;

    @Override
    public void onEnable() {
        getCommand("startvote").setExecutor(new MapVoteCommand(this));
        getCommand("endvote").setExecutor(new EndMapVoteCommand());

        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new MapVoteInventory(this), this);
        Bukkit.getPluginManager().registerEvents(new LapsHandler(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
