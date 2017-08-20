package net.crazysnailboy.mods.villagerinventory.common.network.message;

import io.netty.buffer.ByteBuf;
import net.crazysnailboy.mods.villagerinventory.common.config.ModConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


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
		enableInventoryGui = (ByteBufUtils.readVarShort(buf) == 1);
		enableDeathDrops = (ByteBufUtils.readVarShort(buf) == 1);
		requireEmptyHand = (ByteBufUtils.readVarShort(buf) == 1);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarShort(buf, (enableInventoryGui ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (enableDeathDrops ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (requireEmptyHand ? 1 : 0));
	}


	public static final class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage>
	{

		private IThreadListener getThreadListener(MessageContext ctx)
		{
			switch (ctx.side)
			{
				case SERVER: return (WorldServer)ctx.getServerHandler().playerEntity.world;
				case CLIENT: return Minecraft.getMinecraft();
				default: return null;
			}
		}

		@Override
		public IMessage onMessage(final ConfigSyncMessage message, MessageContext ctx)
		{
			IThreadListener threadListener = getThreadListener(ctx);
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