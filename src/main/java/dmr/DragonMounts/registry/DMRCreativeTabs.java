package dmr.DragonMounts.registry;

import dmr.DragonMounts.DragonMountsRemaster;
import dmr.DragonMounts.server.items.DMRDevItem;
import dmr.DragonMounts.server.items.DragonEggItemBlock;
import dmr.DragonMounts.server.items.DragonSpawnEgg;
import dmr.DragonMounts.types.dragonBreeds.IDragonBreed;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

public class DMRCreativeTabs
{
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DragonMountsRemaster.MOD_ID);

	
	public static Supplier<CreativeModeTab> MOD_HYBRIDS_DEV_TAB;
	public static Supplier<CreativeModeTab> MOD_TESTING_DEV_TAB;
	
	public static final Supplier<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("dragon_mounts",
	                                                                                          () -> CreativeModeTab.builder().icon(() -> new ItemStack(DMRItems.DRAGON_EGG_BLOCK_ITEM.get())).title(Component.translatable("itemGroup.dragon_mounts")).displayItems((enabledFeatures, entries) -> {
		                                                                                          var breeds = DragonBreedsRegistry.getDragonBreeds();
		                                                                                          for (IDragonBreed type : breeds) {
			                                                                                          if (!type.isHybrid()) entries.accept(DragonEggItemBlock.getDragonEggStack(type));
		                                                                                          }
		                                                                                          for (IDragonBreed type : breeds) {
			                                                                                          if (!type.isHybrid()) entries.accept(DragonSpawnEgg.create(type));
		                                                                                          }
	                                                                                          }).build());
	
	public static void init()
	{
		if(DragonMountsRemaster.DEBUG){
			MOD_HYBRIDS_DEV_TAB = CREATIVE_MODE_TABS.register("dragon_mounts_hybrids_dev", () -> CreativeModeTab.builder().icon(() -> new ItemStack(DMRItems.DRAGON_EGG_BLOCK_ITEM.get()))
					.title(Component.literal("DMR Hybrid eggs")).displayItems(
					(enabledFeatures, entries) -> {
						var breeds = new ArrayList<>(DragonBreedsRegistry.getDragonBreeds()).stream().filter(IDragonBreed::isHybrid).sorted(Comparator.comparing(IDragonBreed::getId)).toList();
						
						for (IDragonBreed type : breeds) entries.accept(DragonEggItemBlock.getDragonEggStack(type));
						for (IDragonBreed type : breeds) entries.accept(DragonSpawnEgg.create(type));
					}).build());
			
			MOD_TESTING_DEV_TAB = CREATIVE_MODE_TABS.register("dragon_mounts_testing_dev", () -> CreativeModeTab.builder()
					.icon(() -> new ItemStack(DMRItems.DRAGON_EGG_BLOCK_ITEM.get()))
					.title(Component.literal("DMR Testing items"))
					.displayItems((enabledFeatures, entries) -> DMRItems.ITEMS.getEntries().forEach(supplier -> {
						var item = supplier.get();
						
						if(item instanceof DMRDevItem) {
							entries.accept(item.getDefaultInstance());
						}
					}))
					.build());
		}
	}
}
