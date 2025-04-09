package com.mladich.thegnomemod.entity.gnome;

/**
 * Gnome is afraid of sticks if it is in a player's hands.
 * Gnome checks for it and scans the area for players with sticks in their hands
 * This mechanism has a timer of 10 seconds between each panic activation to trigger custom animation
 * ---
 * This class is abandoned
 * */

//public class GnomePanicGoal extends PanicGoal {
//    private int panicCooldown = 0;
//    private final RockeaterGnomeEntity gnome;
//    public GnomePanicGoal(RockeaterGnomeEntity pGnome, double pSpeedModifier) {
//        super(pGnome, pSpeedModifier);
//        this.gnome = pGnome;
//    }
//
//    @Override
//    public void tick() {
//        if (panicCooldown > 0) {
//            panicCooldown--;
//        }
//        super.tick();
//    }
//
//    @Override
//    public void start() {
//        if (panicCooldown == 0) {
//            super.start();
//            gnome.setPanicking(true);
//            panicCooldown = 200; //Set timer for 10 seconds
//        }
//    }
//
//    @Override
//    public void stop() {
//        if (panicCooldown == 0) {
//            super.stop();
//            gnome.setPanicking(false);
//        }
//    }
//
//    @Override
//    protected boolean shouldPanic() {
//            return isPlayerHoldingStickNearby() || this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
//    }
//
//    // Modified method with distance parameter
//    private boolean isPlayerHoldingStickNearby() {
//        List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(
//                Player.class,
//                this.mob.getBoundingBox().inflate(6.0) // In radius of 6 blocks
//        );
//
//        for (Player player : nearbyPlayers) {
//            if (player.getMainHandItem().is(Items.STICK) || player.getOffhandItem().is(Items.STICK)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//}
