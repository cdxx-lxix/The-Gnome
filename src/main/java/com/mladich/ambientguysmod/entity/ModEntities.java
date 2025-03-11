package com.mladich.ambientguysmod.entity;

import com.mladich.ambientguysmod.AmbientGuysMod;
import com.mladich.ambientguysmod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AmbientGuysMod.MODID);

    public static final RegistryObject<EntityType<RockeaterGnomeEntity>> RockeaterGnome =
            ENTITY_TYPES.register("rockeatergnome", () ->EntityType.Builder.of(RockeaterGnomeEntity::new, MobCategory.CREATURE)
            .sized(1f,2f)
            .build("rockeatergnome"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
