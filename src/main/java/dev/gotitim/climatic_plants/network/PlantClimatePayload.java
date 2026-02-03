package dev.gotitim.climatic_plants.network;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.PreferredClimate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record PlantClimatePayload(Map<ResourceLocation, PreferredClimate> data) implements CustomPacketPayload {
    public static final ResourceLocation SUMMON_LIGHTNING_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(ClimaticPlants.MOD_ID, "climate");
    public static final CustomPacketPayload.Type<PlantClimatePayload> ID = new CustomPacketPayload.Type<>(SUMMON_LIGHTNING_PAYLOAD_ID);
    public static final StreamCodec<FriendlyByteBuf, PlantClimatePayload> CODEC = StreamCodec.of(
            PlantClimatePayload::encode,
            PlantClimatePayload::decode
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void encode(FriendlyByteBuf buffer, PlantClimatePayload payload) {
        buffer.writeVarInt(payload.data.size());
        for (var entry : payload.data.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            PreferredClimate climate = entry.getValue();
            buffer.writeFloat(climate.minTemperature);
            buffer.writeFloat(climate.maxTemperature);
            buffer.writeFloat(climate.minDownfall);
            buffer.writeFloat(climate.maxDownfall);
        }
    }

    public static PlantClimatePayload decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, PreferredClimate> data = new HashMap<>();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            ResourceLocation loc = buffer.readResourceLocation();
            PreferredClimate climate = PreferredClimate.of(buffer);
            data.put(loc, climate);
        }
        return new PlantClimatePayload(data);
    }
}
