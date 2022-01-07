package io.github.alexeymartynov.police;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import io.github.alexeymartynov.police.main.PoliceCommand;
import io.github.alexeymartynov.police.main.PolicePlugin;
import io.github.alexeymartynov.util.Util;
import io.github.alexeymartynov.util.UtilItem;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class Rank {

	private static List<Rank> ranks = new ArrayList<>();
	
	private String index, name;
	private Set set;
	private int places, needExp, needArrests, moneyForArrest, expForArrest;
	private List<PoliceCommand> permissions = new ArrayList<>();
	private FileConfiguration config = Config.RANKS.get();
	
	public Rank(String index) 
	{
		this.index = index;
		try 
		{
			name = config.getString("ranks." + index + ".name");
			places = config.getInt("ranks." + index + ".places");
			needExp = config.getInt("ranks." + getIndex() + ".need_exp");
			needArrests = config.getInt("ranks." + getIndex() + ".need_arrests");
			moneyForArrest = config.getInt("ranks." + getIndex() + ".money_for_arrest");
			expForArrest = config.getInt("ranks." + getIndex() + ".exp_for_arrest");
			List<ItemStack> items = new ArrayList<>();
			ConfigurationSection section = config.getConfigurationSection("ranks." + getIndex() + ".set");
			if(section == null)
				return;
			
			for(String key : section.getKeys(false)) 
			{
				List<String> lore = new ArrayList<>();
				for(String line : section.getStringList(key + ".lore"))
					lore.add(Util.formatString(line));

				items.add(UtilItem.create(UtilItem.create(CraftItemStack.asNewCraftStack(Item.REGISTRY.get(new MinecraftKey(section.getString(key + ".id")))).getType(), 
						Util.formatString(section.getString(key + ".name"))), lore));
			}
			
			set = new Set(items);
			for(String permission : config.getStringList("ranks." + getIndex() + ".permissions"))
				permissions.add(PoliceCommand.valueOf(permission));
			
			ranks.add(this);
		}
		catch(Exception exception) 
		{ 
			exception.printStackTrace();
			Bukkit.getLogger().severe("PolicePlugin was disabled because ranks.yml has errors in setup"); 
			Bukkit.getPluginManager().disablePlugin(PolicePlugin.getInstance());
		}		
	}
	
	public static List<Rank> getRanks() { return ranks; }
	
	public Rank getNextRank() 
	{
		int index = getRanks().indexOf(this);
		if(index < getRanks().size() - 1)
			return getRanks().get(index + 1);
		
		return null;
	}
	
	public int getPlaces() { return places; }
	
	public int getNeedExp() { return needExp; }
	
	public int getNeedArrests() { return needArrests; }
	
	public int getMoneyForArrest() { return moneyForArrest; }
	
	public int getExpForArrest() { return expForArrest; }
	
	public boolean hasPermission(PoliceCommand permission) { return permissions.contains(permission); }
	
	public Set getSet() { return set; }

	public String getName() { return new String(name); }
	
	public String getIndex() { return new String(index); }
	
	public static Rank getRank(String index) 
	{
		ConfigurationSection section = Config.RANKS.get().getConfigurationSection("ranks." + index);
		if(section == null)
			return null;
		
		for(Rank rank : getRanks())
			if(rank.getIndex().equals(index))
				return rank;
		
		return null;
	}
	
	public static Rank getLowestRank() { return getRanks().get(getRanks().size() - 1); }
	
	public static Rank getHighestRank() { return getRanks().get(0); }
	
}
