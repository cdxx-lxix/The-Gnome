package com.mladich.thegnomemod.entity.gnome;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GnomeStandStillWhenOrderedToGoal extends Goal {
    private final RockeaterGnomeEntity gnome;

    public GnomeStandStillWhenOrderedToGoal(RockeaterGnomeEntity pGnome) {
        this.gnome = pGnome;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.gnome.isStandingStill();
    }

    @Override
    public boolean canUse() {
        if (!this.gnome.isTame()) {
            return false;
        } else if (this.gnome.isInWaterOrBubble()) {
            return false;
        } else if (!this.gnome.onGround()) {
            return false;
        } else {
            LivingEntity livingentity = this.gnome.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return (!(this.gnome.distanceToSqr(livingentity) < 144.0D) || livingentity.getLastHurtByMob() == null) && this.gnome.isStandingStill();
            }
        }
    }

    public void start() {
        this.gnome.getNavigation().stop();
        this.gnome.setStandingStill(true);
    }

    public void stop() {
        this.gnome.setStandingStill(false);
    }
}
