package org.limelight.iceBoatRace.boatSystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static org.limelight.iceBoatRace.IceBoatRace.plugin;

public class startEventRound implements CommandExecutor {




    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        BoatHandler.startLoop(10,10,plugin);
        return true;
    }
}
