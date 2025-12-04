package org.limelight.iceBoatRace.general;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.joml.Vector2f;
import org.limelight.iceBoatRace.lapsNLeaderboardSystem.LapsHandler;

public class Debugger implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;
        if (args.length <= 0) return false;

        switch (args[0]) {
            case "spawn_locations":
                Vector2f point1 = new Vector2f(-2907.5f, 2172.5f);
                Vector2f point2 = new Vector2f(-2901.5f, 2195.5f);
                LapsHandler.getSpawnLocations(Bukkit.getWorld("keventsbuildworld"), point1, point2, 3, 30);
                return true;
        }
        return false;
    }

}
