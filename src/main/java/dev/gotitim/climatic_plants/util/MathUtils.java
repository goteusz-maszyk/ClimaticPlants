package dev.gotitim.climatic_plants.util;

import net.minecraft.util.RandomSource;

public class MathUtils {
    public static float triangleRandom(RandomSource random)
    {
        return random.nextFloat() - random.nextFloat() * 0.5f;
    }
}
