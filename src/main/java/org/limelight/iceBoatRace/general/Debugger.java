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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Debugger implements CommandExecutor, TabCompleter {
    String[] commands = {"spawn_locations", "line_interpolate", "set_line", "clear_debug"};

    ArrayList<BlockDisplay> debugPoints = new ArrayList<BlockDisplay>();

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
        });
        debugPoints.add(debugPoint);
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

        switch (args[0]) {
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

                return true;
            case "line_interpolate":
                Vector2f newPoint = Line.interpolate(point1, point2, 0.5f);
                World world = player.getWorld();
                Location spawnLoc = new Location(world, newPoint.x, world.getHighestBlockYAt((int) newPoint.x, (int) newPoint.y) + 1, newPoint.y);

                deleteDebugPoints();
                spawnDebugPoint(spawnLoc);

                return true;
            case "set_line":
                if (args.length >= 5) {
                    try {
                        point1 = new Vector2f(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                        point2 = new Vector2f(Float.parseFloat(args[3]), Float.parseFloat(args[4]));
                    } catch (NumberFormatException ignored) {}
                }
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
        } else {
            switch (args[1]) {
                case "spawn_locations":
                    completions.add("100");
                    completions.add("50");
                    completions.add("25");
                    completions.add("10");
                    completions.add("5");
                case "set_line":
                    switch (args.length) {
                        case 2, 4:
                            completions.add(player.getLocation().x()+"");
                        case 3, 5:
                            completions.add(player.getLocation().y()+"");
                    }
            }
        }

        return completions;
    }
}
