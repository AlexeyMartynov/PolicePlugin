package io.github.alexeymartynov.police;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.alexeymartynov.police.main.PolicePlugin;

public enum Config {

	MAIN("config"),
	RANKS("ranks"),
	PROHIBITED("prohibited"),
	JAIL("jail"),
	WANTED("wanted"),
	CUFFED("cuffs"),
	MESSAGES("messages"),
	POLICE("police");
	
	private File file;
	private FileConfiguration config;
	
	private Config(String name) 
	{
		name = name + ".yml"; 
		file = new File(PolicePlugin.getInstance().getDataFolder() + File.separator + name);
		if(!file.exists()) 
			PolicePlugin.getInstance().saveResource(name, false);
			
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration get() { return config; }
	
	public void save() 
	{
		try { config.save(file); }
		catch(Exception exception) {}
	}
}
