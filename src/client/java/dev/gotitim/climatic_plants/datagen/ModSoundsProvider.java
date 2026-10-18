package dev.gotitim.climatic_plants.datagen;

import static dev.gotitim.climatic_plants.ClimaticPlants.identifier;
import static dev.gotitim.climatic_plants.content.ClimaticSounds.*;
import static net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder.RegistrationBuilder.ofEvent;
import static net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder.RegistrationType.FILE;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class ModSoundsProvider extends FabricSoundsProvider {
    public ModSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registryLookup, SoundExporter exporter) {
        add(exporter, QUERN_DRAG, "block/quern/drag1", "block/quern/drag2");
        add(exporter, BARREL_OPEN, ofEvent(SoundEvents.STONE_BREAK).pitch(0.9f));
        add(exporter, BARREL_CLOSE, ofEvent(SoundEvents.STONE_PLACE).pitch(0.65f));
    }

    private void add(SoundExporter exporter, SoundEvent event, String... sounds) {
        SoundTypeBuilder builder = SoundTypeBuilder.of()
                .subtitle("sound." + event.location().getNamespace() + "."
                        + event.location().getPath());
        for (String sound : sounds) {
            builder.sound(SoundTypeBuilder.RegistrationBuilder.create(FILE, identifier(sound)));
        }
        exporter.add(event, builder);
    }

    private void add(SoundExporter exporter, SoundEvent event, SoundTypeBuilder.RegistrationBuilder sound) {
        SoundTypeBuilder builder = SoundTypeBuilder.of()
                .subtitle("sound."
                        + event.location().getNamespace()
                        + "."
                        + event.location().getPath());
        builder.sound(sound);
        exporter.add(event, builder);
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}
