package io.github.alexeymartynov.police;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Set {

	private List<ItemStack> items;
	
	public Set(List<ItemStack> items) { this.items = items; }
	
	public void giveSet(Player player) 
	{
		for(ItemStack item : items)
			player.getInventory().addItem(item);
	}
	
	public void removeSet(Player player) 
	{
		for(ItemStack item : items)
			player.getInventory().removeItem(item);
	}
}
