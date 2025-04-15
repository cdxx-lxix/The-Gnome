package com.mladich.thegnomemod.entity.gnome;

import com.mladich.thegnomemod.Config;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
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
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RockeaterGnomeEntity extends TamableAnimal implements GeoEntity {

    public RockeaterGnomeEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TEMPTATION = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PANIC = SynchedEntityData.defineId(RockeaterGnomeEntity.class, EntityDataSerializers.BOOLEAN);
    private int ambientSoundTime = 0;
    private int ambientSoundInterval = 0;
//    private final TagKey<Item> gnomeFood = ItemTags.STONE_TOOL_MATERIALS;

    /**
     * Synced data prevents loosing data and flukes in behavior.
     * */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(STANDING, false);
        this.entityData.define(TEMPTATION, false);
        this.entityData.define(PANIC, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getTextureVariant());
        compound.putBoolean("Standing", this.isStandingStill());
        compound.putBoolean("Temptation", this.isTempted());
        compound.putBoolean("Panic", this.isInPanicMode());
    }

    public int getTextureVariant() {
        return this.entityData.get(VARIANT);
    }

    public boolean isInPanicMode() {
        return this.entityData.get(PANIC);
    }

    public boolean isTempted() {
        return this.entityData.get(TEMPTATION);
    }

    public boolean isStandingStill() {
        return this.entityData.get(STANDING);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTextureVariant(compound.getInt("Variant"));
        this.setStandingStill(compound.getBoolean("Standing"));
        this.setTempted(compound.getBoolean("Temptation"));
        this.setPanicMode(compound.getBoolean("Panic"));
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public void setPanicMode(boolean panic) {
        this.entityData.set(PANIC, panic);
    }

    public void setTempted(boolean temptation) {
        this.entityData.set(TEMPTATION, temptation);
    }

    public void setStandingStill(boolean standing) {
        this.entityData.set(STANDING, standing);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 10);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); // Prevents drowning, lol
        this.goalSelector.addGoal(1, new GnomePanicGoal(this,1.5D)); // He will prioritize panic above anything rather than drowning
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this)); // Only panic, drowning  or getting hurt can make him standup without command
        this.goalSelector.addGoal(1, new GnomeStandStillWhenOrderedToGoal(this)); // Only panic, drowning  or getting hurt can make him stop standing without command
        this.goalSelector.addGoal(2, new GnomeTemptationGoal(this, 1.40, Ingredient.of(ItemTags.STONE_TOOL_MATERIALS), false)); // He's a rock eater after all
        this.goalSelector.addGoal(2, new GnomeLookAtPlayerGoal(this, Player.class, 5f)); // Just stare sometimes in your eyes
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.10)); // Prevents the desire to swim
        this.goalSelector.addGoal(3, new GnomeRandomLookAroundGoal(this)); // Head rotation is in com.mladich.ambientguysmod.entity.client.RockeaterGnomeModel.setCustomAnimations
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false)); // He will follow his owner but all of the things on top can get him distracted
    }
    /**
     *
     * */
    private PlayState predicate(AnimationState<?> event) {
        if (this.isInSittingPose()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("sit"));
        } else if (this.isStandingStill()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("idle"));
        } else if (this.isTempted() && event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("temptation_follow"));
        } else if (this.isTempted()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("temptation_idle"));
        }
//        else if (this.isInPanicMode()) {
//            event.getController().setAnimation(RawAnimation.begin().thenPlay("panic"));
//        }
        else if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("walk"));
        }  else {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
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
            if (handStack.is(ItemTags.STONE_TOOL_MATERIALS)) {
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
             if (!this.isFood(handStack) && player.isShiftKeyDown()) {
                // If a player is holding anything but the food and sneaking THE GNOME will stand still
                this.setStandingStill(!this.isStandingStill());
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            } else if (!this.isFood(handStack)) {
                 // If a player is holding anything but the food THE GNOME will sit
                 this.setOrderedToSit(!this.isOrderedToSit());
                 return InteractionResult.sidedSuccess(this.level().isClientSide());
             }  else if (this.getHealth() < this.getMaxHealth()) {
                // If a player is holding food then THE GNOME will heal
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

    @Override
    public boolean isFood(@NotNull ItemStack pStack) {
        return pStack.is(ItemTags.STONE_TOOL_MATERIALS);
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
    public boolean isPushable() {
        // THE GNOME becomes static when sitting or standing just like a statue (Terraria inspired)
        return this.isAlive() && !this.isOrderedToSit() && !this.isStandingStill() && !this.onClimbable();
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
     * Changes gnome's spawning area to caves only (at least it tries to). In the json it is set to minecraft:is_overworld
     */
    public static boolean checkRockeaterGnomeSpawnRules(EntityType<RockeaterGnomeEntity> pGnome, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        //            TheGnomeMod.LOG.error("GNOME METHOD   /tp {} {} {}", pPos.getX(), pPos.getY(), pPos.getZ());
        return pPos.getY() < pLevel.getSeaLevel() - 10;
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        // Without overriding this ridiculus bullshit THE GNOME won't spawn at all or only on the surface.
        return 0.0F;
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag p_146750_) {
        this.setTextureVariant(this.random.nextIntBetweenInclusive(0, 2));
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
    public @Nullable LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public boolean alwaysAccepts() {
        return super.alwaysAccepts();
    }
}

