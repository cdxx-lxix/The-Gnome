package com.mladich.ambientguysmod.entity.client;

import com.mladich.ambientguysmod.AmbientGuysMod;
import com.mladich.ambientguysmod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RockeaterGnomeRenderer extends MobRenderer<RockeaterGnomeEntity, RockeaterGnomeModel<RockeaterGnomeEntity>> {
    public RockeaterGnomeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new RockeaterGnomeModel<>(pContext.bakeLayer(ModModelLayers.ROCKEATER_LAYER)), 0.7f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RockeaterGnomeEntity pEntity) {
        return new ResourceLocation(AmbientGuysMod.MODID, "textures/entity/rockeatergnome.png");
    }


}
