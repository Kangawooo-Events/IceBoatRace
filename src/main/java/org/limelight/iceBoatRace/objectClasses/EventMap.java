package org.limelight.iceBoatRace.objectClasses;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector2f;
import org.limelight.iceBoatRace.IceBoatRace;
import org.limelight.iceBoatRace.boatSystem.BoatHandler;
import org.limelight.iceBoatRace.general.MessageHandler;
import org.limelight.iceBoatRace.lapsNLeaderboardSystem.LapsHandler;
import java.util.ArrayList;
import java.util.List;


public class EventMap {
    public List<Player> players = new ArrayList<>();
    String name;
    Vector2f[] start;
    Vector2f[] finish;
    World world;
    Integer maxLaps;
    float spacing;
    JavaPlugin plugin;

    public EventMap(String name, Vector2f[] start, Vector2f[] finish, World world, Integer maxLaps, float spacing,JavaPlugin plugin){
        this.name = name;
        this.start = start;
        this.finish = finish;
        this.maxLaps = maxLaps;
        this.world = world;
        this.spacing = spacing;
        this.plugin = plugin;
    }

    public ArrayList<Location> getSpawnLocations(Integer numPlayers) {

        Vector2f lineVector = Line.getVector(start[0], start[1]);
        float lineMagnitude = Line.getMagnitude(lineVector.x, lineVector.y);
        Vector2f lineUnitVector = new Vector2f(lineVector.x / lineMagnitude, lineVector.y / lineMagnitude);
        Vector2f normalUnitVector = new Line(start[0], start[1]).getNormal(world);

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

            double x = rowX + columnX + start[1].x;
            double z = rowZ + columnZ + start[1].y;
            int y = world.getHighestBlockYAt((int) x, (int) z) + 1;

            float yaw = Line.getAngle(normalUnitVector.y, normalUnitVector.x);

            Location position = new Location(world, x, y, z,yaw,0);
            locations.add(position);



        }
        return locations;
    }

    public void startRace(){

        //Update the variables in the lap handler
        LapsHandler.point1 = finish[0];
        LapsHandler.point2 = finish[1];
        LapsHandler.finishLine = new Line(finish[0],finish[1]);
        LapsHandler.maxLaps = maxLaps;

        IceBoatRace.eventStatus = IceBoatRace.EventStatus.COUNTDOWN;
        int countdown = 3;

        //Gathers the list of players
        for (Player player : Bukkit.getOnlinePlayers()){
            if (players == null){players = new ArrayList<>();}
            if (!player.isOp()) players.add(player);
        }

        int size = players.size();
        ArrayList<Location> startLocations = getSpawnLocations(size);

        if (startLocations != null) {
            for (int n = 0; n < size; n++) BoatHandler.spawnRacer(players.get(n),plugin,startLocations.get(n));
        }

        String colorString = ChatColor.BOLD+"";

        //Sends a countdown to all the players
        for (int i = 0; i < countdown;i++){
            String countdownString = String.valueOf(countdown-i);

            Bukkit.getScheduler().runTaskLater(plugin,()->{
                for (Player player: players) MessageHandler.sendActionbar(player, colorString+countdownString);
            },(i)*20);
        }

        Bukkit.getScheduler().runTaskLater(plugin,()->{
            for (Player player: players) MessageHandler.sendActionbar(player, colorString+"GO!");
            IceBoatRace.eventStatus = IceBoatRace.EventStatus.IN_PROGRESS;
        },countdown*20);


    }

}
