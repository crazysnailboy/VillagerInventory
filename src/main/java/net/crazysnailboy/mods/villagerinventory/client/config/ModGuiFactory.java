package net.crazysnailboy.mods.villagerinventory.client.config;

import java.util.List;
import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.common.config.ModConfiguration;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;


public class ModGuiFactory extends DefaultGuiFactory
{

	public ModGuiFactory()
	{
		super(VillagerInventoryMod.MODID, VillagerInventoryMod.NAME);
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new GuiConfig(parentScreen, getConfigElements(), VillagerInventoryMod.MODID, false, false, this.title);
	}

	private static List<IConfigElement> getConfigElements()
	{
		Configuration config = ModConfiguration.getConfig();
		List<IConfigElement> list = new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
		return list;
	}

}
