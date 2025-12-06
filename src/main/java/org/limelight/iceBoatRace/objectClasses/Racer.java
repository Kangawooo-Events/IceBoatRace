package org.limelight.iceBoatRace.objectClasses;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Racer extends Boat {

    public Racer(EntityType<? extends Boat> entityType, Level level, Supplier<Item> dropItem) {
        super(entityType, level, dropItem);
    }


    // Make the entity have zero dimensions (no hitbox)
    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.fixed(0.0F, 0.0F);
    }


    // Prevent being pushed / pushing others
    @Override
    public boolean isPushable() {
        return false;
    }


    @Override
    public void push(@NotNull Entity entity) {
        // no-op
    }


}
