package com.mladich.ambientguysmod.entity;

import com.mladich.ambientguysmod.AmbientGuysMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AmbientGuysMod.MODID);

    public static final RegistryObject<SoundEvent> ROCKEATER_AMBIENT = SOUNDS.register("entity.rockeatergnome.ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AmbientGuysMod.MODID, "entity.rockeatergnome.ambient")));
    public static final RegistryObject<SoundEvent> ROCKEATER_HURT = SOUNDS.register("entity.rockeatergnome.hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AmbientGuysMod.MODID, "entity.rockeatergnome.hurt")));
    public static final RegistryObject<SoundEvent> ROCKEATER_DEATH = SOUNDS.register("entity.rockeatergnome.death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AmbientGuysMod.MODID, "entity.rockeatergnome.death")));
}