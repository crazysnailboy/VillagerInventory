package net.crazysnailboy.mods.villagerinventory.client.config;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ButtonEntry;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.IConfigElement;


public class ModGuiConfigEntries
{

	public static class BooleanEntry extends ButtonEntry
	{

		protected final boolean beforeValue;
		protected boolean currentValue;

		public BooleanEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
		{
			super(owningScreen, owningEntryList, configElement);
			this.beforeValue = Boolean.valueOf(configElement.get().toString());
			this.currentValue = this.beforeValue;
			this.btnValue.enabled = this.enabled();
			this.updateValueButtonText();
		}

		@Override
		public void updateValueButtonText()
		{
			this.btnValue.displayString = I18n.format(String.valueOf(this.currentValue));
			this.btnValue.packedFGColour = this.currentValue ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
		}

		@Override
		public void valueButtonPressed(int slotIndex)
		{
			if (this.enabled()) this.currentValue = !this.currentValue;
		}

		@Override
		public boolean isDefault()
		{
			return this.currentValue == Boolean.valueOf(this.configElement.getDefault().toString());
		}

		@Override
		public void setToDefault()
		{
			if (this.enabled())
			{
				this.currentValue = Boolean.valueOf(this.configElement.getDefault().toString());
				this.updateValueButtonText();
			}
		}

		@Override
		public boolean isChanged()
		{
			return this.currentValue != this.beforeValue;
		}

		@Override
		public void undoChanges()
		{
			if (this.enabled())
			{
				this.currentValue = this.beforeValue;
				this.updateValueButtonText();
			}
		}

		@Override
		public boolean saveConfigElement()
		{
			if (this.enabled() && this.isChanged())
			{
				this.configElement.set(this.currentValue);
				return this.configElement.requiresMcRestart();
			}
			return false;
		}

		@Override
		public Boolean getCurrentValue()
		{
			return this.currentValue;
		}

		@Override
		public Boolean[] getCurrentValues()
		{
			return new Boolean[] { this.getCurrentValue() };
		}
	}


}
