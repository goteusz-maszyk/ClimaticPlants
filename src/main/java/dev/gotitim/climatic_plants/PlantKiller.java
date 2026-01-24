package dev.gotitim.climatic_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PlantKiller {
    public static boolean tryKillSapling(Holder<Biome> biome, float[] tempRange, BlockPos pos, Level world) {
        if (biome.is(BiomeTags.IS_OVERWORLD)) {
            float tempValue = TemperatureHandler.getTemperature(biome.value(), pos);
            float min = tempRange[0];
            float max = tempRange[1];

            var freezeVal = min - tempValue;
            var overheatVal = tempValue - max;

            if (freezeVal > 0 && randomDeath(freezeVal)) {
                killSapling(world, pos, -freezeVal);
                return true;
            }
            if (overheatVal > 0 && randomDeath(overheatVal)) {
                killSapling(world, pos, overheatVal);
                return true;
            }
        } else if (biome.is(BiomeTags.IS_END)) {
            killSapling(world, pos, -1f);
            return true;
        } else if (biome.is(BiomeTags.IS_NETHER)) {
            killSapling(world, pos, 2.5f);
            return true;
        }
        return false;
    }

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
        }
        world.playSound(null, pos, SoundEvents.CHERRY_SAPLING_BREAK, SoundSource.BLOCKS, 1f, pitch);
    }


    public static void killFungus(ServerLevel world, BlockPos pos) {
        world.setBlockAndUpdate(pos, ModBlocks.DEAD_FUNGUS.defaultBlockState());
        world.playSound(null, pos, SoundEvents.FUNGUS_BREAK, SoundSource.BLOCKS, 1f, 1.5f);
    }

    public static boolean randomDeath(float difference) {
        var r = Math.random();
        System.out.println(r);
        return difference/ConfigUtils.CONFIG.sapling_survival_margin > r;
    }

    public static boolean tryCancelCrop(Level level, BlockPos pos, Block block) {
        Holder<Biome> biome = level.getBiomeManager().getBiome(pos);
        if (!biome.is(BiomeTags.IS_OVERWORLD)){
            return true;
        }

        var tempRange = ConfigUtils.getTemperatureRange(block);
        float tempValue = TemperatureHandler.getTemperature(biome.value(), pos);
        float min = tempRange[0];
        float max = tempRange[1];

        var freezeVal = min - tempValue;
        var overheatVal = tempValue - max;

        if (freezeVal > 0)
            if (freezeVal < ConfigUtils.CONFIG.crop_survival_margin) {
                if (Math.random() > freezeVal / ConfigUtils.CONFIG.crop_survival_margin) {
                    return true;
                }
            } else {
                if (block instanceof CropBlock) {
                    level.setBlockAndUpdate(pos, ModBlocks.FROZEN_CROP.defaultBlockState());
                }
                return true;
            }

        if (overheatVal > 0)
            if (overheatVal < ConfigUtils.CONFIG.crop_survival_margin) {
                return Math.random() > overheatVal / ConfigUtils.CONFIG.crop_survival_margin;
            } else {
                if (block instanceof CropBlock) {
                    level.setBlockAndUpdate(pos, ModBlocks.BURNT_CROP.defaultBlockState());
                }
                return true;
            }
        return false;
    }
}