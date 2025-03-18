package com.mladich.ambientguysmod.entity.client;

import com.mladich.ambientguysmod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RockeaterGnomeRenderer extends GeoEntityRenderer<RockeaterGnomeEntity> {

    public RockeaterGnomeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RockeaterGnomeModel());
    }
}