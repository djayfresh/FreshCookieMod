package freshcaa.fresh.cookies;
import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.fresh.item.Cookie;
import freshcaa.fresh.load.ConfigLoader;
import freshcaa.fresh.load.ItemLoader;
 
public class Cookie_WM extends Cookie {

	public Cookie_WM(int par1) {
	super(par1, ConfigLoader.macadamiaCookieHeal, false); //Returns super constructor: par1 is ID
	setUnlocalizedName("White Macadamia Cookie");
	setCreativeTab(CookieMod.cookieTab); }//Tells the game what creative mode tab it goes in


	public void registerIcons(IconRegister reg) 
	{ // Make sure to import IconRegister!
		
		if (itemID == ItemLoader.wm_Cookie.itemID) //Cookie refers to the class we created and Amethyst is the type
		{ 
			//The pictue filename inside 1.6.4.jar/assets/minecraft/textures
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":Cookie_WM"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}