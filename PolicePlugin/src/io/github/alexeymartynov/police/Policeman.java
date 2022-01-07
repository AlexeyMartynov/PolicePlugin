package io.github.alexeymartynov.police;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.alexeymartynov.police.main.PolicePlugin;
import io.github.alexeymartynov.police.managers.IManager;

public class Policeman implements IManager {
	
	private String nick;
	private Rank rank;
	private int arrestAmount, rankExp;
	private boolean warned;
	private FileConfiguration config = Config.POLICE.get();
	
	public Policeman(String nick, Rank rank, int arrestAmount, int rankExp, boolean warned) 
	{ 
		this.nick = nick;
		this.rank = rank;
		this.arrestAmount = arrestAmount;
		this.rankExp = rankExp;
		this.warned = warned; 
		if(PolicePlugin.getInstance().getPoliceManager() != null) 
		{
			PolicePlugin.getInstance().getPoliceManager().getPolicemen().add(this);
			rank.getSet().giveSet(Bukkit.getPlayer(nick));
		}
		
		sync();
	}

	public void setArrestAmount(int count) 
	{
		arrestAmount = count;
		sync();
	}
	
	public int getArrestAmount() { return arrestAmount; }

	public void kick() 
	{
		Player player = Bukkit.getPlayer(nick);
		getRank().getSet().removeSet(player);
		PolicePlugin.getInstance().getPoliceManager().getPolicemen().remove(this);
		config.set("police.members." + getNick(), null); 
		Config.POLICE.save();
	}
	
	public int getRankExp(String nick) { return rankExp;}
	
	public void addRankExp(int exp) 
	{
		rankExp += exp;
		sync();
	}
	
	public Rank getRank() { return rank; }
	
	public void setRank(Rank rank) 
	{
		Player player = Bukkit.getPlayer(nick);
		if(player != null)
			rank.getSet().giveSet(player);
			
		this.rank = rank;
		sync();
	}
	
	public String getNick() { return new String(nick); }
	
	public boolean isWarned() { return warned; }
	
	@Override
	public void sync() 
	{
		Player player = Bukkit.getPlayer(getNick());
		Rank rank = this.rank.getNextRank(); 
		if(rank != null) 
		{
			if(rank.getNeedArrests() <= arrestAmount && rank.getNeedExp() <= rankExp) 
			{
				if(rank.getPlaces() <=  PolicePlugin.getInstance().getPoliceManager().getPolicemanAmountWithRank(rank))
				{
					if(player != null)
						player.sendMessage(Message.TOO_MUCH_POLICEMEN_WITH_THIS_RANK.get());
				}
				else 
				{
					rankExp -= rank.getNeedArrests();
					setRank(rank);
					if(player != null)
						player.sendMessage(Message.UP_RANK.get());
				}
			}
		}
		
		config.set("police.members." + getNick() + ".rank", this.rank.getIndex());
		config.set("police.members." + getNick() + ".arrest_amount", arrestAmount);
		config.set("police.members." + getNick() + ".rank_exp", rankExp);
		config.set("police.members." + getNick() + ".warned", warned);
		Config.POLICE.save();
	}
}
