package com.alexthw.ars_ip_compat.immersive_portal;

import com.alexthw.ars_ip_compat.item.ImmersiveWarpScroll;

import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.DQuaternion;

import static qouteall.imm_ptl.core.commands.PortalCommand.reloadPortal;


public class IPCompat {

    public static void createPortal(Entity portal) {

        if (portal != null) {
            //get item entities around the spot and fetch the nearest warp scroll one
            var scrollItemEntity = portal.level().getEntities(portal, portal.getBoundingBox().inflate(4), entity -> entity instanceof ItemEntity ie && (ie.getItem().getItem() instanceof ImmersiveWarpScroll)).stream().findFirst();
            if (scrollItemEntity.isPresent()) {
                Entity entity = scrollItemEntity.get();
                ItemStack scrollStack = ((ItemEntity) entity).getItem();
                WarpScrollData data = scrollStack.get(DataComponentRegistry.WARP_SCROLL);
                if (data == null || data.pos().isEmpty() || !(scrollStack.getItem() instanceof ImmersiveWarpScroll scrollItem)) {
                    return;
                }

                redirectPortalToScroll(portal, scrollItem, data);

            }else{
                // get the closest player to the portal and try to fetch their warp scroll
                var player = portal.level().getNearestPlayer(portal, 16);
                if (player != null) {
                    if (player.getMainHandItem().getItem() instanceof ImmersiveWarpScroll scrollItemMainHand) {
                        ItemStack scrollStack = player.getMainHandItem();
                        WarpScrollData data = scrollStack.get(DataComponentRegistry.WARP_SCROLL);
                        if (data != null && data.pos().isPresent()) {
                            redirectPortalToScroll(portal, scrollItemMainHand, data);
                        }
                    }
                }
            }
        }

    }

    private static void convertArsPortalToImmersivePortal(PortalTile portalTile) {


    }


    private static void redirectPortalToScroll(Entity portal, ImmersiveWarpScroll scrollItem, WarpScrollData data) {
        BlockPos scrollPos = data.pos().get();
        //applies the data from the scroll to the immersive portal entity
        if (portal instanceof Portal immersivePortal) {
            var portalHeight = immersivePortal.getBoundingBox().maxY - immersivePortal.getBoundingBox().minY;
            var portalX = immersivePortal.getBoundingBox().maxX - immersivePortal.getBoundingBox().minX;
            var portalZ = immersivePortal.getBoundingBox().maxZ - immersivePortal.getBoundingBox().minZ;
            float destX = scrollPos.getX();
            float destY = scrollPos.getY();
            float destZ = scrollPos.getZ();
            float rotation = Math.round(data.rotation().y / 90);
            if (portalX < portalZ)
                rotation += 1;
            if (portalHeight >= 1) {
                destY += (float) (portalHeight / 2);
            } else {
                destY += 0.5F;
            }
            if (portalX < 1 || portalX % 2 == 1) {
                destX += 0.5F;
            }
            if (portalZ < 1 || portalZ % 2 == 1) {
                destZ += 0.5F;
            }
            if (Math.abs(rotation % 2) == 1 && (portalX % 2 == 0 || portalZ % 2 == 0)) {
                var trigo = ((rotation - 2) % 4 + 2 == 1);
                if (trigo && portalZ >= 1.0) {
                    destX += 0.5F;
                    destZ += 0.5F;
                } else if (trigo && portalX >= 1.0) {
                    destX += 0.5F;
                    destZ -= 0.5F;
                } else if (!trigo && portalZ >= 1.0) {
                    destX -= 0.5F;
                    destZ += 0.5F;
                } else if (!trigo && portalX >= 1.0) {
                    destX += 0.5F;
                    destZ += 0.5F;
                }
            }
            if (scrollItem.allowCrossDim || data.canTeleportWithDim(portal.level()))
                immersivePortal.setDestinationDimension(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(data.dimension())));
            immersivePortal.setDestination(new Vec3(destX, destY, destZ));
            immersivePortal.setRotationTransformation(new DQuaternion(0, 1, 0, rotation * 90));
            reloadPortal(immersivePortal);
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ArsPortalCommand.register(event.getDispatcher());
    }

}