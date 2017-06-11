package net.crazysnailboy.mods.villagerinventory.common.network;

import io.netty.buffer.ByteBuf;
import net.crazysnailboy.mods.villagerinventory.VillagerInventoryMod;
import net.crazysnailboy.mods.villagerinventory.common.config.ModConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class ConfigSyncMessage implements IMessage
{

	private boolean enableInventoryGui = ModConfiguration.enableInventoryGui;
	private boolean enableDeathDrops = ModConfiguration.enableDeathDrops;
	private boolean requireEmptyHand = ModConfiguration.requireEmptyHand;


	public ConfigSyncMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.enableInventoryGui = (ByteBufUtils.readVarShort(buf) == 1);
		this.enableDeathDrops = (ByteBufUtils.readVarShort(buf) == 1);
		this.requireEmptyHand = (ByteBufUtils.readVarShort(buf) == 1);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarShort(buf, (this.enableInventoryGui ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (this.enableDeathDrops ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (this.requireEmptyHand ? 1 : 0));
	}


	public static final class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage>
	{

		private IThreadListener getThreadListener(MessageContext ctx)
		{
			try
			{
				if (ctx.side == Side.SERVER) return (WorldServer)ctx.getServerHandler().player.world;
				else if (ctx.side == Side.CLIENT) return Minecraft.getMinecraft();
				else return null;
			}
			catch (Exception ex)
			{
				VillagerInventoryMod.LOGGER.catching(ex);
				return null;
			}
		}

		@Override
		public IMessage onMessage(final ConfigSyncMessage message, MessageContext ctx)
		{
			IThreadListener threadListener = this.getThreadListener(ctx);
			threadListener.addScheduledTask(new Runnable()
			{

				@Override
				public void run()
				{
					ModConfiguration.enableInventoryGui = message.enableInventoryGui;
					ModConfiguration.enableDeathDrops = message.enableDeathDrops;
					ModConfiguration.requireEmptyHand = message.requireEmptyHand;
				}
			});

			return null;
		}
	}

}
