package org.limelight.iceBoatRace.boatSystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.limelight.iceBoatRace.IceBoatRace.currentMap;

public class startRace implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender.isOp() && currentMap != null){currentMap.startRace();}
        return true;
    }
}
