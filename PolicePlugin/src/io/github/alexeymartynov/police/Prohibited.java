package io.github.alexeymartynov.police;

import org.bukkit.inventory.ItemStack;

public class Prohibited {

	private ItemStack item;
	private boolean checkLore;
	
	public Prohibited(ItemStack item, boolean checkLore) 
	{
		this.item = item;
		this.checkLore = checkLore;
	}
	
	public ItemStack getItem() { return new ItemStack(item); }
	
	public boolean checkLore() { return checkLore; }
}
