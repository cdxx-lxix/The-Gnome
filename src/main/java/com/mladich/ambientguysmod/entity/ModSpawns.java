package com.mladich.ambientguysmod.entity;

import com.mladich.ambientguysmod.AmbientGuysMod;
import com.mladich.ambientguysmod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AmbientGuysMod.MODID)
public class ModSpawns {
    public static void SpawnPlacement() {
        SpawnPlacements.register(ModEntities.RockeaterGnome.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RockeaterGnomeEntity::checkRockeaterGnomeSpawnRules);
    }
}