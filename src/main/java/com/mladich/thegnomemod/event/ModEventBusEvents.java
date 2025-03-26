package com.mladich.thegnomemod.event;

import com.mladich.thegnomemod.TheGnomeMod;
import com.mladich.thegnomemod.entity.ModEntities;
import com.mladich.thegnomemod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheGnomeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.RockeaterGnome.get(),
                RockeaterGnomeEntity.createAttributes().build());
    }
}
