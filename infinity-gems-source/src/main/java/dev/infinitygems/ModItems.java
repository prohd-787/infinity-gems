package dev.infinitygems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    /** Alle Items in Anzeige-Reihenfolge (für den Creative-Tab). */
    public static final List<Item> ALL = new ArrayList<>();

    public static final Item RUBY = register("ruby", p -> new GemItem(p, Gem.RUBY));
    public static final Item SAPPHIRE = register("sapphire", p -> new GemItem(p, Gem.SAPPHIRE));
    public static final Item EMERALD = register("emerald", p -> new GemItem(p, Gem.EMERALD));
    public static final Item AMETHYST = register("amethyst", p -> new GemItem(p, Gem.AMETHYST));
    public static final Item TOPAZ = register("topaz", p -> new GemItem(p, Gem.TOPAZ));
    public static final Item VOID_GEM = register("void_gem", p -> new GemItem(p, Gem.VOID));
    public static final Item INFINITY_CORE = register("infinity_core",
            p -> new CoreItem(p.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final Item INFINITY_GEM = register("infinity_gem",
            p -> new GemItem(p.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true), Gem.INFINITY));

    private ModItems() {
    }

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, InfinityGems.id(name));
        Item.Properties properties = new Item.Properties().setId(key).stacksTo(1).rarity(Rarity.EPIC);
        Item item = factory.apply(properties);
        Registry.register(BuiltInRegistries.ITEM, key, item);
        ALL.add(item);
        return item;
    }

    /** Erzwingt das Laden der Klasse (und damit die Registrierung). */
    public static void init() {
    }
}
