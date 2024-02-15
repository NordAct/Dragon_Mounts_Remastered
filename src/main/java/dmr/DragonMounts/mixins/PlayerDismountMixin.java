package dmr.DragonMounts.mixins;

import dmr.DragonMounts.common.capability.DragonOwnerCapability;
import dmr.DragonMounts.registry.DMRCapability;
import dmr.DragonMounts.server.entity.DMRDragonEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Player.class )
public class PlayerDismountMixin
{
	@Inject( method = "wantsToStopRiding", at = @At( "HEAD" ), cancellable = true )
	protected void wantsToStopRiding(CallbackInfoReturnable<Boolean> ci){
		var player = (Player)(Object)this;
		
		if(player.getControlledVehicle() instanceof DMRDragonEntity){
			DragonOwnerCapability cap = player.getData(DMRCapability.PLAYER_CAPABILITY);
			ci.setReturnValue(cap.shouldDismount);
		}
	}
}
