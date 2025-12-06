package org.limelight.iceBoatRace.mapVoteSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static org.limelight.iceBoatRace.IceBoatRace.eventStatus;
import static org.limelight.iceBoatRace.mapVoteSystem.MapVoteCommand.voteMap;

public class EndMapVoteCommand  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp() || !eventStatus.equals("voting"))
            return true;

        sender.sendMessage(ChatColor.AQUA + "" + "[ICEBOAT] The voting has ended");
        endVote(sender);
        return true;
    }

    private void endVote(CommandSender sender) {
        if (!voteMap.isEmpty()) {
            int maxValue = Collections.max(voteMap.entrySet(), Map.Entry.comparingByValue()).getValue();
            List<String> highestVoteMaps = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : voteMap.entrySet()) {
                if (entry.getValue() == maxValue)
                    highestVoteMaps.add(entry.getKey());
            }
            Random rand = new Random();
            String selectedMap = highestVoteMaps.get(rand.nextInt(highestVoteMaps.size()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.AQUA + "[ICEBOAT] Selected Map: " + selectedMap);
                player.sendTitle(ChatColor.AQUA + selectedMap, "has been selected", 20,40,20);
            }

        }
        else {
            sender.sendMessage(ChatColor.AQUA + "" + "[ICEBOAT] There were no players present during voting");
        }

        eventStatus = "countdown";
    }
}
