package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.fluid.YeastFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;

public class ClimaticFluids {
    public static final YeastFluid YEAST = register(Ids.YEAST, new YeastFluid());

    private static <T extends Fluid> T register(ResourceKey<Fluid> key, T fluid) {
        return Registry.register(BuiltInRegistries.FLUID, key, fluid);
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }

    public static class Ids {
        public static final ResourceKey<Fluid> YEAST = create("yeast");

        private static ResourceKey<Fluid> create(String name) {
            return ResourceKey.create(Registries.FLUID, ClimaticPlants.identifier(name));
        }
    }
}
