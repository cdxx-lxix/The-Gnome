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

    // Ambient Interval  (min, max)
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> GNOME_AMBIENT_INTERVAL;
    public static final List<Integer> DEFAULT_AMBIENT_INTERVAL = Arrays.asList(100, 1000);

    // Texture Variants  (start, end)
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> GNOME_TEXTURE_VARIANTS;
    public static final List<Integer> DEFAULT_TEXTURE_VARIANTS = Arrays.asList(0, 2);

    // Follow Rules  (range, stop)
    public static ForgeConfigSpec.ConfigValue<List<? extends Float>> GNOME_OWNER_FOLLOW_RULES;
    public static final List<Float> DEFAULT_OWNER_FOLLOW_RULES = Arrays.asList(10.0F, 2.0F);;

    // Random Teleportation
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

        GNOME_TEXTURE_VARIANTS = BUILDER
                .comment("Control types of GNOMES in your world")
                .comment("0 - Adult, 1 - Senior, 2 - Gramps")
                .comment("Format: [Start, End]")
                .comment("Default: [0, 2] (all types)")
                .defineList("texture_variants", DEFAULT_TEXTURE_VARIANTS,
                        Config::validateTextureVariants);

        GNOME_OWNER_FOLLOW_RULES = BUILDER
                .comment("Controls how THE GNOME follows its owner")
                .comment("Format: [Follow Range, Stop Distance]")
                .comment("Default: [10.0F, 2.0F] (follows within 10 blocks, stops 2 blocks away)")
                .defineList("owner_follow_rules", DEFAULT_OWNER_FOLLOW_RULES,
                        Config::validateFollowRules);

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
                TheGnomeMod.LOG.warn("Invalid ambient interval: min must be >= 0 and max must be >= min. Using defaults.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            TheGnomeMod.LOG.warn("Invalid ambient interval format. Using defaults.");
            return false;
        }
    }

    private static boolean validateTextureVariants(Object obj) {
        if (!(obj instanceof List)) return false;

        try {
            List<?> list = (List<?>) obj;
            if (list.size() != 2) return false;

            int start = Integer.parseInt(list.get(0).toString());
            int end = Integer.parseInt(list.get(1).toString());

            if (start < 0 || end < 0 || end < start || end > 2) {
                TheGnomeMod.LOG.warn("Invalid texture variants range: must be between 0-2, and start can't be higher than end. Using defaults.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            TheGnomeMod.LOG.warn("Invalid texture variants format. Using defaults.");
            return false;
        }
    }

    private static boolean validateFollowRules(Object obj) {
        if (!(obj instanceof List)) return false;

        try {
            List<?> list = (List<?>) obj;
            if (list.size() != 2) return false;

            float followRange = Float.parseFloat(list.get(0).toString());
            float stopDistance = Float.parseFloat(list.get(1).toString());

            if (stopDistance < 0.0F || stopDistance >= followRange) {
                TheGnomeMod.LOG.warn("Invalid follow rules: range must be > 0, stop distance must be >= 0, and stop distance must be < range. Using defaults.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            TheGnomeMod.LOG.warn("Invalid follow rules format. Using defaults.");
            return false;
        }
    }

    // Getter methods for enhanced readability
    public static class GnomeSettings {
        // Texture variants access
        public static int getTextureVariantStart() {
            try {
                List<? extends Integer> variants = GNOME_TEXTURE_VARIANTS.get();
                return variants != null && variants.size() == 2 ? variants.get(0) : DEFAULT_TEXTURE_VARIANTS.get(0);
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
                TheGnomeMod.LOG.debug("Config not loaded yet, using default texture variant start");
                return DEFAULT_TEXTURE_VARIANTS.get(0);
            }
        }

        public static int getTextureVariantEnd() {
            try {
                List<? extends Integer> variants = GNOME_TEXTURE_VARIANTS.get();
                return variants != null && variants.size() == 2 ? variants.get(1) : DEFAULT_TEXTURE_VARIANTS.get(1);
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
                TheGnomeMod.LOG.debug("Config not loaded yet, using default texture variant end");
                return DEFAULT_TEXTURE_VARIANTS.get(1);
            }
        }

        // Ambient interval access
        public static int getAmbientIntervalMin() {
            try {
                List<? extends Integer> interval = GNOME_AMBIENT_INTERVAL.get();
                return interval != null && interval.size() == 2 ? interval.get(0) : DEFAULT_AMBIENT_INTERVAL.get(0);
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
                TheGnomeMod.LOG.debug("Config not loaded yet, using default ambient interval min");
                return DEFAULT_AMBIENT_INTERVAL.get(0);
            }
        }

        public static int getAmbientIntervalMax() {
            try {
                List<? extends Integer> interval = GNOME_AMBIENT_INTERVAL.get();
                return interval != null && interval.size() == 2 ? interval.get(1) : DEFAULT_AMBIENT_INTERVAL.get(1);
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
                TheGnomeMod.LOG.debug("Config not loaded yet, using default ambient interval max");
                return DEFAULT_AMBIENT_INTERVAL.get(1);
            }
        }

        // Follow rules access
        public static float getOwnerFollowRange() {
            try {
                List<? extends Number> rules = GNOME_OWNER_FOLLOW_RULES.get();
                return rules != null && rules.size() == 2 ? rules.get(0).floatValue() : DEFAULT_OWNER_FOLLOW_RULES.get(0);
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
                TheGnomeMod.LOG.debug("Config not loaded yet, using default follow range");
                return DEFAULT_OWNER_FOLLOW_RULES.get(0);
            }
        }

        public static float getOwnerFollowStop() {
            try {
                List<? extends Number> rules = GNOME_OWNER_FOLLOW_RULES.get();
                return rules != null && rules.size() == 2 ? rules.get(1).floatValue() : DEFAULT_OWNER_FOLLOW_RULES.get(1);
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
                TheGnomeMod.LOG.debug("Config not loaded yet, using default follow stop");
                return DEFAULT_OWNER_FOLLOW_RULES.get(1);
            }
        }

        public static boolean canRandomlyTeleport() {
            try {
                return GNOME_RANDOM_TELEPORTATION.get();
            } catch (IllegalStateException e) {
                // Config not loaded yet, use default
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