package net.crazysnailboy.mods.villagerinventory.client.gui.inventory;

import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.inventory.ContainerVillagerInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVillagerInventory extends GuiContainer 
{

	private static final ResourceLocation VILLAGER_GUI_TEXTURES = new ResourceLocation(VillagerInventoryMod.MODID, "textures/gui/container/villager_inventory.png");

	private final IInventory playerInventory;
	private final IInventory villagerInventory;
	private final EntityVillager villagerEntity;
	

	public GuiVillagerInventory(InventoryPlayer playerInv, InventoryBasic villagerInv, EntityVillager villager) 
	{
		super(new ContainerVillagerInventory(playerInv, villagerInv, villager, playerInv.player));
		this.playerInventory = playerInv;
		this.villagerInventory = villagerInv;
		this.villagerEntity = villager;
		this.allowUserInput = false;
		this.ySize = 133;
	}
	
	public void readVillagerFromNBT(NBTTagCompound compound)
	{
		System.out.println("readVillagerFromNBT");
		
		this.villagerEntity.readEntityFromNBT(compound);
	}
	
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) 
	{
		// write the villager career at the top of the gui
		this.fontRenderer.drawString(this.villagerInventory.getName(), 8, 6, 4210752);
		
		// write "inventory" above the player inventory
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}
	
	
	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) 
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		// bind the background gui texture
		this.mc.getTextureManager().bindTexture(VILLAGER_GUI_TEXTURES);
		
		// render the gui background
		int guiX = (this.width - this.xSize) / 2;
		int guiY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(guiX, guiY, 0, 0, this.xSize, this.ySize);
	}

}
