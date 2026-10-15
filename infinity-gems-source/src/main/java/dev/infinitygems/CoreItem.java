package dev.infinitygems;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Der Infinity Core: Crafting-Zutat für den Infinity Gem. */
public class CoreItem extends Item {
    public CoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
            Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.infinitygems.infinity_core.1")
                .withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.infinitygems.infinity_core.2")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
