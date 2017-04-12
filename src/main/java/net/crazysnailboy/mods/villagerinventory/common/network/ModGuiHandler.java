package net.crazysnailboy.mods.villagerinventory.common.network;

import net.crazysnailboy.mods.villagerinventory.client.gui.inventory.GuiVillagerInventory;
import net.crazysnailboy.mods.villagerinventory.inventory.ContainerVillagerInventory;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public class ModGuiHandler implements IGuiHandler
{

	public static final int GUI_VILLAGER_INVENTORY = 0;


	/**
	 * Returns a Server side Container to be displayed to the user.
	 */
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int entityId, int professionId, int careerId)
	{
		if (id == GUI_VILLAGER_INVENTORY)
		{
			EntityVillager villager = (EntityVillager)world.getEntityByID(entityId);
			if (villager != null)
			{
				return new ContainerVillagerInventory(player.inventory, villager.getVillagerInventory(), villager, player);
			}
		}
		return null;
	}


	/**
	 * Returns a Container to be displayed to the user. On the client side, this
	 * needs to return a instance of GuiScreen On the server side, this needs to
	 * return a instance of Container
	 */
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int entityId, int professionId, int careerId)
	{
		if (id == GUI_VILLAGER_INVENTORY)
		{
			EntityVillager villager = (EntityVillager)world.getEntityByID(entityId);
			if (villager != null)
			{
				return new GuiVillagerInventory(player.inventory, villager.getVillagerInventory(), villager);
			}
		}
		return null;
	}
}
