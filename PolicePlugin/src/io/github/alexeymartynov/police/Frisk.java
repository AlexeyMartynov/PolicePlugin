package io.github.alexeymartynov.police;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.alexeymartynov.util.UtilItem;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class Frisk {
	
	private static List<Prohibited> prohibited = new ArrayList<>();
	
	private Player player, policeman;
	
	public static void setupProhibited() 
	{
		ConfigurationSection section = Config.PROHIBITED.get().getConfigurationSection("prohibited");
		if(section == null)
			return;
		
		for(String index : section.getKeys(false)) 
		{
			boolean checkLore = section.getBoolean(index + ".check_lore");
			ItemStack item = new ItemStack(CraftItemStack.asNewCraftStack(Item.REGISTRY.get(new MinecraftKey(section.getString(index + ".id")))).getType());
			String name = section.getString(index + ".name");
			ItemMeta meta = item.getItemMeta();		
			if(checkLore)
				meta.setLore(section.getStringList(index + ".lore"));
			
			if(!name.equals(null))
				meta.setDisplayName(name);
			
			item.setItemMeta(meta);
				
			prohibited.add(new Prohibited(item, checkLore));
		}
	}
	
	public Frisk(Player player, Player policeman) 
	{
		this.player = player;
		this.policeman = policeman;
	}
	
	public boolean doIt() 
	{
		if(player == null || policeman == null)
			return false;
		
		List<Integer> remove = new ArrayList<>();
		ItemStack[] contents = player.getInventory().getContents();
		for(int index = 0; index < contents.length; index++) 
		{
			ItemStack item = contents[index];
			if(item == null) continue;
			
			for(Prohibited prohibited : prohibited) 
			{
				ItemStack prohibitedItem = prohibited.getItem();
				if(!prohibited.checkLore())
				{
					item = new ItemStack(item);
					ItemMeta meta = item.getItemMeta();
					meta.setLore(prohibitedItem.getItemMeta().getLore());
					item.setItemMeta(meta);
				}
				
				if(!UtilItem.areTheSameItems(item, prohibitedItem)) continue;
				
				remove.add(index);
				break;
			}
		}
		
		if(!remove.isEmpty())
		{
			policeman.sendMessage(Message.FOUND_ON_FRISK.get());
			for(int index : remove) 
			{
				ItemStack item = contents[index];
				policeman.sendMessage("- " + (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString()) + " " + item.getAmount());
				policeman.getInventory().addItem(item);
				contents[index] = null;
			}
			
			player.getInventory().setContents(contents);
			return true;
		}
		
		return false;
	}
}
