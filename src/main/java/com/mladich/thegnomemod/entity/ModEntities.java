package com.mladich.thegnomemod.entity;

import com.mladich.thegnomemod.TheGnomeMod;
import com.mladich.thegnomemod.entity.ag_entities.RockeaterGnomeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheGnomeMod.MODID);

    public static final RegistryObject<EntityType<RockeaterGnomeEntity>> RockeaterGnome =
            ENTITY_TYPES.register("rockeatergnome", () ->EntityType.Builder.of(RockeaterGnomeEntity::new, MobCategory.MONSTER)
            .sized(1f,1.5f)
            .build("rockeatergnome"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
