package com.mladich.ambientguysmod.event;

import com.mladich.ambientguysmod.AmbientGuysMod;
import com.mladich.ambientguysmod.entity.ModEntities;
import com.mladich.ambientguysmod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AmbientGuysMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.RockeaterGnome.get(),
                RockeaterGnomeEntity.createAttributes().build());
    }
}
