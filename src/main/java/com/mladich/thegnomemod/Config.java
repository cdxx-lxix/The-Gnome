package com.mladich.thegnomemod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = TheGnomeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> GNOME_AMBIENT_INTERVAL;
    public static final List<Integer> DEFAULT_AMBIENT_INTERVAL = Arrays.asList(100, 1000);

    public static ForgeConfigSpec.BooleanValue GNOME_RANDOM_TELEPORTATION;
    public static final boolean DEFAULT_RANDOM_TELEPORTATION = true;

    static {
        BUILDER.comment("THE GNOME Mod Configuration");

        BUILDER.push("Behavior Settings");

        GNOME_AMBIENT_INTERVAL = BUILDER
                .comment("Time THE GNOME takes to break the silence (in ticks; 20 ticks = 1 second)")
                .comment("Format: [Minimum, Maximum]")
                .comment("Default: [100, 1000] (5 to 50 seconds)")
                .defineList("ambient_interval", DEFAULT_AMBIENT_INTERVAL,
                        Config::validateAmbientInterval);

        GNOME_RANDOM_TELEPORTATION = BUILDER
                .comment("If true, THE GNOME randomly teleports like an enderman")
                .comment("If false, THE GNOME only teleports when in panic or hurt")
                .comment("Default: true")
                .define("random_teleportation", DEFAULT_RANDOM_TELEPORTATION);

        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validateAmbientInterval(Object obj) {
        if (!(obj instanceof List)) return false;

        try {
            List<?> list = (List<?>) obj;
            if (list.size() != 2) return false;

            int min = Integer.parseInt(list.get(0).toString());
            int max = Integer.parseInt(list.get(1).toString());

            if (min < 0 || max < 0 || max < min) {
                TheGnomeMod.LOG.warn("INVALID GNOME_AMBIENT_INTERVAL: min must be >= 0 and max must be >= min. Using defaults.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            TheGnomeMod.LOG.warn("INVALID GNOME_AMBIENT_INTERVAL format. Using defaults.");
            return false;
        }
    }

    public static class GnomeSettings {

        public static int getAmbientIntervalMin() {
            try {
                List<? extends Integer> interval = GNOME_AMBIENT_INTERVAL.get();
                return interval != null && interval.size() == 2 ? interval.get(0) : DEFAULT_AMBIENT_INTERVAL.get(0);
            } catch (IllegalStateException e) {
                TheGnomeMod.LOG.debug("Config not loaded yet, using default ambient interval min");
                return DEFAULT_AMBIENT_INTERVAL.get(0);
            }
        }

        public static int getAmbientIntervalMax() {
            try {
                List<? extends Integer> interval = GNOME_AMBIENT_INTERVAL.get();
                return interval != null && interval.size() == 2 ? interval.get(1) : DEFAULT_AMBIENT_INTERVAL.get(1);
            } catch (IllegalStateException e) {
                TheGnomeMod.LOG.debug("Config not loaded yet, using default ambient interval max");
                return DEFAULT_AMBIENT_INTERVAL.get(1);
            }
        }

        public static boolean canRandomlyTeleport() {
            try {
                return GNOME_RANDOM_TELEPORTATION.get();
            } catch (IllegalStateException e) {
                TheGnomeMod.LOG.debug("Config not loaded yet, using default random teleportation");
                return DEFAULT_RANDOM_TELEPORTATION;
            }
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        TheGnomeMod.LOG.debug("THE GNOME config (re)loaded");
    }
}