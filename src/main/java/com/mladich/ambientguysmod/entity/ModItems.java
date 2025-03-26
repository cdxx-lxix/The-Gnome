package com.mladich.ambientguysmod.entity;

import com.mladich.ambientguysmod.AmbientGuysMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AmbientGuysMod.MODID);

    public static final RegistryObject<Item> ROCKEATERGNOME_SPAWN_EGG = ITEMS.register("rockeatergnome_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.RockeaterGnome, 0x2F00A5, 0xAB0000, (new Item.Properties())));
}

