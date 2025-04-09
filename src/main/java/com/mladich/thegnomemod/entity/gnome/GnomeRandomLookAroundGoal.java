package com.mladich.thegnomemod.entity.gnome;

import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

/**
 * This class modifies the standard RandomLookAroundGoal to make THE GNOME sit still
 */

public class GnomeRandomLookAroundGoal extends RandomLookAroundGoal {
    private final RockeaterGnomeEntity gnome;
    public GnomeRandomLookAroundGoal(RockeaterGnomeEntity pGnome) {
        super(pGnome);
        this.gnome = pGnome;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !gnome.isOrderedToSit();
    }
}
