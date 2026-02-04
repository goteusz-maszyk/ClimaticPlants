package dev.gotitim.climatic_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PlantKiller {
    public static void killSapling(Level world, BlockPos pos, float difference) {
        float pitch;
        BlockState state;
        if (difference < -ConfigUtils.CONFIG.sapling_survival_margin*2.5) {
            state = ModBlocks.FROZEN_BUSH.defaultBlockState();
            pitch = 1.5f;
        } else if (difference > ConfigUtils.CONFIG.sapling_survival_margin*2.5) {
            state = Blocks.DEAD_BUSH.defaultBlockState();
            pitch = 0.5f;
        } else {
            state = ModBlocks.DEAD_SAPLING.defaultBlockState();
            pitch = 1.2f;
        }
        if (state.canSurvive(world, pos)) {
            world.setBlockAndUpdate(pos, state);
            world.playSound(null, pos, SoundEvents.CHERRY_SAPLING_BREAK, SoundSource.BLOCKS, 1f, pitch);
        }
    }


    public static void killFungus(ServerLevel world, BlockPos pos) {
        world.setBlockAndUpdate(pos, ModBlocks.DEAD_FUNGUS.defaultBlockState());
        world.playSound(null, pos, SoundEvents.FUNGUS_BREAK, SoundSource.BLOCKS, 1f, 1.5f);
    }

    public static boolean randomDeath(float difference) {
        return difference/ConfigUtils.CONFIG.sapling_survival_margin > Math.random();
    }

    public static boolean tryCancelGeneral(Level level, BlockPos blockPos, BlockState state) {
        PreferredClimate climate = ClimaticPlants.getClimateNullable(state.getBlock());
        if (climate == null) {
            return false;
        }
        Holder<Biome> biome = level.getBiome(blockPos);
        float currentTemp = biome.value().getHeightAdjustedTemperature(blockPos);
        float downfall = biome.value().climateSettings.downfall();

        var freezeVal = climate.minTemperature - currentTemp;
        var overheatVal = currentTemp - climate.maxTemperature;

        if (state.getBlock() instanceof SaplingBlock) {
            if (freezeVal > 0 && randomDeath(freezeVal)) {
                killSapling(level, blockPos, -freezeVal);
                return true;
            }
            if (overheatVal > 0 && randomDeath(overheatVal)) {
                killSapling(level, blockPos, overheatVal);
                return true;
            }
            return downfall < climate.minDownfall || downfall > climate.maxDownfall;
        }
        if (state.getBlock() instanceof CropBlock) {
            if (freezeVal > 0)
                if (freezeVal < ConfigUtils.CONFIG.crop_survival_margin) {
                    if (Math.random() > freezeVal / ConfigUtils.CONFIG.crop_survival_margin) {
                        return true;
                    }
                } else {
                    level.setBlockAndUpdate(blockPos, ModBlocks.FROZEN_CROP.defaultBlockState());
                    return true;
                }

            if (overheatVal > 0)
                if (overheatVal < ConfigUtils.CONFIG.crop_survival_margin) {
                    return Math.random() > overheatVal / ConfigUtils.CONFIG.crop_survival_margin;
                } else {
                    level.setBlockAndUpdate(blockPos, ModBlocks.BURNT_CROP.defaultBlockState());
                    return true;
                }
            return downfall < climate.minDownfall || downfall > climate.maxDownfall;
        }
        return freezeVal > 0 || overheatVal > 0 || downfall < climate.minDownfall || downfall > climate.maxDownfall;
    }
}