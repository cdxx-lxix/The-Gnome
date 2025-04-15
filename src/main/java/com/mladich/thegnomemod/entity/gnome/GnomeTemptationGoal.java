package com.mladich.thegnomemod.entity.gnome;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class GnomeTemptationGoal  extends TemptGoal {
    protected final RockeaterGnomeEntity mob;
    public GnomeTemptationGoal(RockeaterGnomeEntity pMob, double pSpeedModifier, Ingredient pItems, boolean pCanScare) {
        super(pMob, pSpeedModifier, pItems, pCanScare);
        this.mob = pMob;
    }

    @Override
    public void start() {
        super.start();
        this.mob.setTempted(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setTempted(false);
    }
}
