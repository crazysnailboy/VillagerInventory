package net.crazysnailboy.mods.villagerinventory.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.client.config.ModGuiConfigEntries;
import net.crazysnailboy.mods.villagerinventory.common.network.ConfigSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ModConfiguration
{

	private static Configuration config = null;

	public static boolean enableInventoryGui = true;
	public static boolean enableDeathDrops = true;
	public static boolean requireEmptyHand = false;


	public static void initializeConfiguration()
	{
		File configFile = new File(Loader.instance().getConfigDir(), VillagerInventoryMod.MODID + ".cfg");
		config = new Configuration(configFile);
		config.load();
		syncFromFile();
	}

	public static Configuration getConfig()
	{
		return config;
	}


	public static void syncFromFile()
	{
		syncConfig(true, true);
	}

	public static void syncFromGUI()
	{
		syncConfig(false, true);
	}


	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{

		if (loadConfigFromFile)
		{
			config.load();
		}

		Property propEnableInventoryGUI = config.get(Configuration.CATEGORY_GENERAL, "enableInventoryGui", true, "");
		propEnableInventoryGUI.setLanguageKey("options.enableInventoryGui");
		propEnableInventoryGUI.setRequiresMcRestart(false);

		Property propRequireEmptyHand = config.get(Configuration.CATEGORY_GENERAL, "requireEmptyHand", false, "");
		propRequireEmptyHand.setLanguageKey("options.requireEmptyHand");
		propRequireEmptyHand.setRequiresMcRestart(false);

		Property propEnableDeathDrops = config.get(Configuration.CATEGORY_GENERAL, "enableDeathDrops", true, "");
		propEnableDeathDrops.setLanguageKey("options.enableDeathDrops");
		propEnableDeathDrops.setRequiresMcRestart(false);


		try
		{
			propEnableInventoryGUI.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propRequireEmptyHand.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propEnableDeathDrops.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);

			List<String> propOrderGeneral = new ArrayList<String>();
			propOrderGeneral.add(propEnableInventoryGUI.getName());
			propOrderGeneral.add(propRequireEmptyHand.getName());
			propOrderGeneral.add(propEnableDeathDrops.getName());
			config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrderGeneral);

		}
		catch (NoClassDefFoundError e)
		{
		}


		if (readFieldsFromConfig)
		{
			enableInventoryGui = propEnableInventoryGUI.getBoolean();
			requireEmptyHand = propRequireEmptyHand.getBoolean();
			enableDeathDrops = propEnableDeathDrops.getBoolean();
		}

		propEnableInventoryGUI.set(enableInventoryGui);
		propRequireEmptyHand.set(requireEmptyHand);
		propEnableDeathDrops.set(enableDeathDrops);

		if (config.hasChanged())
		{
			config.save();
		}

	}


	@EventBusSubscriber
	public static class ConfigEventHandler
	{

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerLoggedInEvent event)
		{
			if (!event.player.world.isRemote)
			{
				VillagerInventoryMod.INSTANCE.getNetwork().sendTo(new ConfigSyncMessage(), (EntityPlayerMP)event.player);
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (event.getModID().equals(VillagerInventoryMod.MODID))
			{
				if (!event.isWorldRunning() || Minecraft.getMinecraft().isSingleplayer())
				{
					syncFromGUI();
					if (event.isWorldRunning() && Minecraft.getMinecraft().isSingleplayer())
					{
						VillagerInventoryMod.INSTANCE.getNetwork().sendToServer(new ConfigSyncMessage());
					}
				}
			}
		}
	}

}
