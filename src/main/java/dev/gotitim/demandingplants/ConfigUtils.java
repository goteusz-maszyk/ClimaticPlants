package dev.gotitim.demandingplants;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class ConfigUtils {
    public static DemandingPlantsConfig CONFIG;
    static {
        AutoConfig.register(DemandingPlantsConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DemandingPlantsConfig.class).getConfig();
    }

    public static float[] getTemperatureRange(Block plant) {
        return CONFIG.ranges.getOrDefault(BuiltInRegistries.BLOCK.getKey(plant), CONFIG.default_range);
    }
}