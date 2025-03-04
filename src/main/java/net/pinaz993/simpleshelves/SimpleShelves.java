package net.pinaz993.simpleshelves;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SimpleShelves implements ModInitializer {
    public static final FabricBlockSettings WOOD_SHELF_SETTINGS = FabricBlockSettings.of(Material.WOOD)
            .strength(1.5f, 1.5f);

    public static final OakShelf OAK_SHELF = new OakShelf(WOOD_SHELF_SETTINGS);
    public static final BirchShelf BIRCH_SHELF = new BirchShelf(WOOD_SHELF_SETTINGS);
    public static final SpruceShelf SPRUCE_SHELF = new SpruceShelf(WOOD_SHELF_SETTINGS);
    public static final JungleShelf JUNGLE_SHELF = new JungleShelf(WOOD_SHELF_SETTINGS);
    public static final AcaciaShelf ACACIA_SHELF = new AcaciaShelf(WOOD_SHELF_SETTINGS);
    public static final DarkOakShelf DARK_OAK_SHELF = new DarkOakShelf(WOOD_SHELF_SETTINGS);
    public static final CrimsonShelf CRIMSON_SHELF = new CrimsonShelf(WOOD_SHELF_SETTINGS);
    public static final WarpedShelf WARPED_SHELF = new WarpedShelf(WOOD_SHELF_SETTINGS);
    public static final Item REDSTONE_BOOK = new RedstoneBookItem(
            new FabricItemSettings().group(ItemGroup.REDSTONE).maxCount(15));
    public static BlockEntityType<ShelfEntity> SHELF_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        // Register Blocks
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "oak_shelf"), OAK_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "birch_shelf"), BIRCH_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "spruce_shelf"), SPRUCE_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "jungle_shelf"), JUNGLE_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "acacia_shelf"), ACACIA_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "dark_oak_shelf"), DARK_OAK_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "crimson_shelf"), CRIMSON_SHELF);
        Registry.register(Registry.BLOCK, new Identifier("simple_shelves", "warped_shelf"), WARPED_SHELF);

        // Register BlockItems
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "oak_shelf"), // OAK_SHELF
                new BlockItem(OAK_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "birch_shelf"), //BIRCH SHELF
                new BlockItem(BIRCH_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "spruce_shelf"), //SPRUCE SHELF
                new BlockItem(SPRUCE_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "jungle_shelf"), //JUNGLE SHELF
                new BlockItem(JUNGLE_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "acacia_shelf"), //ACACIA SHELF
                new BlockItem(ACACIA_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "dark_oak_shelf"), //DARK OAK SHELF
                new BlockItem(DARK_OAK_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "crimson_shelf"), //CRIMSON SHELF
                new BlockItem(CRIMSON_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "warped_shelf"), //WARPED SHELF
                new BlockItem(WARPED_SHELF, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        // Register Items
        Registry.register(Registry.ITEM, new Identifier("simple_shelves", "redstone_book"), REDSTONE_BOOK);

        // Register BlockEntity
        SHELF_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE, "simple_shelves:shelf_block_entity",
                FabricBlockEntityTypeBuilder.create(ShelfEntity::new,
                        OAK_SHELF, BIRCH_SHELF, SPRUCE_SHELF, JUNGLE_SHELF,
                        ACACIA_SHELF, DARK_OAK_SHELF, CRIMSON_SHELF, WARPED_SHELF
                ).build(null));
    }
}