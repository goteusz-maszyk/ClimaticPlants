package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ClimaticSounds {
    public static final SoundEvent QUERN_DRAG = register("block.quern.drag");

    private static SoundEvent register(String id) {
        Identifier identifier = ClimaticPlants.identifier(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier,
                SoundEvent.createVariableRangeEvent(identifier)
        );
    }

    public static void init() {

    }
}
