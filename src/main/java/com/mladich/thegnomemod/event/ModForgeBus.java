package com.mladich.thegnomemod.event;

import com.mladich.thegnomemod.TheGnomeMod;
import com.mladich.thegnomemod.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheGnomeMod.MODID)
public class ModForgeBus {
    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity().getType() == ModEntities.RockeaterGnome.get()) {
                BlockPos pPos = event.getEntity().getOnPos();
                TheGnomeMod.LOG.error("/tp {} {} {}", pPos.getX(), pPos.getY(), pPos.getZ());
                TheGnomeMod.LOG.warn("{} gnomes currently alive", ((ServerLevel) event.getLevel()).getEntities(ModEntities.RockeaterGnome.get(), LivingEntity::isAlive).size());
            }
        }
    }
}
