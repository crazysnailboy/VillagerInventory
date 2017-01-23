package net.crazysnailboy.mods.villagerinventory.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.client.config.ModGuiConfigEntries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfiguration 
{
	private static Configuration config = null;
	private static ConfigEventHandler configEventHandler = new ConfigEventHandler();

	public static boolean enableInventoryGui;
	public static boolean enableDeathDrops;
	
	
	public static void preInit()
	{
		File configFile = new File(Loader.instance().getConfigDir(), VillagerInventoryMod.MODID + ".cfg");
		config = new Configuration(configFile);
		config.load();
		syncFromFile();
	}
	
	public static void clientPreInit() 
	{
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
	}
	
	
	public static Configuration getConfig() {
		return config;
	}
	
	
	public static void syncFromFile() {
		syncConfig(true, true);
	}

	public static void syncFromGUI() {
		syncConfig(false, true);
	}

	public static void syncFromFields() {
		syncConfig(false, false);
	}
	

	
	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig) {	
		
		if (loadConfigFromFile) {
			config.load();
		}
		
		Property propEnableInventoryGUI = config.get(Configuration.CATEGORY_GENERAL, "enableInventoryGui", true, "");
		propEnableInventoryGUI.setLanguageKey("options.enableInventoryGui");
		propEnableInventoryGUI.setRequiresMcRestart(false);
		
		Property propEnableDeathDrops = config.get(Configuration.CATEGORY_GENERAL, "enableDeathDrops", true, "");
		propEnableDeathDrops.setLanguageKey("options.enableDeathDrops");
		propEnableDeathDrops.setRequiresMcRestart(false);

		
		try
		{
			propEnableInventoryGUI.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propEnableDeathDrops.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			
			List<String> propOrderGeneral = new ArrayList<String>();
			propOrderGeneral.add(propEnableInventoryGUI.getName());
			propOrderGeneral.add(propEnableDeathDrops.getName());
			config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrderGeneral);
			
		}
		catch(NoClassDefFoundError e) { }

		
		if (readFieldsFromConfig) {
			
			enableInventoryGui = propEnableInventoryGUI.getBoolean();
			enableDeathDrops = propEnableDeathDrops.getBoolean();
			
		}
		
		propEnableInventoryGUI.set(enableInventoryGui);
		propEnableDeathDrops.set(enableDeathDrops);
		
		
	
		if (config.hasChanged()) {
			config.save();
		}
		
	}
	
	
	public static class ConfigEventHandler 
	{
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) 
		{
			if (VillagerInventoryMod.MODID.equals(event.getModID()) && !event.isWorldRunning())
			{
				syncFromGUI();
			}
		}
	}
	
}
