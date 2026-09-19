package dev.gotitim.climatic_plants.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.function.Predicate;

public final class FluidStack implements Predicate<SingleFluidStorage> {
    public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

    public static final MapCodec<FluidStack> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidVariant.CODEC.fieldOf("fluid").forGetter(FluidStack::fluidVariant),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStack::amount)
    ).apply(i, FluidStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = StreamCodec.composite(
            FluidVariant.PACKET_CODEC, FluidStack::fluidVariant, ByteBufCodecs.VAR_INT, FluidStack::amount,
            FluidStack::new
    );
    private final FluidVariant fluidVariant;
    private final int amount;

    public FluidStack(FluidVariant fluidVariant, int amount) {
        this.fluidVariant = fluidVariant;
        this.amount = amount;
    }

    public static FluidStack of(Fluid fluid, int amount) {
        return new FluidStack(FluidVariant.of(fluid), amount);
    }

    public boolean test(SingleFluidStorage storage) {
        return fluidVariant.isBlank() || (fluidVariant.is(
                storage.getResource().getFluid()) && amount <= storage.amount);
    }

    public FluidVariant fluidVariant() {
        return fluidVariant;
    }

    public int amount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof FluidStack stack)) return false;
        return fluidVariant.equals(stack.fluidVariant) && this.amount == stack.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluidVariant, amount);
    }

    @Override
    public String toString() {
        return "FluidStack[" +
                "fluidVariant=" + fluidVariant + ", " +
                "amount=" + amount + ']';
    }
}
