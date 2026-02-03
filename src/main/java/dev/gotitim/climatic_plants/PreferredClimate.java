package dev.gotitim.climatic_plants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;

public final class PreferredClimate {
    public float minTemperature;
    public float maxTemperature;
    public float minDownfall;
    public float maxDownfall;

    public PreferredClimate(Biome biome) {
        this.minTemperature = biome.climateSettings.temperature();
        this.maxTemperature = biome.climateSettings.temperature();
        this.minDownfall = biome.climateSettings.downfall();
        this.maxDownfall = biome.climateSettings.downfall();
    }
    public PreferredClimate() {
        this.minTemperature = Float.MAX_VALUE;
        this.maxTemperature = Float.MIN_VALUE;
        this.minDownfall = Float.MAX_VALUE;
        this.maxDownfall = Float.MIN_VALUE;
    }

    public PreferredClimate(float minTemperature, float maxTemperature, float minDownfall, float maxDownfall) {
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minDownfall = minDownfall;
        this.maxDownfall = maxDownfall;
    }

    public static PreferredClimate of(FriendlyByteBuf buffer) {
        return new PreferredClimate(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat()
        );
    }

    public static PreferredClimate of(float[] temperatureRange) {
        return new PreferredClimate(
                temperatureRange[0],
                temperatureRange[1],
                Float.MIN_VALUE,
                Float.MAX_VALUE
        );
    }

    public void add(PreferredClimate preferredClimate) {
        if (preferredClimate.maxTemperature < minTemperature) {
            minTemperature = preferredClimate.minTemperature;
        }
        if (preferredClimate.maxTemperature > maxTemperature) {
            maxTemperature = preferredClimate.maxTemperature;
        }
        if (preferredClimate.maxDownfall > maxDownfall) {
            maxDownfall = preferredClimate.maxDownfall;
        }
        if (preferredClimate.minDownfall < minDownfall) {
            minDownfall = preferredClimate.minDownfall;
        }
    }

    public void add(Biome biome) {
        if (biome.climateSettings.temperature() < minTemperature) {
            minTemperature = biome.getBaseTemperature();
        }
        if (biome.climateSettings.temperature() > maxTemperature) {
            maxTemperature = biome.getBaseTemperature();
        }
        if (biome.climateSettings.downfall() > maxDownfall) {
            maxDownfall = biome.climateSettings.downfall();
        }
        if (biome.climateSettings.downfall() < minDownfall) {
            minDownfall = biome.climateSettings.downfall();
        }
    }

    @Override
    public String toString() {
        return "PreferredClimate{temperature=" +
                minTemperature + ".." + maxTemperature +
                ", downfall=" +
                minDownfall + ".." + maxDownfall +
                "}";

    }
}
