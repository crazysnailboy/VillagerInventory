package net.crazysnailboy.mods.villagerinventory;

import net.crazysnailboy.mods.villagerinventory.common.config.ModConfiguration;
import net.crazysnailboy.mods.villagerinventory.common.network.ModGuiHandler;
import net.crazysnailboy.mods.villagerinventory.common.network.VillagerCareerMessage;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
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


@Mod(modid = VillagerInventoryMod.MODID, name = VillagerInventoryMod.MODNAME, version = VillagerInventoryMod.VERSION, updateJSON = VillagerInventoryMod.UPDATEJSON, guiFactory = VillagerInventoryMod.GUIFACTORY)
public class VillagerInventoryMod
{

	public static final String MODID = "villagerinventory";
	public static final String MODNAME = "Villager Inventory Viewer";
	public static final String VERSION = "1.1";
	public static final String GUIFACTORY = "net.crazysnailboy.mods.villagerinventory.client.config.ModGuiFactory";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/VillagerInventory/master/update.json";


	// mod instance
	@Instance(VillagerInventoryMod.MODID)
	public static VillagerInventoryMod instance;

	// gui handler
	private static ModGuiHandler guiHandler = new ModGuiHandler();

	// network
	private static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(VillagerInventoryMod.MODID);



	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// initialize the configuration
		ModConfiguration.preInit();
		if (event.getSide() == Side.CLIENT) ModConfiguration.clientPreInit();

		// register the network messages
		network.registerMessage(VillagerCareerMessage.MessageHandler.class, VillagerCareerMessage.class, 0, Side.CLIENT);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// register with the event bus
		MinecraftForge.EVENT_BUS.register(this);

		// register the gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(VillagerInventoryMod.instance, guiHandler);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}


	@SubscribeEvent
	public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		// if the villager inventory gui is enabled...
		if (ModConfiguration.enableInventoryGui)
		{
			// if the event is running on the server, if the entity being interacted with is a villager, and if the player is sneaking...
			if ((!event.getWorld().isRemote) && (event.getTarget() instanceof EntityVillager) && (event.getEntityPlayer().isSneaking()))
			{
				// if opening the gui requires the player to not be holding anything, check to ensure that the player isn't holding anything
				if (ModConfiguration.requireEmptyHand && !event.getEntityPlayer().inventory.getCurrentItem().isEmpty()) return;

				// cast the entity to type EntityVillager
				EntityVillager villager = (EntityVillager)event.getTarget();

				// get the entity id of the villager
				int entityId = villager.getEntityId();

				// and read it's profession and career from it's nbt data
				NBTTagCompound compound = new NBTTagCompound();
				villager.writeEntityToNBT(compound);
				int professionId = compound.getInteger("Profession"); // int professionId = villager.getProfession();
				int careerId = compound.getInteger("Career"); // int careerId = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(EntityVillager.class, villager, "careerId", "field_175563_bv"))).intValue();

				// send a message to the client to tell it what the villager's career is
				network.sendTo(new VillagerCareerMessage(entityId, careerId), (EntityPlayerMP)event.getEntityPlayer());

				// open the inventory gui
				FMLNetworkHandler.openGui(event.getEntityPlayer(), VillagerInventoryMod.instance, ModGuiHandler.GUI_VILLAGER_INVENTORY, event.getWorld(), entityId, professionId, careerId);

				// cancel the event which would show the trading gui
				event.setCanceled(true);
			}
		}
	}


	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		// if villager death drops are enabled...
		if (ModConfiguration.enableDeathDrops)
		{
			// if the newly dead entity is a villager, and that villager was killed by a player...
			if ((event.getEntityLiving() instanceof EntityVillager) && (event.getSource().getSourceOfDamage() instanceof EntityPlayerMP))
			{
				// iterate through the itemstacks in the villager's inventory
				InventoryBasic inventory = ((EntityVillager)event.getEntityLiving()).getVillagerInventory();
				for ( int i = 0 ; i < inventory.getSizeInventory() ; i++ )
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
