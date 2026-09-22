package com.fresh.pirates;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.fresh.pirates.blocks.TestingBlock;
import com.fresh.pirates.items.TestingItem;

public class PirateWorldMod
{
    //Blocks
    public static Block testingBlock;
    
    
    //Items
    public static Item testingItem;
    
    
	PirateEventHandler handler = new PirateEventHandler();
    
    @EventHandler
    public void preinit(FMLInitializationEvent e)
    {
    	FMLCommonHandler.instance().bus().register(handler);
    	MinecraftForge.EVENT_BUS.register(handler);
    	
    	testingBlock = new TestingBlock(null);
    	
    	GameRegistry.registerBlock(testingBlock, "movingBlock");
    	
    	testingItem = new TestingItem();
    	
    	GameRegistry.registerItem(testingItem, "steel_sword");
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	//recipes
    	
    }
}
