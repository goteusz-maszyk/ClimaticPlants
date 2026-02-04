package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.network.PlantClimatePayload;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClimaticPlants implements ModInitializer {
    public static final String MOD_ID = "climatic_plants";

    public static final Map<ResourceLocation, PreferredClimate> climates = new HashMap<>();

    @Override
    public void onInitialize() {
        AutoConfig.register(ClimaticPlantsConfig.class, (clazz, factory) -> new JanksonConfigSerializer<>(clazz, factory, Jankson.builder()
                .registerDeserializer(JsonObject.class, PreferredClimate.class, (jsonObject, marshaller1) -> PreferredClimate.deserialise(jsonObject))
                .registerSerializer(PreferredClimate.class, (climate, marshaller) -> climate.serialise())
                .registerDeserializer(JsonPrimitive.class, ResourceLocation.class, (prim, mar) -> ResourceLocation.parse(prim.asString())).build()));

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

        ServerLifecycleEvents.SERVER_STARTING.register(this::registerWorldgenBasedPlants);
        ServerConfigurationConnectionEvents.CONFIGURE.register((packetListener, server) -> ServerConfigurationNetworking.send(packetListener, new PlantClimatePayload(climates)));
    }

    private void registerWorldgenBasedPlants(MinecraftServer server) {
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
            List<Optional<ResourceKey<ConfiguredFeature<?, ?>>>> features;
            switch (block) {
                case SaplingBlock saplingBlock -> {
                    var tree = saplingBlock.treeGrower;
                    features = List.of(tree.tree, tree.megaTree, tree.secondaryTree, tree.secondaryMegaTree, tree.flowers, tree.secondaryFlowers);
                }
                case AzaleaBlock ignored -> {
                    var tree = TreeGrower.AZALEA;
                    features = List.of(tree.tree, tree.megaTree, tree.secondaryTree, tree.secondaryMegaTree, tree.flowers, tree.secondaryFlowers);
                }
                case null, default -> {
                    continue;
                }
            }
            Registry<ConfiguredFeature<?, ?>> configuredFeatures = server.registryAccess().registry(Registries.CONFIGURED_FEATURE).get();
            for (var treeFeature : features) {
                treeFeature.ifPresent(key -> {
                    ConfiguredFeature<?, ?> feature = configuredFeatures.get(key);
                    if (feature == null) {
                        return;
                    }
                    var featureClimate = featureClimates.get(feature.config());
                    if (featureClimate == null) {
                        return;
                    }
                    addBlockClimate(block, featureClimate);
                });
            }
        }
        addBlockClimate(Blocks.NETHER_WART, biomeRegistry.getTag(BiomeTags.IS_NETHER).get());
        addBlockClimate(Blocks.BIRCH_SAPLING, biomeRegistry.get(Biomes.BIRCH_FOREST)); // Adding manually because birch forests generate ungrowable variant
        var config = getConfig();
        for (ResourceLocation blockId : config.ranges.keySet()) {
            climates.put(blockId, config.ranges.get(blockId));
        }
    }

    private void addBlockClimate(Block block, PreferredClimate climate) {
        if (block instanceof BonemealableBlock ||
                block.getStateDefinition().getPossibleStates().stream().anyMatch(BlockBehaviour.BlockStateBase::isRandomlyTicking)) {
            climates.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(block), k -> new PreferredClimate()).add(climate);
        }
    }

    private void addBlockClimate(Block block, HolderSet.Named<Biome> tag) {
        tag.stream().forEach(biomeHolder -> addBlockClimate(block, biomeHolder.value()));
    }

    private void addBlockClimate(Block block, Biome biome) {
        addBlockClimate(block, new PreferredClimate(biome));
    }

    private void processFeature(Map<FeatureConfiguration, PreferredClimate> featureClimates, Biome biome, ConfiguredFeature<?, ? extends Feature<?>> configured) {
        Feature<?> feature = configured.feature();
        FeatureConfiguration config = configured.config();
        switch (feature) {
            case TreeFeature ignored -> {
                var preferredClimate = featureClimates.computeIfAbsent(config, k -> new PreferredClimate(biome));
                preferredClimate.add(biome);
                var treeConfig = (TreeConfiguration) config;
                for (var decorator : treeConfig.decorators) {
                    if (decorator instanceof CocoaDecorator) {
                        addBlockClimate(Blocks.COCOA, biome);
                    }
                }
            }
            case SimpleBlockFeature ignored -> {
                SimpleBlockConfiguration simpleBlockConfiguration = (SimpleBlockConfiguration) config;
                Block block = simpleBlockConfiguration.toPlace().getState(RandomSource.create(), BlockPos.ZERO).getBlock();
                addBlockClimate(block, biome);
            }
            case RandomSelectorFeature ignored -> {
                var randomConfig = (RandomFeatureConfiguration) config;
                for (WeightedPlacedFeature weightedPlacedFeature : randomConfig.features) {
                    ConfiguredFeature<?, ?> configuredFeature = weightedPlacedFeature.feature.value().feature().value();
                    processFeature(featureClimates, biome, configuredFeature);
                }
                processFeature(featureClimates, biome, randomConfig.defaultFeature.value().feature().value());
            }
            case RootSystemFeature ignored -> {
                RootSystemConfiguration rootConfig = (RootSystemConfiguration) config;
                processFeature(featureClimates, biome, rootConfig.treeFeature.value().feature().value());
            }
            case RandomPatchFeature ignored -> {
                RandomPatchConfiguration patchConfig = (RandomPatchConfiguration) config;
                processFeature(featureClimates, biome, patchConfig.feature().value().feature().value());
            }
            case BlockColumnFeature ignored -> {
                BlockColumnConfiguration columnConfig = (BlockColumnConfiguration) config;
                for (BlockColumnConfiguration.Layer layer : columnConfig.layers()) {
                    Block block = layer.state().getState(RandomSource.create(), BlockPos.ZERO).getBlock();
                    addBlockClimate(block, biome);
                }
            }
            case BambooFeature ignored -> {
                addBlockClimate(Blocks.BAMBOO, biome);
                addBlockClimate(Blocks.BAMBOO_SAPLING, biome);
            }
            default -> {}
        }
    }
    public static PreferredClimate getClimate(Block block) {
        return climates.get(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static ClimaticPlantsConfig getConfig() {
        return AutoConfig.getConfigHolder(ClimaticPlantsConfig.class).getConfig();
    }
}
