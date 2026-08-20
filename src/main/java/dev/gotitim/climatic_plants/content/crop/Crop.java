package dev.gotitim.climatic_plants.content.crop;

import dev.gotitim.climatic_plants.content.ClimaticItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Crop(String name, Block block, Item seedsItem, Item harvestItem) {
    public static final Crop WHEAT = new Crop("wheat", Blocks.WHEAT, Items.WHEAT_SEEDS, Items.WHEAT);

    public static final Crop CARROT = new Crop("carrots", Blocks.CARROTS, ClimaticItems.CARROT_SEEDS, Items.CARROT);
    public static final Crop BEETROOT = new Crop("beetroots", Blocks.BEETROOTS, Items.BEETROOT_SEEDS, Items.BEETROOT);

    public static final Crop POTATO = new Crop("potatoes", Blocks.POTATOES, Items.POTATO, Items.POTATO);

//    public static final Crop MELON = new Crop();
//    public static final Crop PUMPKIN = new Crop();
//
//    public static final Crop SWEET_BERRIES = new Crop();

//    public static final Crop SUGAR_CANE = new Crop(Blocks.SUGAR_CANE, Items.SUGAR_CANE, Items.SUGAR_CANE);
    public static final List<Crop> ALL_CROPS = List.of(
            WHEAT, CARROT, BEETROOT, POTATO
    );
    public static final Map<Block, Crop> BY_BLOCK = ALL_CROPS.stream().collect(Collectors.toMap(Crop::block, k -> k));
}
