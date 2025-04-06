package com.mladich.thegnomemod.event;

// Used exclusively for testing

//@Mod.EventBusSubscriber(modid = TheGnomeMod.MODID)
//public class ModForgeBus {
//    @SubscribeEvent
//    public static void onEntityJoinWorld(final EntityJoinLevelEvent event) {
//        if (!event.getLevel().isClientSide()) {
//            if (event.getEntity().getType() == ModEntities.RockeaterGnome.get()) {
//                BlockPos pPos = event.getEntity().getOnPos();
//                TheGnomeMod.LOG.error("/tp {} {} {}", pPos.getX(), pPos.getY(), pPos.getZ());
//                TheGnomeMod.LOG.warn("{} gnomes currently alive", ((ServerLevel) event.getLevel()).getEntities(ModEntities.RockeaterGnome.get(), LivingEntity::isAlive).size());
//            }
//        }
//    }
//}
