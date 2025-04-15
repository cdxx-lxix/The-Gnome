package com.mladich.thegnomemod.entity.gnome;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

/**
 * This class modifies the standard LookAtPlayerGoal to make THE GNOME sit still
 */

public class GnomeLookAtPlayerGoal extends LookAtPlayerGoal {
    private final RockeaterGnomeEntity gnome;
    public GnomeLookAtPlayerGoal(RockeaterGnomeEntity pGnome, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
        super(pGnome, pLookAtType, pLookDistance);
        this.gnome = pGnome;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !gnome.isOrderedToSit() && !gnome.isStandingStill();
    }
}
