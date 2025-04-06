package com.mladich.thegnomemod.entity.ag_entities;

import com.mladich.thegnomemod.Config;
import com.mladich.thegnomemod.TheGnomeMod;
import com.mladich.thegnomemod.entity.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class RockeaterGnomeEntity extends TamableAnimal implements GeoEntity {

    public RockeaterGnomeEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> PANIC = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.INT);
    private int panicCooldown = 0;
    private int ambientSoundTime = 0;
    private int ambientSoundInterval = 0;

    /**
     * Without synched data the panic state is unrelyable and won't be saved on exit or pause
     * */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PANIC, false);
        this.entityData.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Panic", this.isPanicking());
        compound.putInt("Variant", this.getTextureVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setPanicking(compound.getBoolean("Panic"));
        this.setTextureVariant(compound.getInt("Variant"));
    }

    public int getTextureVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public boolean isPanicking() {
        return this.entityData.get(PANIC);
    }

    public void setPanicking(boolean panicking) {
        this.entityData.set(PANIC, panicking);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 10);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); // Prevents drowning, lol
        this.goalSelector.addGoal(1, new RockeaterGnomeEntity.RockeaterGnomePanicGoal(1.5D)); // He will prioritize panic above anything rather than drowning
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this)); // Only panic, drowning  or getting hurt can make him standup without command
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.40, Ingredient.of(ItemTags.STONE_TOOL_MATERIALS), false)); // He's a rock eater after all
        this.goalSelector.addGoal(2, new RockeaterGnomeEntity.GnomeLookAtPlayerGoal(this, Player.class, 5f)); // Just stare sometimes in your eyes
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.10)); // Prevents the desire to swim
        this.goalSelector.addGoal(3, new RockeaterGnomeEntity.GnomeRandomLookAroundGoal(this)); // Head rotation is in com.mladich.ambientguysmod.entity.client.RockeaterGnomeModel.setCustomAnimations
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, Config.GnomeSettings.getOwnerFollowRange(), Config.GnomeSettings.getOwnerFollowStop(), false)); // He will follow his owner but all of the things on top can get him distracted
    }

    class GnomeRandomLookAroundGoal extends RandomLookAroundGoal {
        private GnomeRandomLookAroundGoal(Mob pMob) {
            super(pMob);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !RockeaterGnomeEntity.this.isOrderedToSit();
        }
    }

    class GnomeLookAtPlayerGoal extends LookAtPlayerGoal {
        public GnomeLookAtPlayerGoal(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
            super(pMob, pLookAtType, pLookDistance);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !RockeaterGnomeEntity.this.isOrderedToSit();
        }
    }

    /**
     * Subject to change. Some animations doesn't work properly or everytime.
     * */
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

    /**
     * Register controller and point to a predicate with logic for animations.
     * Single controller instead of multiple because of custom animations for single action (Idle)
     * */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }


    /**
     * Recudes cooldown of panic mode. If it is more than 0 the gnome won't panic again
     * */
    @Override
    public void tick() {
        super.tick();
        if (panicCooldown > 0) {
            panicCooldown--;
        }
    }

    /**
     * Just animations cache
     * */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /**
     * Tame, feed and healing mechanics
     * */
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

    /**
     * Gnome is afraid of sticks if it is in a player's hands.
     * Gnome checks for it and scans the area for players with sticks in their hands
     * This mechanism has a timer of 10 seconds between each panic activation to trigger custom animation
     * */
    class RockeaterGnomePanicGoal extends PanicGoal {
        public RockeaterGnomePanicGoal(double pSpeedModifier) {
            super(RockeaterGnomeEntity.this, pSpeedModifier);
        }

        @Override
        public void start() {
            if (panicCooldown == 0) {
                super.start();
                RockeaterGnomeEntity.this.setPanicking(true);
                panicCooldown = 200; //Set timer for 10 seconds
            }
        }

        @Override
        public void stop() {
            if (panicCooldown == 1) { // Stops panic one tick before timer runs out
                super.stop();
                RockeaterGnomeEntity.this.setPanicking(false);
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

    /**
     *  Modifies receiving damage mechanic (mix of wolf and enderman)
     * */
    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            Entity entity = pSource.getEntity();
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }

            if (entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                pAmount = (pAmount + 1.0F) / 2.0F;
            }
            this.resetAmbientSoundTimeAndInterval();
            this.teleport();
            return super.hurt(pSource, pAmount);
        }
    }



    /** Sound Events block  */
    @Override
    public void baseTick() {
        super.baseTick();
        if (this.isAlive() && (this.ambientSoundInterval == this.ambientSoundTime++)) {
            this.resetAmbientSoundTimeAndInterval();
            this.playAmbientSound();
        }
    }

    @Override
    public int getAmbientSoundInterval() {
        return this.random.nextIntBetweenInclusive(Config.GnomeSettings.getAmbientIntervalMin(), Config.GnomeSettings.getAmbientIntervalMax());
    }

    private void resetAmbientSoundTimeAndInterval() {
        this.ambientSoundInterval = getAmbientSoundInterval();
        this.ambientSoundTime = 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ROCKEATER_AMBIENT.get();
    }

    public void playAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }

    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.ROCKEATER_HURT.get();
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ROCKEATER_DEATH.get();
    }
// Custom sound event for teleport to replace enderman's sound
    protected SoundEvent getGnomeTeleport() {
        return ModSounds.ROCKEATER_TELEPORT.get();
    }

    /**
     * Changes gnome's spawning area to caves only (at least it tries to)
     */
    public static boolean checkRockeaterGnomeSpawnRules(EntityType<RockeaterGnomeEntity> pGnome, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
//        if ( pPos.getY() < 60 && pLevel.getMaxLocalRawBrightness(pPos) <= 4 && pLevel.getBlockState(pPos.below()).isSolidRender(pLevel, pPos.below())) {
//            TheGnomeMod.LOG.error("/tp {} {} {}", pPos.getX(), pPos.getY(), pPos.getZ());
//            TheGnomeMod.LOG.warn("The Gnome had spawned");
//            return checkMobSpawnRules(pGnome, pLevel, pSpawnType, pPos, pRandom);
//        } else {
//            TheGnomeMod.LOG.debug("Current Y: " + pPos.getY());
//            TheGnomeMod.LOG.warn("The Gnome won't spawn");
//            return false;
//        }

        if (pPos.getY() < pLevel.getSeaLevel() - 10)
        {
            TheGnomeMod.LOG.error("GNOME METHOD   /tp {} {} {}", pPos.getX(), pPos.getY(), pPos.getZ());
            return true;
        }
        else
        {
            return false;
        }

//        if (pPos.getY() >= pLevel.getSeaLevel() - 10) {
//            return false;
//        } else {
//            int i = pLevel.getMaxLocalRawBrightness(pPos);
//            int j = 4;
//            TheGnomeMod.LOG.error("GNOME METHOD   /tp {} {} {}", pPos.getX(), pPos.getY(), pPos.getZ());
//            return i <= pRandom.nextInt(j) && checkMobSpawnRules(pGnome, pLevel, pSpawnType, pPos, pRandom);
//        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag p_146750_) {
        this.setTextureVariant(this.random.nextIntBetweenInclusive(Config.GnomeSettings.getTextureVariantStart(), Config.GnomeSettings.getTextureVariantEnd())); // Used as array index
        return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, p_146750_);
    }

    /**
     * Overrides usual entity's behavior to occasionally teleport (Modified enderman code without block related to light level)
     */
    @Override
    protected void customServerAiStep() {
        if (Config.GnomeSettings.canRandomlyTeleport() && !this.isOrderedToSit()) {
            float f = this.random.nextFloat();
            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.teleport();
            }
        }
        super.customServerAiStep();
    }

    /**
     * Teleport the gnome to a random nearby position (Enderman code)
     */
    protected boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    /**
     * Teleport the gnome (Enderman code)
     */
    private boolean teleport(double pX, double pY, double pZ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(pX, pY, pZ);

        while(blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, pX, pY, pZ);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound((Player)null, this.xo, this.yo, this.zo, this.getGnomeTeleport(), this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(this.getGnomeTeleport(), 1.0F, 1.0F);
                }
            }
            return flag2;
        } else {
            return false;
        }
    }



    /**
     * Mandatory but useless for gnome methods
     * */
    @Override
    public int getExperienceReward() {
        return this.random.nextInt(2, 5);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
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

