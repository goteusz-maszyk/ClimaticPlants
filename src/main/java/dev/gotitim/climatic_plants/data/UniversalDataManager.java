package dev.gotitim.climatic_plants.data;

import com.mojang.serialization.Codec;
import dev.gotitim.climatic_plants.ClimaticPlants;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UniversalDataManager<T> extends SimpleJsonResourceReloadListener<T> {
    private final Map<Identifier, T> data = new HashMap<>();
    private final String id;
    private final T defaultValue;

    public UniversalDataManager(String id, Codec<T> codec, T defaultValue) {
        super(codec, new FileToIdConverter(id, ".json"));
        this.id = id;
        this.defaultValue = defaultValue;
    }

    @Override
    protected void apply(@NonNull Map<Identifier, T> preparations, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        data.clear();
        data.putAll(preparations);
    }

    public void register() {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(ClimaticPlants.identifier(id), this);
    }

    public Optional<T> get(Identifier identifier) {
        return Optional.ofNullable(data.get(identifier));
    }

    public T getDefaulted(Identifier identifier) {
        return data.getOrDefault(identifier, defaultValue);
    }

    public String getPath() {
        return id;
    }

    public Optional<T> get(Block block) {
        return get(BuiltInRegistries.BLOCK.getResourceKey(block).map(ResourceKey::identifier).orElseThrow());
    }
}