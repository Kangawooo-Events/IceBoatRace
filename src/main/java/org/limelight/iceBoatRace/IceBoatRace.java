package org.limelight.iceBoatRace;

import me.lucko.spark.paper.proto.SparkProtos;
import org.bukkit.Bukkit;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector2f;
import org.limelight.iceBoatRace.boatSystem.BoatListener;
import org.limelight.iceBoatRace.boatSystem.scb;
import org.limelight.iceBoatRace.general.Debugger;
import org.limelight.iceBoatRace.lapsNLeaderboardSystem.LapsHandler;
import org.limelight.iceBoatRace.mapVoteSystem.EndMapVoteCommand;
import org.limelight.iceBoatRace.mapVoteSystem.MapVoteCommand;
import org.limelight.iceBoatRace.mapVoteSystem.MapVoteInventory;
import org.limelight.iceBoatRace.objectClasses.EventMap;
import org.limelight.iceBoatRace.objectClasses.Line;


import java.util.HashMap;
import java.util.Map;


public final class IceBoatRace extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;

    public enum EventStatus {
        OFF,
        VOTING,
        COUNTDOWN,
        IN_PROGRESS
    }

    public static EventStatus eventStatus ;
    public static Map<String,EventMap> availableMaps = new HashMap<>();
    public static EventMap currentMap;

    public void setupMaps(){
        /*
        availableMaps.put("Kangawooorld",new EventMap("Kangawooorld",new Vector2f[]{new Vector2f(),new Vector2f()}, new Vector2f[]{new Vector2f(),new Vector2f()},Bukkit.getWorld("KEventsBuildWorld"),3,3));
        availableMaps.put("Wild West",new EventMap("Wild West",new Vector2f[]{new Vector2f(),new Vector2f()}, new Vector2f[]{new Vector2f(),new Vector2f()},Bukkit.getWorld("KEventsBuildWorld"),3,3));
        availableMaps.put("Winter Wonderland",new EventMap("Winter Wonderland",new Vector2f[]{new Vector2f(),new Vector2f()}, new Vector2f[]{new Vector2f(),new Vector2f()},Bukkit.getWorld("KEventsBuildWorld"),3,3));
        availableMaps.put("Medieval Castle",new EventMap("Medieval Castle",new Vector2f[]{new Vector2f(),new Vector2f()}, new Vector2f[]{new Vector2f(),new Vector2f()},Bukkit.getWorld("KEventsBuildWorld"),3,3));
        availableMaps.put("Rainbow Road",new EventMap("Rainbow Road",new Vector2f[]{new Vector2f(),new Vector2f()}, new Vector2f[]{new Vector2f(),new Vector2f()},Bukkit.getWorld("KEventsBuildWorld"),3,3));
        */

        //availableMaps.put("Circle",new EventMap("Circle",new Vector2f[]{new Vector2f(-2908f,2172f),new Vector2f(-2902f,2195f)}, new Vector2f[]{new Vector2f(-2885.5f, 2188.5f),new Vector2f(-2903.5f, 2170.5f)},Bukkit.getWorld("KEventsBuildWorld"),3,3,this));
        availableMaps.put("Circle",new EventMap("Circle",new Vector2f[]{new Vector2f(-2626f,1444f),new Vector2f(-2626f,1426f)}, new Vector2f[]{new Vector2f(-2630f,1444f),new Vector2f(-2630f,1426f)},Bukkit.getWorld("KEventsBuildWorld"),3,3,this));
    }

    @Override
    public void onEnable() {
        setupMaps();
        getCommand("votestart").setExecutor(new MapVoteCommand(this));
        getCommand("iceboat_debug").setExecutor(new Debugger());
        getCommand("voteend").setExecutor(new EndMapVoteCommand());

        getCommand("iceboat_debug").setTabCompleter(new Debugger());


        plugin = this;

        // TEMPORARY
        eventStatus = EventStatus.OFF;


        getServer().getPluginManager().registerEvents(new BoatListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new MapVoteInventory(), this);
        Bukkit.getPluginManager().registerEvents(new LapsHandler(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
