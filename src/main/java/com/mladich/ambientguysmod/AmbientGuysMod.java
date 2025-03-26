package com.mladich.ambientguysmod;

import com.mladich.ambientguysmod.entity.ModEntities;
import com.mladich.ambientguysmod.entity.ModItems;
import com.mladich.ambientguysmod.entity.ModSounds;
import com.mladich.ambientguysmod.entity.ModSpawns;
import com.mladich.ambientguysmod.entity.client.RockeaterGnomeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;


@Mod(AmbientGuysMod.MODID)
public class AmbientGuysMod {
    public static final String MODID = "ambientguys";
//    private static final Logger LOGGER = LogUtils.getLogger();

    public AmbientGuysMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        GeckoLib.initialize();

        ModEntities.register(modEventBus); // register mobs
        ModSounds.SOUNDS.register(modEventBus); // register sounds
        ModItems.ITEMS.register(modEventBus); // register one spawn egg
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModSpawns::SpawnPlacement);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // I'm not going to register a new tab for a single fucking egg
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
             event.accept(ModItems.ROCKEATERGNOME_SPAWN_EGG.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.RockeaterGnome.get(), RockeaterGnomeRenderer::new);
        }
    }
}