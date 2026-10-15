package dev.infinitygems;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public class GemItem extends Item {
    private final Gem gem;

    public GemItem(Properties properties, Gem gem) {
        super(properties);
        this.gem = gem;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Auf dem Client nur den Schwung anzeigen, die Logik läuft auf dem Server.
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }

        if (!GemAbilities.activate(gem, serverLevel, serverPlayer)) {
            return InteractionResult.FAIL;
        }

        player.getCooldowns().addCooldown(stack, gem.cooldownTicks());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
            Consumer<Component> builder, TooltipFlag flag) {
        for (int i = 1; i <= gem.tooltipLines; i++) {
            builder.accept(Component.translatable("tooltip.infinitygems." + gem.id + "." + i)
                    .withStyle(ChatFormatting.GRAY));
        }
        if (gem.durationSeconds > 0) {
            builder.accept(Component.translatable("tooltip.infinitygems.duration", gem.durationSeconds)
                    .withStyle(ChatFormatting.AQUA));
        }
        builder.accept(Component.translatable("tooltip.infinitygems.cooldown", gem.cooldownSeconds)
                .withStyle(ChatFormatting.YELLOW));
    }
}
