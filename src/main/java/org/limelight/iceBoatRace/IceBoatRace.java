package org.limelight.iceBoatRace;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector2f;
import org.limelight.iceBoatRace.general.Debugger;
import org.limelight.iceBoatRace.lapsNLeaderboardSystem.LapsHandler;
import org.limelight.iceBoatRace.mapVoteSystem.MapVoteCommand;
import org.limelight.iceBoatRace.mapVoteSystem.MapVoteInventory;

import java.util.List;


public final class IceBoatRace extends JavaPlugin implements Listener {
    record EventMap(String name, String difficulty, Vector2f[] startLine, Vector2f[] finishLine, World world, Integer maxLaps) {}

    public static String eventStatus; // in_progress, voting
    public static List<EventMap> availableMaps;
    public static EventMap currentMap;

    @Override
    public void onEnable() {
        getCommand("startvote").setExecutor(new MapVoteCommand());
        getCommand("iceboat_debug").setExecutor(new Debugger());

        // TEMPORARY
        eventStatus = "in_progress";

        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new MapVoteInventory(), this);
        Bukkit.getPluginManager().registerEvents(new LapsHandler(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
