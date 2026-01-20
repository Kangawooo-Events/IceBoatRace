package org.limelight.iceBoatRace.objectClasses;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class CollisionlessBoat extends Boat {
    public CollisionlessBoat(EntityType<? extends Boat> var0, Level var1, Supplier<Item> var2) {
        super(var0, var1, var2);
    }

    // Force all collision checks to fail
    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }
}