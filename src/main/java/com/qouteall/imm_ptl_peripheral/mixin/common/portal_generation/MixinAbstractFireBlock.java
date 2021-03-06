package com.qouteall.imm_ptl_peripheral.mixin.common.portal_generation;

import com.qouteall.hiding_in_the_bushes.O_O;
import com.qouteall.imm_ptl_peripheral.portal_generation.IntrinsicPortalGeneration;
import com.qouteall.immersive_portals.Global;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.AreaHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(AbstractFireBlock.class)
public class MixinAbstractFireBlock {
    @Redirect(
        method = "onBlockAdded",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/dimension/AreaHelper;method_30485(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;"
        )
    )
    Optional<AreaHelper> redirectCreateAreaHelper(WorldAccess worldAccess, BlockPos blockPos, Direction.Axis axis) {
        if (Global.netherPortalMode == Global.NetherPortalMode.vanilla) {
            return AreaHelper.method_30485(worldAccess, blockPos, axis);
        }
        
        if (isNearObsidian(worldAccess, blockPos)) {
            IntrinsicPortalGeneration.onFireLitOnObsidian(
                ((ServerWorld) worldAccess),
                blockPos
            );
        }
        
        return Optional.empty();
    }
    
    private static boolean isNearObsidian(WorldAccess access, BlockPos blockPos) {
        for (Direction value : Direction.values()) {
            if (O_O.isObsidian(access.getBlockState(blockPos.offset(value)))) {
                return true;
            }
        }
        return false;
    }
}
