package com.alexthw.ars_ip_compat.item;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ImmersiveWarpScroll extends ModItem {

    public final boolean allowCrossDim;

    public ImmersiveWarpScroll(boolean allowCrossDim) {
        super();
        this.allowCrossDim = allowCrossDim;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND)
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

        if (world.isClientSide())
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

        if (player.isShiftKeyDown()) {
            if (stack.getCount() == 1) {
            stack.set(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(Optional.of(player.blockPosition()), player.getCommandSenderWorld().dimension().location().toString(), player.getRotationVector(), this.allowCrossDim));
            } else {
                ItemStack newWarpStack = stack.split(1);
                newWarpStack.set(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(Optional.of(player.blockPosition()), player.getCommandSenderWorld().dimension().location().toString(), player.getRotationVector(), this.allowCrossDim));
                ItemHandlerHelper.giveItemToPlayer(player, newWarpStack);
            }
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.recorded"));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        WarpScrollData data = stack.get(DataComponentRegistry.WARP_SCROLL);
        if (data == null || !data.isValid()) {
            tooltip.add(Component.translatable("ars_nouveau.warp_scroll.no_location"));
            return;
        }
        BlockPos pos = data.pos().orElseGet(null);
        if (pos == null) return;
        tooltip.add(Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
        if (!ServerConfig.ENABLE_WARP_PORTALS.get()) {
            tooltip.add(Component.translatable("ars_nouveau.warp_scroll.disabled_warp_portal").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }


}
