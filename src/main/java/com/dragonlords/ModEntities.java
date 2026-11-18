package com.dragonlords;

import com.dragonlords.entity.DragonEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.EnumMap;
import java.util.Map;

public class ModEntities {
    public static final Map<DragonType, EntityType<DragonEntity>> TYPES = new EnumMap<>(DragonType.class);
    public static final Map<DragonType, Item> EGGS = new EnumMap<>(DragonType.class);

    public static EntityType<DragonEntity> get(DragonType t) { return TYPES.get(t); }

    public static void register() {
        for (DragonType t : DragonType.values()) {
            FabricEntityTypeBuilder.Mob<DragonEntity> b = FabricEntityTypeBuilder.<DragonEntity>createMob()
                    .spawnGroup(MobCategory.CREATURE)
                    .entityFactory(DragonEntity::new)
                    .dimensions(EntityDimensions.scalable(t.scale * 1.2f, t.scale * 1.4f))
                    .trackRangeChunks(10)
                    .trackedUpdateRate(2)
                    .spawnRestriction(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DragonEntity::checkDragonSpawn);
            if (t == DragonType.FIRE) b.fireImmune();
            EntityType<DragonEntity> type = b.build();

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DragonLords.MOD_ID, t.entityName());
            Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
            FabricDefaultAttributeRegistry.register(type, DragonEntity.createAttributes(t));
            TYPES.put(t, type);

            Item egg = Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(DragonLords.MOD_ID, t.entityName() + "_spawn_egg"),
                    new SpawnEggItem(type, t.eggPrimary, t.eggSecondary, new Item.Properties()));
            EGGS.put(t, egg);

            // wild spawns, driven by data/dragonlords/tags/worldgen/biome/<id>_dragon_habitat.json
            TagKey<Biome> habitat = TagKey.create(Registries.BIOME,
                    ResourceLocation.fromNamespaceAndPath(DragonLords.MOD_ID, t.id + "_dragon_habitat"));
            BiomeModifications.addSpawn(BiomeSelectors.tag(habitat), MobCategory.CREATURE, type, 4, 1, 1);
        }
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(e -> EGGS.values().forEach(e::accept));
    }
}
