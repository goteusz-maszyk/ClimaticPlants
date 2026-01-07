package dev.gotitim.demandingplants;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.CreativeModeTabs;

public class Demandingplants implements ModInitializer {
    public static final String MOD_ID = "demanding_plants";

    @Override
    public void onInitialize() {
        ModBlocks.init();

        FuelRegistry.INSTANCE.add(ModBlocks.FROZEN_BUSH, 25);
        FuelRegistry.INSTANCE.add(ModBlocks.DEAD_SAPLING, 50);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(tab -> {
                    tab.accept(ModBlocks.FROZEN_BUSH);
                    tab.accept(ModBlocks.DEAD_SAPLING);
                    tab.accept(ModBlocks.DEAD_FUNGUS);
                });
    }
}
