package net.crazysnailboy.mods.villagerinventory.common.network.message;


import io.netty.buffer.ByteBuf;
import net.crazysnailboy.mods.villagerinventory.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class EntityFlagMessage implements IMessage
{

	private int entityId;
	private int flag;
	private boolean value;


	public EntityFlagMessage()
	{
	}


	public EntityFlagMessage(Entity entity, int flag, boolean value)
	{
		this.entityId = entity.getEntityId();
		this.flag = flag;
		this.value = value;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = ByteBufUtils.readVarInt(buf, 4);
		this.flag = ByteBufUtils.readVarInt(buf, 4);
		this.value = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, this.entityId, 4);
		ByteBufUtils.writeVarInt(buf, this.flag, 4);
		buf.writeBoolean(this.value);
	}


	public static final class MessageHandler implements IMessageHandler<EntityFlagMessage, IMessage>
	{

		@Override
		public IMessage onMessage(final EntityFlagMessage message, MessageContext context)
		{
			final WorldServer world = (WorldServer)context.getServerHandler().player.world;
			world.addScheduledTask(new Runnable()
			{

				@Override
				public void run()
				{
					EntityUtils.setFlag(world.getEntityByID(message.entityId), message.flag, message.value);
				}
			});
			return null;
		}
	}

}