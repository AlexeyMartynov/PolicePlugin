package io.github.alexeymartynov.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UtilItem {

    public static ItemStack create(Material material, int amount, byte data, String displayName, String... loreLines)
    {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        if(displayName != null)
            meta.setDisplayName(displayName);

        ArrayList<String> lore = new ArrayList<String>();
        if(loreLines.length > 0)
        {
            for(String loreLine : loreLines)
                lore.add(loreLine);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(ItemStack item, String... loreLines)
    {
        ItemStack stack = item;
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
        for(String loreLine : loreLines)
            lore.add(loreLine);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack create(ItemStack item, List<String> lore)
    {
        ItemStack stack = item;
        ItemMeta meta = stack.getItemMeta();
        if(lore != null && !lore.isEmpty())
        	meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(Material material, String displayName, String... loreLines)
    {
        return create(material, 1, (byte)0, displayName, loreLines);
    }

    public static ItemStack create(Material material, String displayName)
    {
        return create(material, 1, (byte)0, displayName);
    }

    public static String getItemName(ItemStack item)
    {
        String itemName = "";
        try { itemName = item.getItemMeta().getDisplayName(); }
        catch(NullPointerException exception) { itemName = "none"; }

        return itemName;
    }

    public static boolean areTheSameItems(ItemStack firstItem, ItemStack secondItem)
    { 
    	firstItem = new ItemStack(firstItem);
    	firstItem.setAmount(1);
    	secondItem = new ItemStack(secondItem);
    	secondItem.setAmount(1);
        if(firstItem != null && secondItem != null)
        {
            if(firstItem.getType() == secondItem.getType() && firstItem.getItemMeta().toString().equals(secondItem.getItemMeta().toString()))
                return true;
        }

        return false;
    }
}
