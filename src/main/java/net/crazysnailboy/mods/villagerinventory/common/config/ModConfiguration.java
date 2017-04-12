package net.crazysnailboy.mods.villagerinventory.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.client.config.ModGuiConfigEntries;
import net.crazysnailboy.mods.villagerinventory.common.network.ConfigSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;


public class ModConfiguration
{
	private static Configuration config = null;

	public static boolean enableInventoryGui = true;
	public static boolean enableDeathDrops = true;
	public static boolean requireEmptyHand = false;


	public static void preInit()
	{
		File configFile = new File(Loader.instance().getConfigDir(), VillagerInventoryMod.MODID + ".cfg");
		config = new Configuration(configFile);
		config.load();
		syncFromFile();
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
	}

	public static void clientPreInit()
	{
		MinecraftForge.EVENT_BUS.register(new ClientConfigEventHandler());
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

	public static void syncFromFields()
	{
		syncConfig(false, false);
	}



	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{

		if (loadConfigFromFile)
		{
			config.load();
		}

		Property propEnableInventoryGUI = config.get(Configuration.CATEGORY_GENERAL, "enableInventoryGui", enableInventoryGui, "");
		propEnableInventoryGUI.setLanguageKey("options.enableInventoryGui");
		propEnableInventoryGUI.setRequiresMcRestart(false);

		Property propRequireEmptyHand = config.get(Configuration.CATEGORY_GENERAL, "requireEmptyHand", requireEmptyHand, "");
		propRequireEmptyHand.setLanguageKey("options.requireEmptyHand");
		propRequireEmptyHand.setRequiresMcRestart(false);

		Property propEnableDeathDrops = config.get(Configuration.CATEGORY_GENERAL, "enableDeathDrops", enableDeathDrops, "");
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
		catch(NoClassDefFoundError e) { }


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




	public static class ConfigEventHandler
	{
		@SubscribeEvent
		public void onPlayerLoggedIn(PlayerLoggedInEvent event)
		{
			if (!event.player.world.isRemote)
			{
				VillagerInventoryMod.INSTANCE.getNetwork().sendTo(new ConfigSyncMessage(), (EntityPlayerMP)event.player);
			}
		}
	}

	public static class ClientConfigEventHandler
	{
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (VillagerInventoryMod.MODID.equals(event.getModID()))
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
