package alexndr.SimpleOres.core;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alexndr.SimpleOres.api.HandlerLogger;
import alexndr.SimpleOres.api.HandlerLoot;
import alexndr.SimpleOres.api.HandlerUpdateChecker;
import alexndr.SimpleOres.api.SimpleIngot;
import alexndr.SimpleOres.api.SimpleTab;
import alexndr.SimpleOres.api.StatTriggers;
import alexndr.SimpleOres.core.conf.Config;
import alexndr.SimpleOres.core.conf.Settings;
import alexndr.SimpleOres.core.content.MythrilFurnaceTileEntity;
import alexndr.SimpleOres.core.content.OnyxFurnaceTileEntity;
import alexndr.SimpleOres.core.handlers.Generator;
import alexndr.SimpleOres.core.handlers.HandlerJoinWorld;
import alexndr.SimpleOres.core.handlers.ProxyCommon;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@NetworkMod(clientSideRequired = true, serverSideRequired = false)
@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)

public class SimpleOres 
{
	@SidedProxy(clientSide = "alexndr.SimpleOres.core.handlers.ProxyClient", serverSide = "alexndr.SimpleOres.core.handlers.ProxyCommon")	
	public static ProxyCommon proxy;
	
	/**
	 * Linking to the classes for easier reference.
	 */
	public static SimpleOres INSTANCE = new SimpleOres();
	
	/**
	 * EnumToolMaterial. In form ("NAME", mining level, max uses, speed, damage to entity, enchantability)
	 */
  public static EnumToolMaterial toolCopper;
  public static EnumToolMaterial toolTin;
  public static EnumToolMaterial toolMythril;
  public static EnumToolMaterial toolAdamantium;
  public static EnumToolMaterial toolOnyx;
     
  /**
   * EnumArmorMaterial. In form ("NAME", max damage (like uses, multiply by pieces for their max damage), new int[] {helmet defense, chestplate defense, leggings defense, boots defense}, enchantability)
   */
  public static EnumArmorMaterial armorCopper;
  public static EnumArmorMaterial armorTin;
  public static EnumArmorMaterial armorMythril;
  public static EnumArmorMaterial armorAdamantium;
  public static EnumArmorMaterial armorOnyx;
  
  /**
   * Creating the tabs for Creative Inventory.
   */
  public static SimpleTab tabSimpleBlocks;
  public static SimpleTab tabSimpleDecorations;
  public static SimpleTab tabSimpleTools;
  public static SimpleTab tabSimpleCombat;
  public static SimpleTab tabSimpleMaterials;

  /**
   * Creating the Armor Renderers. This is simply so you can see the armor texture when you wear it.
   */
	public static int rendererCopper;
	public static int rendererTin;
	public static int rendererMythril;
	public static int rendererAdamantium;
	public static int rendererOnyx;
	
	/**
	 * Called before the @Init function. I had to put most things in here for achievements to work. Probably a better way to do it.
	 */
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) 
  {
  	proxy.registerClientTickHandler();
  	HandlerLogger.init();
  	/**
  	 * Calling the various parts of the mod. Moved to different files for neatness. Pretty self explanatory what they all are :P
  	 */
  	//Configuration
  	Config.doConfig(event);
  	Settings.doSettings(event);
  	doTabs();
  	
  	//Content
  	setToolAndArmorStats();
  	Items.doItems();
  	Blocks.doBlocks();
  	Tools.doTools();
  	Armor.doArmor();
  	Recipes.doRecipes();	
  	Achievements.doAchievements();
  	setTabIcons();
  }
  
  public void doTabs()
  {
  	/**
  	 * Creating the custom tabs using the API class "SimpleTab".
  	 */  	
  	tabSimpleBlocks = new SimpleTab("tabSimpleBlocks");   	
  	 
  	if(Settings.enableSeparateTabs)
  	{
      	tabSimpleDecorations = new SimpleTab("tabSimpleDecorations");
      	tabSimpleTools = new SimpleTab("tabSimpleTools");
      	tabSimpleCombat = new SimpleTab("tabSimpleCombat");
      	tabSimpleMaterials = new SimpleTab("tabSimpleMaterials");
  	}
  }
  
  public void setTabIcons()
  {
  	/**
  	 * Setting the Creative Tab icons.
  	 */  	
		tabSimpleBlocks.setIcon(new ItemStack(Blocks.copperOre));
		
  	if(Settings.enableSeparateTabs)
  	{
  	    tabSimpleDecorations.setIcon(new ItemStack(Blocks.mythrilFurnaceOn));
  	    tabSimpleTools.setIcon(new ItemStack(Tools.onyxPick));
  	    tabSimpleCombat.setIcon(new ItemStack(Armor.adamantiumHelm));
  	    tabSimpleMaterials.setIcon(new ItemStack(Items.tinIngot));
  	}
  }
  
  public void setToolAndArmorStats()
  {
  	toolCopper = EnumHelper.addToolMaterial("COPPER", Settings.copperMiningLevel, Settings.copperUsesNum, Settings.copperMiningSpeed, Settings.copperDamageVsEntity, Settings.copperEnchantability);
  	toolTin = EnumHelper.addToolMaterial("TIN", Settings.tinMiningLevel, Settings.tinUsesNum, Settings.tinMiningSpeed, Settings.tinDamageVsEntity, Settings.tinEnchantability);
  	toolMythril = EnumHelper.addToolMaterial("MYTHRIL", Settings.mythrilMiningLevel, Settings.mythrilUsesNum, Settings.mythrilMiningSpeed, Settings.mythrilDamageVsEntity, Settings.mythrilEnchantability);
  	toolAdamantium = EnumHelper.addToolMaterial("ADAMANTIUM", Settings.adamantiumMiningLevel, Settings.adamantiumUsesNum, Settings.adamantiumMiningSpeed, Settings.adamantiumDamageVsEntity, Settings.adamantiumEnchantability);
  	toolOnyx = EnumHelper.addToolMaterial("ONYX", Settings.onyxMiningLevel, Settings.onyxUsesNum, Settings.onyxMiningSpeed, Settings.onyxDamageVsEntity, Settings.onyxEnchantability);
  	
  	armorCopper = EnumHelper.addArmorMaterial("COPPER", Settings.copperArmorDurability, Settings.copperArmorDamageReduction, Settings.copperArmorEnchantability);
  	armorTin = EnumHelper.addArmorMaterial("TIN", Settings.tinArmorDurability, Settings.tinArmorDamageReduction, Settings.tinArmorEnchantability);
  	armorMythril = EnumHelper.addArmorMaterial("MYTHRIL", Settings.mythrilArmorDurability, Settings.mythrilArmorDamageReduction, Settings.mythrilArmorEnchantability);
  	armorAdamantium = EnumHelper.addArmorMaterial("ADAMANTIUM", Settings.adamantiumArmorDurability, Settings.adamantiumArmorDamageReduction, Settings.adamantiumArmorEnchantability);
  	armorOnyx = EnumHelper.addArmorMaterial("ONYX", Settings.onyxArmorDurability, Settings.onyxArmorDamageReduction, Settings.onyxArmorEnchantability);
  }
  
  @EventHandler
  public void Init(FMLInitializationEvent event)
  {	    	
  	NetworkRegistry.instance().registerConnectionHandler(new HandlerUpdateChecker());
  	
  	if(Settings.enableUpdateChecker){HandlerUpdateChecker.checkUpdates(ModInfo.VERSIONURL, ModInfo.ID, ModInfo.VERSION);}
  	
		INSTANCE = this;
			
		/**
		 * Adding localisation files.
		 * 
		 * Thanks to @zot for the code for loading localisations automatically.
		 */
		addLocalisations();
		
		/**
		 * Registering things such as the world generator, tile entities and GUI's.
		 */
		//Registering
		NetworkRegistry.instance().registerGuiHandler(INSTANCE, proxy);
		GameRegistry.registerWorldGenerator(new Generator());
		GameRegistry.registerTileEntity(MythrilFurnaceTileEntity.class, "mythrilFurnace");
		GameRegistry.registerTileEntity(OnyxFurnaceTileEntity.class, "onyxFurnace");
		GameRegistry.registerCraftingHandler(new StatTriggers());
		GameRegistry.registerPickupHandler(new StatTriggers());
		MinecraftForge.EVENT_BUS.register(new HandlerJoinWorld());
		
		/**
		 * Adds the armor textures when you wear it. Calls a method in the CommonProxy (which is overridden by ClientProxy) called addArmor, and inputs the name of the material.
       	 * This allows the game to recognise that texture files called copper_1, mythril_2, etc. are corresponding to that armor set.
       	 */
		//Armor Renderers
		rendererCopper = proxy.addArmor("copper");
		rendererMythril = proxy.addArmor("mythril");
		rendererTin = proxy.addArmor("tin");
		rendererAdamantium = proxy.addArmor("adamantium");
		rendererOnyx = proxy.addArmor("onyx");
      
		/**
		 * This sets what item can be used to repair tools/armor of that type. ie. Copper Ingot to repair copper tools and items.
		 */     
		//Repair Materials
		toolCopper.customCraftingMaterial = Items.copperIngot;
		toolTin.customCraftingMaterial = Items.tinIngot;
		toolMythril.customCraftingMaterial = Items.mythrilIngot;
		toolAdamantium.customCraftingMaterial = Items.adamantiumIngot;
		toolOnyx.customCraftingMaterial = Items.onyxGem;

		armorCopper.customCraftingMaterial = Items.copperIngot;
		armorTin.customCraftingMaterial = Items.tinIngot;
      	armorMythril.customCraftingMaterial = Items.mythrilIngot;
      	armorAdamantium.customCraftingMaterial = Items.adamantiumIngot;
      	armorOnyx.customCraftingMaterial = Items.onyxGem;
     
      	/**
      	 * Adds SimpleOres items to the various dungeon chests.
      	 */
      	HandlerLoot.addLoot();
      
      	//Stat Triggers
      	Achievements.setTriggers();
  }
  
  public void addLocalisations()
  {
		try 
		{
			Pattern p = Pattern.compile("assets/simpleores/langs/(.*)\\.xml");
			for (ResourceInfo i : ClassPath.from(getClass().getClassLoader()).getResources()) 
			{
				Matcher m = p.matcher(i.getResourceName());
				if (m.matches())
				{
					LanguageRegistry.instance().loadLocalization(i.url(), m.group(1), true);
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			HandlerLogger.log("Localisations loaded successfully.");
		}
  }
}
