package dev.infinitygems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InfinityGems implements ModInitializer {
    public static final String MOD_ID = "infinitygems";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModItems.init();

        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id("gems"));
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.infinitygems"))
                .icon(() -> new ItemStack(ModItems.INFINITY_GEM))
                .displayItems((parameters, output) -> {
                    for (Item item : ModItems.ALL) {
                        output.accept(item);
                    }
                })
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, tab);

        ServerTickEvents.END_SERVER_TICK.register(InfinityGems::passiveEffects);
    }

    /** Passiv: Wer den Rubin (oder den Infinity Gem) in einer Hand hält, bekommt Feuerresistenz. */
    private static void passiveEffects(MinecraftServer server) {
        if (server.getTickCount() % 40 != 0) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (holds(player, ModItems.RUBY) || holds(player, ModItems.INFINITY_GEM)) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false, true));
            }
        }
    }

    private static boolean holds(ServerPlayer player, Item item) {
        return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
    }
}
