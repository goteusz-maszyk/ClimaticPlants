package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.data.ClimateRange;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClimateRangeProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ClimateRangeProvider(FabricPackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "climate_range");
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
        Map<Crop, ClimateRange> climates = new HashMap<>();
        climates.put(Crop.WHEAT, new ClimateRange.Builder().hydration(15, 85).temperature(-7, 22).build());
        climates.put(Crop.POTATO, new ClimateRange.Builder().hydration(35, 90).temperature(-7, 22).build());
        climates.put(Crop.CARROT, new ClimateRange.Builder().hydration(15, 85).temperature(-10, 27).build());
        climates.put(Crop.BEETROOT, new ClimateRange.Builder().hydration(10, 70).temperature(-10, 27).build());

        Map<Identifier, ClimateRange> entries = new HashMap<>();
        for (Map.Entry<Crop, ClimateRange> entry : climates.entrySet()) {
            entries.put(BuiltInRegistries.BLOCK.getKey(entry.getKey().block()), entry.getValue());
        }

        return DataProvider.saveAll(cache, ClimateRange.CODEC, this.pathProvider::json, entries);
    }

    @Override
    public @NonNull String getName() {
        return "Climate Ranges";
    }
}
