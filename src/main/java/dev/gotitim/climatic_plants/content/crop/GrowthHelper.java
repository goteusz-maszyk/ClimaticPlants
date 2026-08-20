package dev.gotitim.climatic_plants.content.crop;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.data.ClimateRange;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class GrowthHelper {
    public static boolean allowVanillaCropGrowth(Level level, BlockPos blockPos, BlockState state) {
        Optional<ClimateRange> optClimate = ClimaticPlants.climateRanges.get(state.getBlock());
        if (optClimate.isEmpty()) {
            return true;
        }
        CropBlock cropBlock = (CropBlock) state.getBlock();
        Crop crop = Crop.BY_BLOCK.get(cropBlock);
        if (crop == null) {
            return true;
        }

        if (cropBlock.getAge(state) == cropBlock.getMaxAge()) {
            float growthSpeed = CropBlock.getGrowthSpeed(cropBlock, level, blockPos);
            if (level.getRandom().nextInt((int)(50.0F / growthSpeed) + 1) == 0) {
                killCrop(level, blockPos, crop, true);
            }
            return false;
        }

        ClimateRange climate = optClimate.get();
        Holder<Biome> biome = level.getBiome(blockPos);

        float currentTemp = biome.value().getHeightAdjustedTemperature(blockPos, level.getSeaLevel());
        float currentTempCelsius = ClimateRange.minecraftToCelsius(currentTemp);

        ClimateRange.Result temperatureResult = climate.checkTemperature(currentTempCelsius, 0);

        if (temperatureResult != ClimateRange.Result.VALID) {
            killCrop(level, blockPos, crop, false);
            return false;
        }
        return true;
    }

    public static void killCrop(Level level, BlockPos blockPos, Crop crop, boolean mature) {
        BlockState state = ClimaticBlocks.DEAD_CROPS.get(crop).defaultBlockState().setValue(DeadCropBlock.MATURE, mature);
        level.setBlock(blockPos, state, 3);
    }
}