package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.network.PlantClimatePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClimaticPlants implements ModInitializer {
    public static final String MOD_ID = "climatic_plants";

    public static final Map<ResourceLocation, PreferredClimate> climates = new HashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.configurationS2C().register(PlantClimatePayload.ID, PlantClimatePayload.CODEC);
        ModBlocks.init();

        FuelRegistry.INSTANCE.add(ModBlocks.FROZEN_BUSH, 25);
        FuelRegistry.INSTANCE.add(ModBlocks.DEAD_SAPLING, 50);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(tab -> {
                    tab.accept(ModBlocks.FROZEN_BUSH);
                    tab.accept(ModBlocks.DEAD_SAPLING);
                    tab.accept(ModBlocks.DEAD_FUNGUS);
                });

        ServerLifecycleEvents.SERVER_STARTING.register(this::serverStarting);
        ServerConfigurationConnectionEvents.CONFIGURE.register((packetListener, server) -> {
            ServerConfigurationNetworking.send(packetListener, new PlantClimatePayload(climates));
        });
    }

    private void serverStarting(MinecraftServer server) {
        Registry<Biome> biomeRegistry =
                server.registryAccess().registryOrThrow(Registries.BIOME);

        Map<FeatureConfiguration, PreferredClimate> featureClimates = new HashMap<>();
        for (Biome biome : biomeRegistry) {
            BiomeGenerationSettings gen = biome.getGenerationSettings();

            for (HolderSet<PlacedFeature> step : gen.features()) {
                for (Holder<PlacedFeature> holder : step) {
                    PlacedFeature feature = holder.value();
                    var configured =
                            feature.feature().value();
                    processFeature(featureClimates, biome, configured);
                }
            }
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof SaplingBlock saplingBlock) {
                var tree = saplingBlock.treeGrower;
                PreferredClimate climate = new PreferredClimate();
                Registry<ConfiguredFeature<?, ?>> configuredFeatures = server.registryAccess().registry(Registries.CONFIGURED_FEATURE).get();
                for (var treeFeature : List.of(tree.tree, tree.megaTree, tree.secondaryTree, tree.secondaryMegaTree, tree.flowers, tree.secondaryFlowers)) {
                    treeFeature.ifPresent(key -> {
                        ConfiguredFeature<?, ?> feature = configuredFeatures.get(key);
                        if (feature != null) {
                            var featureClimate = featureClimates.get(feature.config());
                            if (featureClimate == null) {
                                System.out.println(server.registryAccess().registry(Registries.CONFIGURED_FEATURE).get().getKey(feature));
                                return;
                            }
                            climate.add(featureClimate);
                        }
                    });
                }
                climates.put(BuiltInRegistries.BLOCK.getKey(block), climate);
            }
        }
    }

    private void processFeature(Map<FeatureConfiguration, PreferredClimate> featureClimates, Biome biome, ConfiguredFeature<?, ? extends Feature<?>> configured) {
        Feature<?> feature = configured.feature();
        FeatureConfiguration config = configured.config();
        if (feature instanceof RandomSelectorFeature) {
            var randomConfig = (RandomFeatureConfiguration) config;
            for (WeightedPlacedFeature weightedPlacedFeature : randomConfig.features) {
                ConfiguredFeature<?, ?> configuredFeature  = weightedPlacedFeature.feature.value().feature().value();
                processFeature(featureClimates, biome, configuredFeature);
            }
            processFeature(featureClimates, biome, randomConfig.defaultFeature.value().feature().value());
        }
        if (feature instanceof TreeFeature) {
            var preferredClimate = featureClimates.computeIfAbsent(config, k -> new PreferredClimate(biome));
            preferredClimate.add(biome);
        }
    }

    public static PreferredClimate getClimate(Block block) {
        return climates.getOrDefault(BuiltInRegistries.BLOCK.getKey(block), PreferredClimate.of(ConfigUtils.getTemperatureRange(block)));
    }
}
