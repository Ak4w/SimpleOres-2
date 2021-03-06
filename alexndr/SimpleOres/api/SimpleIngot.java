package alexndr.SimpleOres.api;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import alexndr.SimpleOres.core.SimpleOres;
import alexndr.SimpleOres.core.conf.Settings;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class SimpleIngot extends Item
{
	private String modName = "simpleores";
	private CreativeTabs tab = SimpleOres.tabSimpleMaterials;
	
	public SimpleIngot(int id) 
	{
		super(id);
		
		if(Settings.enableSeparateTabs == true)
		{
			this.setCreativeTab(tab);
		}
		else this.setCreativeTab(SimpleOres.tabSimpleBlocks);
	}
	
	/**
	 * The modID of the mod adding the item. Used to find textures. Defaults to "simpleores".
	 */
	public SimpleIngot modId(String modId)
	{
		this.modName = modId;
		
		return this;
	}
	
	/**
	 * Sets the creative tab for the item to appear in. Defaults to SimpleOres.tabSimpleMaterials.
	 */
	public SimpleIngot setTab(CreativeTabs creativetab)
	{
		tab = creativetab;
		return this;
	}
	
	/**
	 * Registers the item in the GameRegistry, with the name given, and sends the name through to setUnlocalizedName in the super class.
	 */
	public SimpleIngot setUnlocalizedName(String unlocalizedName)
	{
		super.setUnlocalizedName(unlocalizedName);
		GameRegistry.registerItem(this, unlocalizedName);
		return this;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(modName + ":" + (this.getUnlocalizedName().substring(5)));
	}
}
