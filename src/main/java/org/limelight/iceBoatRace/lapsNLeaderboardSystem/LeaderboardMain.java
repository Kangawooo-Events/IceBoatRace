package org.limelight.iceBoatRace.lapsNLeaderboardSystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import static org.limelight.iceBoatRace.IceBoatRace.currentMap;

public class LeaderboardMain {

    public static int getAwardedPoints(){
        return currentMap.players.size();
    }

    public static void awardPoints(Player player, Integer points){
        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("points");
        try{
            points += objective.getScore(player).getScore();
        } catch (Exception ignored) {}
        objective.getScore(player).setScore(points);
    }


}
