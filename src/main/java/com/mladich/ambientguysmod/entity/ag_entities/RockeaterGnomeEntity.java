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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RockeaterGnomeEntity extends TamableAnimal {
    public RockeaterGnomeEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

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
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) * 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); //Prevents drowning, lol
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.10));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.40, Ingredient.of(Items.COBBLESTONE), false));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.20, Ingredient.of(Items.COBBLED_DEEPSLATE), true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 5f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.7D)
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

    @Override
    public LivingEntity self() {
        return super.self();
    }

    @Override
    public boolean shouldRiderSit() {
        return super.shouldRiderSit();
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return super.getPickedResult(target);
    }

    @Override
    public boolean canRiderInteract() {
        return super.canRiderInteract();
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return super.canBeRiddenUnderFluidType(type, rider);
    }

    @Override
    public boolean isMultipartEntity() {
        return super.isMultipartEntity();
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return super.getParts();
    }

    @Override
    public float getStepHeight() {
        return super.getStepHeight();
    }

    @Override
    public boolean canStartSwimming() {
        return super.canStartSwimming();
    }

    @Override
    public boolean canSwimInFluidType(FluidType type) {
        return super.canSwimInFluidType(type);
    }

    @Override
    public void sinkInFluid(FluidType type) {
        super.sinkInFluid(type);
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return super.canDrownInFluidType(type);
    }

    @Override
    public boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
        return super.moveInFluid(state, movementVector, gravity);
    }
}
