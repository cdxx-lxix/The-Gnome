package com.mladich.ambientguysmod.event;

import com.mladich.ambientguysmod.AmbientGuysMod;
import com.mladich.ambientguysmod.entity.client.ModModelLayers;
import com.mladich.ambientguysmod.entity.client.RockeaterGnomeModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AmbientGuysMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.ROCKEATER_LAYER, RockeaterGnomeModel::createBodyLayer);
    }
}
