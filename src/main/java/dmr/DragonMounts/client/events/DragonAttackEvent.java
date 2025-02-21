package dmr.DragonMounts.client.events;

import dmr.DragonMounts.client.handlers.KeyInputHandler;
import dmr.DragonMounts.common.config.DMRConfig;
import dmr.DragonMounts.network.NetworkHandler;
import dmr.DragonMounts.network.packets.DragonAttackPacket;
import dmr.DragonMounts.network.packets.DragonBreathPacket;
import dmr.DragonMounts.server.entity.DMRDragonEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.TimeUnit;

@EventBusSubscriber( bus = EventBusSubscriber.Bus.FORGE )
public class DragonAttackEvent
{
	private static Long lastAttack = null;
	
	@SubscribeEvent
	public static void onClickEvent(InteractionKeyMappingTriggered event)
	{
		if(DMRConfig.USE_ALTERNATE_ATTACK_KEY.get() && !KeyInputHandler.ATTACK_KEY.isDown()) return;
		
		var player = Minecraft.getInstance().player;
		if (player != null) {
			if (player.getControlledVehicle() instanceof DMRDragonEntity dragon) {
				if (lastAttack == null || System.currentTimeMillis() - lastAttack > TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)) {
					lastAttack = System.currentTimeMillis();
					
					if (event.isAttack()) {
						event.setCanceled(true);
						event.setSwingHand(false);
						
						NetworkHandler.send(PacketDistributor.SERVER.noArg(), new DragonAttackPacket(dragon.getId()));
					} else if (event.isUseItem()) {
						event.setCanceled(true);
						event.setSwingHand(false);
						
						NetworkHandler.send(PacketDistributor.SERVER.noArg(), new DragonBreathPacket(dragon.getId()));
					}
				}
			}
		}
	}
}
