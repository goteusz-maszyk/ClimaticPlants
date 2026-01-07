package dev.gotitim.climatic_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public class TemperatureHandler {
    private static final double tempSubtractor = 0.00125;
    public static float getTemperature(Biome biome, BlockPos position) {
        float posY = position.getY();

        double tempBioma = 0;

        if (posY <= 80) {
            tempBioma = biome.getBaseTemperature();
        }
        else if (posY >= 81) {
            int height = (int)posY - 81;
            double totalResta = height * tempSubtractor;
            tempBioma = biome.getBaseTemperature() - totalResta;
        }

        return (float)tempBioma;
    }
}