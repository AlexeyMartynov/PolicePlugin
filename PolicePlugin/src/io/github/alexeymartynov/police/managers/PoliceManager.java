package io.github.alexeymartynov.police.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import io.github.alexeymartynov.police.Config;
import io.github.alexeymartynov.police.Message;
import io.github.alexeymartynov.police.Policeman;
import io.github.alexeymartynov.police.Rank;
import io.github.alexeymartynov.police.Wanted;
import io.github.alexeymartynov.util.Util;

public class PoliceManager implements IPolice, IManager {
	
	private Config config = Config.WANTED;
	private List<Wanted> wanted = new ArrayList<>();
	private List<Policeman> policemen = new ArrayList<>();
	
	public PoliceManager()
	{
		ConfigurationSection section = Config.RANKS.get().getConfigurationSection("ranks");
		if(section != null)
			for(String index : section.getKeys(false))
				new Rank(index);
		
		section = Config.POLICE.get().getConfigurationSection("police.members"); 
		if(section != null) 
		{ 
			for(String nick : section.getKeys(false)) 
				policemen.add(new Policeman(nick, Rank.getRank(section.getString(new String(nick) + ".rank")), 
						section.getInt(new String(nick) + ".arrest_amount"), 
						section.getInt(new String(nick) + ".rank_exp"),
						section.getBoolean(new String(nick) + ".warned"))); 
		}
		
		section = config.get().getConfigurationSection("wanted");
		if(section != null)
			for(String nick : section.getKeys(false))
				wanted.add(new Wanted(nick, config.get().getInt("wanted." + nick + ".wanted_level")));
	}
	
	public List<Policeman> getPolicemen() { return policemen; }
	
	public Policeman getPoliceman(String nick) 
	{
		for(Policeman value : getPolicemen())
			if(value.getNick().equals(nick))
				return value;
		
		return null;
	}
	
	public int getPolicemanAmountWithRank(Rank rank) 
	{
		int amount = 0;
		for(Policeman policeman : getPolicemen())
			if(policeman.getRank() == rank)
				amount++;
			
		return amount;
	}
	
	public Wanted getWanted(String nick) 
	{
		for(Wanted value : this.wanted) 
			if(value.getNick().equals(nick))
				return value;
		
		return null;
	}
	
	public void setWanted(String nick, int wantedLevel) 
	{
		if(getWanted(nick) == null) 
			wanted.add(new Wanted(nick, wantedLevel));
		else getWanted(nick).setWantedLevel(wantedLevel);
		
		sync();
	}
	
	public void sendMessage(String message) 
	{
		for(Policeman policeman : getPolicemen()) 
		{
			Player player = Bukkit.getPlayer(policeman.getNick());
			if(player != null)
				player.sendMessage(message);
		}
	}

	@Override
	public void payDay() 
	{
		for(Policeman policeman : getPolicemen()) 
		{
			Rank rank = policeman.getRank();
			int arrestAmount = policeman.getArrestAmount();
			Util.addMoney(policeman.getNick(), rank.getMoneyForArrest() * arrestAmount);
			policeman.addRankExp(rank.getExpForArrest() * arrestAmount);
			policeman.setArrestAmount(0);
			Player player = Bukkit.getPlayer(policeman.getNick());
			if(player == null) continue;
			
			player.sendMessage(Message.YOUR_SALARY.get());
			player.sendMessage(ChatColor.GOLD + "" + rank.getExpForArrest() + ChatColor.AQUA + " очков опыта");
			player.sendMessage(ChatColor.GOLD + "" + rank.getMoneyForArrest() + ChatColor.AQUA + " единиц денег");
		}
	}

	@Override
	public void sync() 
	{
		for(Policeman policeman : getPolicemen())
			policeman.sync();
		
		for(Wanted value : wanted)
			config.get().set("wanted." + value.getNick(), value.getWantedLevel());
		
		config.save();
	}
}
