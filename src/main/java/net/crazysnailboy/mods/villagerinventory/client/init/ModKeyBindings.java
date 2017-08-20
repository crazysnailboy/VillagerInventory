package net.crazysnailboy.mods.villagerinventory.client.init;

import org.lwjgl.input.Keyboard;
import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.common.network.message.EntityFlagMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModKeyBindings
{

	private static final String CATEGORY_GENERAL = "general";

	public static final KeyBinding OPEN_ARMOR_STAND_GUI = createKeyBinding("openVillagerInventoryGui", KeyConflictContext.IN_GAME, Keyboard.KEY_LSHIFT, CATEGORY_GENERAL);


	private static final KeyBinding createKeyBinding(String description, IKeyConflictContext keyConflictContext, int keyCode, String category)
	{
		return new KeyBinding(VillagerInventoryMod.MODID + ".key." + description, keyConflictContext, keyCode, VillagerInventoryMod.MODID + ".key.categories." + category);
	}


	public static void registerKeyBindings()
	{
		ClientRegistry.registerKeyBinding(OPEN_ARMOR_STAND_GUI);
	}


	@EventBusSubscriber(value = Side.CLIENT)
	public static class EventHandlers
	{

		private static boolean previousState = false;

		@SubscribeEvent
		public static void clientTick(final TickEvent.ClientTickEvent event)
		{
			if (event.phase != TickEvent.Phase.END) return;

			boolean currentState = OPEN_ARMOR_STAND_GUI.isKeyDown();
			if (currentState != previousState)
			{
				VillagerInventoryMod.NETWORK.sendToServer(new EntityFlagMessage(Minecraft.getMinecraft().player, 2, currentState));
				previousState = currentState;
			}

		}
	}

}