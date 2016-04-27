package freshcaa.fresh.cookies;
import net.minecraft.client.renderer.texture.IconRegister;
import freshcaa.minecraft.item.Cookie;
 
public class Cookie_WN extends Cookie {

	public Cookie_WN(int par1) {
	super(par1, 5, true); //Returns super constructor: par1 is ID
	setUnlocalizedName("Pecan Cookie");
	setCreativeTab(CookieMod.cookieTab); }//Tells the game what creative mode tab it goes in


	public void registerIcons(IconRegister reg) 
	{ // Make sure to import IconRegister!
		
		if (itemID == CookieMod.wn_Cookie.itemID) //Cookie refers to the class we created and Amethyst is the type
		{ 
			//The pictue filename inside 1.6.4.jar/minecraft/textures
			this.itemIcon = reg.registerIcon(CookieMod.modid + ":Cookie_WN"); // You can also replace blockID and blockIcon with itemID and itemIcon
		}
	}
}