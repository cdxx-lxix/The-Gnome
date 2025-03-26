package com.mladich.thegnomemod.entity.client;

import com.mladich.thegnomemod.TheGnomeMod;
import com.mladich.thegnomemod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RockeaterGnomeModel extends GeoModel<RockeaterGnomeEntity> {
    private final ResourceLocation model = new ResourceLocation(TheGnomeMod.MODID, "geo/entity/rockeatergnome.geo.json");
//    private final ResourceLocation texture = new ResourceLocation(AmbientGuysMod.MODID, "textures/entity/rockeatergnome.png");
    private final ResourceLocation animations = new ResourceLocation(TheGnomeMod.MODID, "animations/entity/rockeatergnome.animation.json");
    private static final ResourceLocation[] gnomes = new ResourceLocation[]{
            new ResourceLocation(TheGnomeMod.MODID, "textures/entity/rockeatergnome.png"), // Adult
            new ResourceLocation(TheGnomeMod.MODID, "textures/entity/rockeatergnome2.png"), // Senior
            new ResourceLocation(TheGnomeMod.MODID, "textures/entity/rockeatergnome3.png")}; // Old


    @Override
    public void setCustomAnimations(RockeaterGnomeEntity animatable, long instanceId, AnimationState<RockeaterGnomeEntity> animationState) {
//        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }

    @Override
    public ResourceLocation getModelResource(RockeaterGnomeEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(RockeaterGnomeEntity animatable) {
        return  gnomes[animatable.getTextureVariant()];
    }

    @Override
    public ResourceLocation getAnimationResource(RockeaterGnomeEntity rockeaterGnomeEntity) {
        return this.animations;
    }

}