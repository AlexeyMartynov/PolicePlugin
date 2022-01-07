package io.github.alexeymartynov.police.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.alexeymartynov.police.Arrested;
import io.github.alexeymartynov.police.Config;
import io.github.alexeymartynov.police.Message;
import io.github.alexeymartynov.police.Policeman;
import io.github.alexeymartynov.police.main.PolicePlugin;

public class JailManager implements IManager {
	
	private List<Arrested> arrested = new ArrayList<>();
	private Config config = Config.JAIL;
	private Location jailLocation;
	private int secondsForOneStar = config.get().getInt("arrest_seconds_for_1_star");
	
	public JailManager() 
	{	
		try 
		{
			FileConfiguration config = this.config.get();
			jailLocation = new Location(Bukkit.getWorld(config.getString("jail.world")), config.getDouble("jail.x"), config.getDouble("jail.y"), config.getDouble("jail.z"));
			ConfigurationSection section = config.getConfigurationSection("arrested");
			if(section == null)
				return;
			
			for(String nick : section.getKeys(false))
			{
				long secondsLeft = section.getLong(nick + ".seconds_left");
				Location initialLocation = new Location(Bukkit.getWorld(section.getString(nick + ".initial_location.world")), 
						section.getDouble(nick + ".initial_location.x"), section.getDouble(nick + ".initial_location.y"), section.getDouble(nick + ".initial_location.z"));
				
				arrested.add(new Arrested(nick, secondsLeft, initialLocation));
			}
		}
		catch(Exception exception) 
		{ 
			Bukkit.getLogger().severe("PolicePlugin was disabled because jail.yml has errors in setup");
			Bukkit.getPluginManager().disablePlugin(PolicePlugin.getInstance());
		}
	}
	
	public boolean isArrested(String nick) 
	{ 
		for(Arrested arrested : getArrested())
			if(arrested.getNick().equals(nick))
				return true;
		
		return false;
	}
	
	private int getArrestSeconds(int stars) { return stars * secondsForOneStar; }
	
	public boolean arrest(Player player, String policemanNick) 
	{
		String nick = player.getName();
		if(player == null || isArrested(nick))
			return false;
		
		Policeman policeman = PolicePlugin.getInstance().getPoliceManager().getPoliceman(policemanNick);
		if(policeman == null)
			return false;
		
		policeman.setArrestAmount(policeman.getArrestAmount());
		arrested.add(new Arrested(nick, getArrestSeconds(1), player.getLocation()));
		sync();
		PolicePlugin.getInstance().getPoliceManager().sendMessage(Message.ARRESTED.get().replace("#arrested_nick#", nick).replace("#policeman_nick#", policemanNick));
		player.teleport(jailLocation);
		return true;
	}

	public boolean unarrest(Player player, String policemanNick) 
	{
		String nick = player.getName();
		if(player == null || !isArrested(nick))
			return false;
		
		Arrested arrested = null;
		for(Arrested value : getArrested())
		{
			if(value.getNick().equals(nick)) 
			{
				arrested = value;
				break;
			}
		}
		
		if(arrested == null)
			return false;
		
		player.teleport(arrested.getInitialLocation());
		this.arrested.remove(arrested);
		PolicePlugin.getInstance().getCuffManager().uncuff(nick);
		config.get().set("arrested." + player.getName(), null);
		config.save();
		return true;
	}
	
	public List<Arrested> getArrested() { return arrested; }

	@Override
	public void sync() 
	{
		for(Arrested arrested : getArrested())
		{
			String nick = arrested.getNick();
			Location initialLocation = arrested.getInitialLocation();
			Bukkit.getLogger().severe("x " + config.get());
			Bukkit.getLogger().severe("s " + initialLocation);
			config.get().set("arrested." + nick + ".initial_location.world", initialLocation.getWorld().getName());
			config.get().set("arrested." + nick + ".initial_location.x", initialLocation.getX());
			config.get().set("arrested." + nick + ".initial_location.y", initialLocation.getY());
			config.get().set("arrested." + nick + ".initial_location.z", initialLocation.getZ());
			config.get().set("arrested." + nick + ".seconds_left", arrested.secondsLeft);
		}
		
		config.save();
	}		
}
