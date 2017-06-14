package net.crazysnailboy.mods.villagerinventory.client.config;

import java.util.List;
import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.common.config.ModConfiguration;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;


public class ModGuiConfig extends GuiConfig
{

	public ModGuiConfig(GuiScreen parent)
	{
		super(parent, getConfigElements(), VillagerInventoryMod.MODID, false, false, VillagerInventoryMod.NAME);
	}


	private static List<IConfigElement> getConfigElements()
	{
		Configuration config = ModConfiguration.getConfig();
		List<IConfigElement> list = new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
		return list;
	}


	@Override
	public void initGui()
	{
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
	}


}
