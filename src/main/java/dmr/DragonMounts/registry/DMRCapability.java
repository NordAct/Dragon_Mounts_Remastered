package dmr.DragonMounts.registry;

import dmr.DragonMounts.common.capability.DragonOwnerCapability;
import dmr.DragonMounts.network.NetworkHandler;
import dmr.DragonMounts.network.packets.CompleteDataSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static dmr.DragonMounts.DragonMountsRemaster.MOD_ID;

@Mod.EventBusSubscriber( modid = MOD_ID, bus = Bus.FORGE )
public class DMRCapability
{
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MOD_ID);
	
	public static Supplier<AttachmentType<DragonOwnerCapability>> PLAYER_CAPABILITY = ATTACHMENT_TYPES.register("dragon_owner", () -> AttachmentType.serializable(DragonOwnerCapability::new).copyOnDeath().build());
	
	
	@SubscribeEvent
	public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent loggedInEvent){
		Player player = loggedInEvent.getEntity();
		player.getData(PLAYER_CAPABILITY).setPlayer(player);
		syncCapability(player);
	}
	
	public static void syncCapability(Player player){
		//player.reviveCaps();
		NetworkHandler.sendToClients(player, new CompleteDataSync(player));
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent playerRespawnEvent){
		Player player = playerRespawnEvent.getEntity();
		player.getData(PLAYER_CAPABILITY).setPlayer(player);
		syncCapability(player);
	}
	
	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event){
		Player player = event.getEntity();
		player.getData(PLAYER_CAPABILITY).setPlayer(player);
		syncCapability(player);
	}
	
	@SubscribeEvent
	public static void onTrackingStart(PlayerEvent.StartTracking startTracking){
		Player trackingPlayer = startTracking.getEntity();
		if(trackingPlayer instanceof ServerPlayer target){
			Entity tracked = startTracking.getTarget();
			if(tracked instanceof ServerPlayer){
				var handler = tracked.getData(PLAYER_CAPABILITY);
				
				if(handler != null){
					NetworkHandler.send(PacketDistributor.PLAYER.with(target), new CompleteDataSync(tracked.getId(), handler.serializeNBT()));
				}
			}
		}
	}
}
