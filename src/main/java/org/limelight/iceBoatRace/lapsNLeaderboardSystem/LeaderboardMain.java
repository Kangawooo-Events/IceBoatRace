package org.limelight.iceBoatRace.lapsNLeaderboardSystem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import static org.limelight.iceBoatRace.IceBoatRace.currentMap;

public class LeaderboardMain {

    public static int getAwardedPoints(){
        return currentMap.players.size();
    }

    //
    public static void awardPoints(Player player, Integer points){
        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("points");
        if (objective == null){
            objective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("points", Criteria.DUMMY, Component.text("Points"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        points += objective.getScore(player).getScore();
        objective.getScore(player).setScore(points);
    }


}
