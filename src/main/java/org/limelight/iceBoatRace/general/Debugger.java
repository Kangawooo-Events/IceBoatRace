package org.limelight.iceBoatRace.general;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.joml.*;
import org.limelight.iceBoatRace.lapsNLeaderboardSystem.LapsHandler;
import org.limelight.iceBoatRace.objectClasses.Line;

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Debugger implements CommandExecutor, TabCompleter {

    String[] commands = {"spawn_locations", "line_interpolate", "set_line", "visualise_line", "reload_debug", "clear_debug"};

    public static ArrayList<BlockDisplay> debugPoints = new ArrayList<BlockDisplay>();

    Vector2f point1 = new Vector2f(-2907.5f, 2172.5f);
    Vector2f point2 = new Vector2f(-2901.5f, 2195.5f);

    // Spawns a display block to signify a location for debugging
    public void spawnDebugPoint(Location spawnLoc) {
        Matrix4f transform = new Matrix4f();
        transform.translate(-0.15f,0.35f,-0.15f);
        transform.scale(0.3f);

        BlockDisplay debugPoint = spawnLoc.getWorld().spawn(spawnLoc, BlockDisplay.class, (display) -> {
            display.setTransformationMatrix(transform);
            display.setBlock(BlockType.RED_STAINED_GLASS.createBlockData());
            display.addScoreboardTag("DebugPoint");
        });
        debugPoints.add(debugPoint);
    }

    // Spawns a display block to visualise a line for debugging
    public void visualiseLine(World world, Vector2f point1, Vector2f point2) {
        Matrix4f transform = new Matrix4f();
        float rise = point1.y - point2.y;
        float run = point1.x - point2.x;

        float magnitude = Line.getMagnitude(rise, run);

        transform.rotateY((float) Math.toRadians(-Line.getAngle(rise, run)));
        transform.translate(-0.1f,0.35f,-magnitude/2);
        transform.scale(0.2f, 0.2f, magnitude);

        Vector2f midPoint = Line.interpolate(point1, point2, 0.5f);
        Location spawnLoc = new Location(world, midPoint.x, world.getHighestBlockYAt((int) midPoint.x, (int) midPoint.y) + 1, midPoint.y);

        BlockDisplay debugPoint = world.spawn(spawnLoc, BlockDisplay.class, (display) -> {
            display.setTransformationMatrix(transform);
            display.setBlock(BlockType.GOLD_BLOCK.createBlockData());
            display.addScoreboardTag("DebugPoint");
        });
        debugPoints.add(debugPoint);
    }

    public static void getDebugPoints(World world) {
        Collection<BlockDisplay> blockDisplays = world.getEntitiesByClass(BlockDisplay.class);
        for (BlockDisplay blockDisplay : blockDisplays) {
            // Check that this blockDisplay is a debug point
            if (blockDisplay.getScoreboardTags().contains("DebugPoint")) {
                // If this debug point is not already in the ArrayList
                if (!debugPoints.contains(blockDisplay)) {
                    debugPoints.add(blockDisplay);
                }
            }
        }
    }

    public void deleteDebugPoints() {
        for (BlockDisplay debugPoint : debugPoints) {
            debugPoint.remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;
        if (args.length <= 0) return false;
        if (!(sender instanceof Player player)) return false;

        World world;
        Location spawnLoc;


        switch (args[0]) {
            /*
            case "spawn_locations":
                int players = 30;
                try {
                    players = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    return false;
                }

                ArrayList<Location> locations = LapsHandler.getSpawnLocations(Bukkit.getWorld("keventsbuildworld"), point1, point2, 3, players);

                deleteDebugPoints();
                for (Location location : locations) {
                    spawnDebugPoint(location);
                }

                return true;*/
            case "line_interpolate":
                Vector2f newPoint = Line.interpolate(point1, point2, 0.5f);
                world = player.getWorld();
                spawnLoc = new Location(player.getWorld(), newPoint.x, world.getHighestBlockYAt((int) newPoint.x, (int) newPoint.y) + 1, newPoint.y);

                deleteDebugPoints();
                spawnDebugPoint(spawnLoc);

                return true;
            case "visualise_line":
                deleteDebugPoints();
                world = player.getWorld();
                visualiseLine(world, point1, point2);
                return true;
            case "set_line":
                if (args.length >= 5) {
                    try {
                        point1 = new Vector2f(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                        point2 = new Vector2f(Float.parseFloat(args[3]), Float.parseFloat(args[4]));
                    } catch (NumberFormatException ignored) {}
                }
                return true;
            case "reload_debug":
                getDebugPoints(player.getWorld());
                return true;
            case "clear_debug":
                deleteDebugPoints();
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player player)) return completions;

        if (args.length <= 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.stream(commands).toList(), completions);
        }
        else {
            switch (args[0]) {
                case "spawn_locations":
                    completions.add("100");
                    completions.add("50");
                    completions.add("25");
                    completions.add("10");
                    completions.add("5");
                    break;
                case "set_line":
                    DecimalFormat df = new DecimalFormat("#.##");
                    switch (args.length) {
                        case 2, 4:
                            completions.add(df.format(player.getLocation().x()));
                            break;
                        case 3, 5:
                            completions.add(df.format(player.getLocation().z()));
                            break;
                    }
                    break;
            }
        }

        return completions;
    }
}

