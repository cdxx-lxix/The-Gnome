package com.mladich.thegnomemod.entity;

import com.mladich.thegnomemod.TheGnomeMod;
import com.mladich.thegnomemod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TheGnomeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSpawns {
    @SubscribeEvent
    public static void registerSpawnPlacements(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntities.RockeaterGnome.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    RockeaterGnomeEntity::checkRockeaterGnomeSpawnRules);
        });
    }
}