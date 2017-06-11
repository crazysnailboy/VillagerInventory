package net.crazysnailboy.mods.villagerinventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.crazysnailboy.mods.villagerinventory.common.config.ModConfiguration;
import net.crazysnailboy.mods.villagerinventory.common.network.ConfigSyncMessage;
import net.crazysnailboy.mods.villagerinventory.common.network.ModGuiHandler;
import net.crazysnailboy.mods.villagerinventory.common.network.VillagerCareerMessage;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;


@Mod(modid = VillagerInventoryMod.MODID, name = VillagerInventoryMod.NAME, version = VillagerInventoryMod.VERSION, updateJSON = VillagerInventoryMod.UPDATEJSON, guiFactory = VillagerInventoryMod.GUIFACTORY)
public class VillagerInventoryMod
{

	public static final String MODID = "villagerinventory";
	public static final String NAME = "Villager Inventory Viewer";
	public static final String VERSION = "${version}";
	public static final String GUIFACTORY = "net.crazysnailboy.mods.villagerinventory.client.config.ModGuiFactory";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/VillagerInventory/master/update.json";


	@Instance(MODID)
	public static VillagerInventoryMod INSTANCE;

	private static SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static final Logger LOGGER = LogManager.getLogger(MODID);


	public SimpleNetworkWrapper getNetwork()
	{
		return NETWORK;
	}


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// initialize the configuration
		ModConfiguration.initializeConfiguration();

		// register the network messages
		NETWORK.registerMessage(VillagerCareerMessage.MessageHandler.class, VillagerCareerMessage.class, 0, Side.CLIENT);
		NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(VillagerInventoryMod.INSTANCE, new ModGuiHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}


	@EventBusSubscriber
	public static class EventHandlers
	{

		@SubscribeEvent
		public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event)
		{
			// if the villager inventory gui is enabled, if the entity being interacted with is a villager, and if the player is sneaking...
			if ((ModConfiguration.enableInventoryGui) && (event.getTarget() instanceof EntityVillager) && (event.getEntityPlayer().isSneaking()))
			{
				// if opening the gui requires the player to not be holding anything, check to ensure that the player isn't holding anything
				if (!ModConfiguration.requireEmptyHand || (ModConfiguration.requireEmptyHand && event.getEntityPlayer().inventory.getCurrentItem().isEmpty()))
				{
					// if the event is running on the server
					if (!event.getWorld().isRemote)
					{
						// cast the entity to type EntityVillager
						EntityVillager villager = (EntityVillager)event.getTarget();

						// get the entity id of the villager
						int entityId = villager.getEntityId();

						// and read it's profession and career from it's nbt data
						NBTTagCompound compound = new NBTTagCompound();
						villager.writeEntityToNBT(compound);
						int professionId = compound.getInteger("Profession");
						int careerId = compound.getInteger("Career");

						// send a message to the client to tell it what the villager's career is
						NETWORK.sendTo(new VillagerCareerMessage(entityId, careerId), (EntityPlayerMP)event.getEntityPlayer());

						// open the inventory gui
						FMLNetworkHandler.openGui(event.getEntityPlayer(), VillagerInventoryMod.INSTANCE, ModGuiHandler.GUI_VILLAGER_INVENTORY, event.getWorld(), entityId, professionId, careerId);
					}

					// cancel the event which would show the trading gui
					event.setCanceled(true);
				}
			}
		}

		@SubscribeEvent
		public static void onEntityDeath(LivingDeathEvent event)
		{
			// if villager death drops are enabled, if the newly dead entity is a villager, and that villager was killed by a player...
			if ((ModConfiguration.enableDeathDrops) && (event.getEntityLiving() instanceof EntityVillager) && (event.getSource().getTrueSource() instanceof EntityPlayerMP))
			{
				// iterate through the itemstacks in the villager's inventory
				InventoryBasic inventory = ((EntityVillager)event.getEntityLiving()).getVillagerInventory();
				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					// remove the stack from the inventory and spawn it in the world
					ItemStack stack = inventory.getStackInSlot(i);
					if (stack != ItemStack.EMPTY)
					{
						event.getEntityLiving().entityDropItem(stack, 0.0F);
					}
				}
			}
		}

	}

}