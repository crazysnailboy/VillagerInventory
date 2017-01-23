package net.crazysnailboy.mods.villagerinventory.common.network;

import io.netty.buffer.ByteBuf;
import net.crazysnailboy.mods.villagerinventory.client.gui.inventory.GuiVillagerInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class VillagerCareerMessage implements IMessage 
{
	
	private int entityId;
	private int careerId;


	
	public VillagerCareerMessage()
	{
	}
	
	public VillagerCareerMessage(int entityId, int careerId)
	{
		this.entityId = entityId;
		this.careerId = careerId;
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.entityId = ByteBufUtils.readVarShort(buf);
		this.careerId = ByteBufUtils.readVarShort(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeVarShort(buf, this.entityId);
		ByteBufUtils.writeVarShort(buf, this.careerId);
	}
	
	
	public static final class MessageHandler implements IMessageHandler<VillagerCareerMessage, IMessage>
	{
		@Override
		public IMessage onMessage(final VillagerCareerMessage message, MessageContext ctx) 
		{
			Minecraft minecraft = Minecraft.getMinecraft();
			final WorldClient world = minecraft.theWorld;
			
			minecraft.addScheduledTask(new Runnable()
			{
				public void run() 
				{
					// get the entity referenced by the specified id 
					Entity entity = world.getEntityByID(message.entityId);
					
					// ensure that the entity is a villager
					if (entity instanceof EntityVillager)
					{
						// cast the entity to type EntityVillager
						EntityVillager villager = (EntityVillager)entity;
						
						// read the villager's nbt data into a tag compound
						NBTTagCompound compound = new NBTTagCompound();
						villager.writeEntityToNBT(compound);
						// set the career property of the tag compound and write it back to the villager 
						compound.setInteger("Career", message.careerId);
						villager.readEntityFromNBT(compound);
						
						// set the careerId field as well, since this is what drives the 						
//						ObfuscationReflectionHelper.setPrivateValue(EntityVillager.class, villager, message.careerId, "careerId", "field_175563_bv");

						
						
						GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen; 
//						System.out.println("currentScreen: " + (currentScreen == null ? "null" : currentScreen.getClass().getCanonicalName()));
						
						if (currentScreen != null && currentScreen instanceof GuiVillagerInventory)
						{
							((GuiVillagerInventory)currentScreen).readVillagerFromNBT(compound);
						}
						
//						int careerId = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(EntityVillager.class, villager, "careerId", "field_175563_bv"))).intValue(); // TODO so could this..
						
					}
					
					
					
				}
			});
			
			return null;
		}
		
//		void processMessage(WorldClient worldClient, VillagerCareerMessage message)
//		{
//			GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen; 
//			
//			System.out.println("currentScreen: " + (currentScreen == null ? "null" : currentScreen.getClass().getCanonicalName()));
//			
//			
//			
//		}
		
	}

}
