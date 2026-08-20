package dev.gotitim.climatic_plants.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ClimateRange(
    int minHydration, int maxHydration,
    float minTemperature, float maxTemperature
) {
    public static final Codec<ClimateRange> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("min_hydration", 0).forGetter(c -> c.minHydration),
        Codec.INT.optionalFieldOf("max_hydration", 100).forGetter(c -> c.maxHydration),
        Codec.FLOAT.optionalFieldOf("min_temperature", Float.NEGATIVE_INFINITY).forGetter(c -> c.minTemperature),
        Codec.FLOAT.optionalFieldOf("max_temperature", Float.POSITIVE_INFINITY).forGetter(c -> c.maxTemperature)
    ).apply(i, ClimateRange::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClimateRange> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.minHydration,
        ByteBufCodecs.VAR_INT, c -> c.maxHydration,
        ByteBufCodecs.FLOAT, c -> c.minTemperature,
        ByteBufCodecs.FLOAT, c -> c.maxTemperature,
        ClimateRange::new
    );

    private static final float MC_MIN = -0.7f;
    private static final float MC_MAX = 2.0f;
    private static final float CELSIUS_MIN = -35f;
    private static final float CELSIUS_MAX = 45f;

    public static float celsiusToMinecraft(float celsius) {
        return MC_MIN + (celsius - CELSIUS_MIN) / (CELSIUS_MAX - CELSIUS_MIN) * (MC_MAX - MC_MIN);
    }

    public static float minecraftToCelsius(float minecraftTemp) {
        return CELSIUS_MIN + (minecraftTemp - MC_MIN) / (MC_MAX - MC_MIN) * (CELSIUS_MAX - CELSIUS_MIN);
    }

    public Result checkHydration(int hydration, float margin) {
        return check(hydration, minHydration - margin, maxHydration + margin);
    }

    public Result checkTemperature(float temperature, float margin) {
        return check(temperature, minTemperature - margin, maxTemperature + margin);
    }

    public boolean checkBoth(int hydration, float temperature, float margin) {
        return checkHydration(hydration, margin) == Result.VALID && checkTemperature(temperature, margin) == Result.VALID;
    }

    @NotNull
    private Result check(float value, float min, float max) {
        if (value < min) {
            return Result.LOW;
        }
        if (value > max) {
            return Result.HIGH;
        }
        return Result.VALID;
    }

    public enum Result {
        LOW, VALID, HIGH
    }

    public static final class Builder {
        int minHydration = 0;
        int maxHydration = 100;
        float minTemperature = Float.NEGATIVE_INFINITY;
        float maxTemperature = Float.POSITIVE_INFINITY;

        public Builder minHydration(int min) { return hydration(min, 100); }
        public Builder maxHydration(int max) { return hydration(0, max); }
        public Builder hydration(int min, int max) {
            minHydration = min;
            maxHydration = max;
            return this;
        }

        public Builder minTemperature(float min) { return temperature(min, Float.POSITIVE_INFINITY); }
        public Builder maxTemperature(float max) { return temperature(Float.NEGATIVE_INFINITY, max); }
        public Builder temperature(float minCelsius, float maxCelsius) {
            minTemperature = minCelsius;
            maxTemperature = maxCelsius;
            return this;
        }

        public ClimateRange build() {
            return new ClimateRange(minHydration, maxHydration, minTemperature, maxTemperature);
        }
    }
}
