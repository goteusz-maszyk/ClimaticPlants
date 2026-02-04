package dev.gotitim.climatic_plants;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonElement;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;

public final class PreferredClimate {
    public static final float CLIMATE_MARGIN = 0.1f;
    public float minTemperature;
    public float maxTemperature;
    public float minDownfall;
    public float maxDownfall;

    public PreferredClimate(Biome biome) {
        this.minTemperature = biome.climateSettings.temperature() - CLIMATE_MARGIN;
        this.maxTemperature = biome.climateSettings.temperature() + CLIMATE_MARGIN;
        this.minDownfall = biome.climateSettings.downfall() - CLIMATE_MARGIN;
        this.maxDownfall = biome.climateSettings.downfall() + CLIMATE_MARGIN;
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
            minTemperature = biome.getBaseTemperature() - CLIMATE_MARGIN;
        }
        if (biome.climateSettings.temperature() > maxTemperature) {
            maxTemperature = biome.getBaseTemperature() + CLIMATE_MARGIN;
        }
        if (biome.climateSettings.downfall() < minDownfall) {
            minDownfall = biome.climateSettings.downfall() - CLIMATE_MARGIN;
        }
        if (biome.climateSettings.downfall() > maxDownfall) {
            maxDownfall = biome.climateSettings.downfall() + CLIMATE_MARGIN;
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

    public static PreferredClimate deserialise(JsonObject jsonObject) {
        float minTemperature = jsonObject.getFloat("minTemperature", -Float.MAX_VALUE);
        float maxTemperature = jsonObject.getFloat("maxTemperature", Float.MAX_VALUE);
        float minDownfall = jsonObject.getFloat("minDownfall", -Float.MAX_VALUE);
        float maxDownfall = jsonObject.getFloat("maxDownfall", Float.MAX_VALUE);
        System.out.println("Deserialized PreferredClimate: " + minTemperature + ", " + maxTemperature + ", " + minDownfall + ", " + maxDownfall);
        return new PreferredClimate(minTemperature, maxTemperature, minDownfall, maxDownfall);
    }

    public JsonElement serialise() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("minTemperature", new JsonPrimitive(minTemperature));
        jsonObject.put("maxTemperature", new JsonPrimitive(maxTemperature));
        jsonObject.put("minDownfall", new JsonPrimitive(minDownfall));
        jsonObject.put("maxDownfall", new JsonPrimitive(maxDownfall));
        return jsonObject;
    }
}
