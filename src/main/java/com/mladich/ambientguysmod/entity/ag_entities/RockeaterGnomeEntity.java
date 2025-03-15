package com.mladich.ambientguysmod.entity.ag_entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class RockeaterGnomeEntity extends TamableAnimal implements GeoAnimatable {

    public RockeaterGnomeEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState(); // If you don't have this already
    public final AnimationState panicAnimationState = new AnimationState();
    public final AnimationState sprintAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private boolean isPanicking = false;
    private boolean hasPlayedPanicAnimation = false;

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    private void setupAnimationStates() {
        // Existing idle animation logic
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) * 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        // Update walking animation state (if you haven't already implemented this)
        if(this.walkAnimation.isMoving() && !isPanicking) {
            walkAnimationState.startIfStopped(this.tickCount);
        } else {
            walkAnimationState.stop();
        }

        // Update panic animation states
        if(isPanicking) {
            // If we just started panicking, play the initial panic animation
            if(!hasPlayedPanicAnimation) {
                panicAnimationState.start(this.tickCount);
                hasPlayedPanicAnimation = true;
            } else if(!panicAnimationState.isStarted()) {
                // Once the panic animation is done, start the sprint animation
                sprintAnimationState.startIfStopped(this.tickCount);
                walkAnimationState.stop(); // Stop the walk animation
            }
        } else {
            // Reset panic states when not panicking
            panicAnimationState.stop();
            sprintAnimationState.stop();
            hasPlayedPanicAnimation = false;
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); //Prevents drowning, lol
        this.goalSelector.addGoal(1, new RockeaterGnomeEntity.RockeaterGnomePanicGoal(1.5D));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.10));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.40, Ingredient.of(Items.COBBLESTONE), false));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.20, Ingredient.of(Items.COBBLED_DEEPSLATE), true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 5f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    class RockeaterGnomePanicGoal extends PanicGoal {
        private boolean wasPanicking = false;

        public RockeaterGnomePanicGoal(double pSpeedModifier) {
            super(RockeaterGnomeEntity.this, pSpeedModifier);
        }

        @Override
        public void start() {
            super.start();
            RockeaterGnomeEntity.this.isPanicking = true;
        }

        @Override
        public void stop() {
            super.stop();
            RockeaterGnomeEntity.this.isPanicking = false;
        }

        @Override
        protected boolean shouldPanic() {
            // Check if there are any players nearby holding a stick
            if (isPlayerHoldingStickNearby()) {
                return true;
            }

            // super's panic conditions
            return this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
        }

        @Override
        public void tick() {
            super.tick();
            wasPanicking = true;
        }

        private boolean isPlayerHoldingStickNearby() {
            // Your existing code from previous response
            List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(
                    Player.class,
                    this.mob.getBoundingBox().inflate(8.0D)
            );

            for (Player player : nearbyPlayers) {
                if (player.getMainHandItem().is(Items.STICK) || player.getOffhandItem().is(Items.STICK)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 20);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public boolean isFood(@NotNull ItemStack pStack) {
        return pStack.is(Items.COBBLESTONE) || pStack.is(Items.COBBLED_DEEPSLATE);
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public boolean alwaysAccepts() {
        return super.alwaysAccepts();
    }

}
