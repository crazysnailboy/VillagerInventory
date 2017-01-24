package net.crazysnailboy.mods.villagerinventory.inventory;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class ContainerVillagerInventory extends Container 
{

	private InventoryBasic villagerInventory;
	private EntityVillager villager;
	
	
	public ContainerVillagerInventory(IInventory playerInventory, final InventoryBasic villagerInventoryIn, final EntityVillager villager, EntityPlayer player)
	{
		this.villagerInventory = villagerInventoryIn;
		this.villager = villager;
	
		villagerInventory.setCustomName(this.villager.getDisplayName().getUnformattedText());
		
		
		villagerInventoryIn.openInventory(player);

		// villager inventory slots
		int offsetX = 17; int offsetY = 20;
		for (int col = 0; col < 8; col++) 
		{
			this.addSlotToContainer(new Slot(villagerInventoryIn, col, offsetX + col * 18, offsetY));
		}

		// player inventory
		offsetX = 8; offsetY = 51;
		for (int row = 0; row < 3; row++) 
		{
			for (int col = 0; col < 9; col++) 
			{
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, offsetX + col * 18, offsetY + row * 18));
			}
		}
		
		// player hotbar inventory
		offsetY = 109;
		for (int col = 0; col < 9; col++) 
		{
			this.addSlotToContainer(new Slot(playerInventory, col, offsetX + col * 18, offsetY));
		}
		
	}
	
	
	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) 
	{
		return true;
	}
	
	
	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.villagerInventory.getSizeInventory())
			{
				if (!this.mergeItemStack(itemstack1, this.villagerInventory.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, this.villagerInventory.getSizeInventory(), false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}


	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		this.villagerInventory.closeInventory(playerIn);
	}

}
