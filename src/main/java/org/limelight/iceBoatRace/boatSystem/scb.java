package org.limelight.iceBoatRace.boatSystem;

import com.fasterxml.jackson.annotation.JacksonAnnotationValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.craftbukkit.entity.CraftWitch;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.OakBoat;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class scb implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        Location location = player.getLocation();
        World world = player.getWorld();

        /*
        ServerLevel level = ((CraftWorld) (world)).getHandle();
        CollisionlessBoat raft = new CollisionlessBoat(EntityType.OAK_BOAT, level, () -> Items.OAK_BOAT);

        float yaw = Location.normalizeYaw(location.getYaw());
        raft.setYRot(yaw);
        raft.yRotO = yaw;
        raft.setYHeadRot(yaw);

        raft.teleportTo(location.getX(), location.getY(), location.getZ());

        level.addFreshEntity(raft, CreatureSpawnEvent.SpawnReason.COMMAND);*/

        Boat armorStand = world.spawn(location, OakBoat.class);
        armorStand.setCustomName(player.getName());
        armorStand.setCustomNameVisible(true);


        return true;
    }
}
