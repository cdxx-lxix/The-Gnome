package com.mladich.ambientguysmod.entity.ag_entities;

import com.mladich.ambientguysmod.entity.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class RockeaterGnomeEntity extends TamableAnimal implements GeoEntity {

    public RockeaterGnomeEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> PANIC = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.BOOLEAN);
    private int panicCooldown = 0;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PANIC, false);
    }

    public boolean isPanicking() {
        return this.entityData.get(PANIC);
    }

    public void setPanicking(boolean panicking) {
        this.entityData.set(PANIC, panicking);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); //Prevents drowning, lol
        this.goalSelector.addGoal(1, new RockeaterGnomeEntity.RockeaterGnomePanicGoal(1.5D));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.40, Ingredient.of(Items.COBBLESTONE), false));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.20, Ingredient.of(Items.COBBLED_DEEPSLATE), true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 5f));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.10));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
    }

    private PlayState predicate(AnimationState<?> event) {
        if (this.isInSittingPose()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("sit"));
        } else if (event.isMoving()) {
            if (this.getSpeed() > 0.5D) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("sprint"));
            } else {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("walk"));
            }
        } else if (this.isPanicking()) {
            event.getController().setAnimation(RawAnimation.begin().then("panic", Animation.LoopType.PLAY_ONCE));
        }  else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("idle2"));
        }
        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }
    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack handStack = player.getItemInHand(interactionHand);

        if (!this.isTame()) {
            if (handStack.is(Items.COBBLESTONE) || handStack.is(Items.COBBLED_DEEPSLATE)) {
                if (!player.getAbilities().instabuild) {
                    handStack.shrink(1);
                }
                if (!this.level().isClientSide()) {
                    if (this.random.nextInt(10) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                        this.tame(player);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        } else if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.isFood(handStack)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            } else if (this.getHealth() < this.getMaxHealth()) {
                this.gameEvent(GameEvent.EAT, this);
                this.heal(2.0F);
                if (!player.getAbilities().instabuild) {
                    handStack.shrink(1);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }
        return super.mobInteract(player, interactionHand);

    }

    // Fear of sticks
    class RockeaterGnomePanicGoal extends PanicGoal {
        public RockeaterGnomePanicGoal(double pSpeedModifier) {
            super(RockeaterGnomeEntity.this, pSpeedModifier);
        }

        @Override
        public void tick() {
            super.tick();
            if (panicCooldown > 0) {
                panicCooldown--;
            }
        }

        @Override
        public void start() {
            if (panicCooldown == 0) {
                super.start();
                RockeaterGnomeEntity.this.setPanicking(true);
                panicCooldown = 100; //Set timer for 5 seconds
            }
            System.out.println(RockeaterGnomeEntity.this.isPanicking()); // TODO: REMOVE
        }

        @Override
        public void stop() {
            if (panicCooldown == 1) { // Stops panic one tick before timer runs out
                super.stop();
                RockeaterGnomeEntity.this.setPanicking(false);
                System.out.println(RockeaterGnomeEntity.this.isPanicking()); // TODO: REMOVE
            }
        }

        @Override
        protected boolean shouldPanic() {
            if (!RockeaterGnomeEntity.this.isPanicking()) {
                return isPlayerHoldingStickNearby();
            } else {
                return this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
            }
        }

        // Modified method with distance parameter
        private boolean isPlayerHoldingStickNearby() {
            List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(
                    Player.class,
                    this.mob.getBoundingBox().inflate(6.0) // In radius of 6 blocks
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

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ROCKEATER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.ROCKEATER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ROCKEATER_DEATH.get();
    }
}