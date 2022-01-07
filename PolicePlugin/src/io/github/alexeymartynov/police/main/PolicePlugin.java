package io.github.alexeymartynov.police.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.alexeymartynov.police.Call;
import io.github.alexeymartynov.police.Frisk;
import io.github.alexeymartynov.police.handlers.CuffHandler;
import io.github.alexeymartynov.police.handlers.FriskHandler;
import io.github.alexeymartynov.police.handlers.JailHandler;
import io.github.alexeymartynov.police.managers.CuffManager;
import io.github.alexeymartynov.police.managers.JailManager;
import io.github.alexeymartynov.police.managers.PoliceManager;
import net.milkbowl.vault.economy.Economy;

public class PolicePlugin extends JavaPlugin {

	private static PolicePlugin instance;

	public static PolicePlugin getInstance() { return instance; }
	
	private PoliceManager policeManager;
	private JailManager jailManager;
	private CuffManager cuffManager;
	
	private CuffHandler cuffHandler;
	private JailHandler jailHandler;
	private FriskHandler friskHandler;
	
	private Economy economy;
	
	public void onEnable() 
	{	
		instance = this; 
	    economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	    if(economy == null) 
	    {
	    	Bukkit.getLogger().severe("PolicePlugin was disabled because Economy has errors in setup"); 
			Bukkit.getPluginManager().disablePlugin(PolicePlugin.getInstance());
	    	return;
	    }
	    
	    registerManagers();
	    registerCommands();
	    registerListeners();
	    
		Call.startCalls();
		Frisk.setupProhibited();
	}
	
	public void onDisable() 
	{
		jailManager.sync();
		cuffManager.sync();
		policeManager.sync();
	}

	public PoliceManager getPoliceManager() { return policeManager; }
	
	public void registerManagers() 
	{
		jailManager = new JailManager();
		cuffManager = new CuffManager();
		policeManager = new PoliceManager();
	}
	
	public void registerListeners() 
	{
		jailHandler = new JailHandler();
		cuffHandler = new CuffHandler();
		friskHandler = new FriskHandler();
		Bukkit.getPluginManager().registerEvents(jailHandler, getInstance());
		Bukkit.getPluginManager().registerEvents(cuffHandler, getInstance());
		Bukkit.getPluginManager().registerEvents(friskHandler, getInstance());
	}

	public void registerCommands()
	{
		PoliceCommands executor = new PoliceCommands();
		getCommand("police").setExecutor(executor);
		getCommand("911").setExecutor(executor);
	}
	
	public JailHandler getJailHandler() { return jailHandler; } 
	
	public CuffHandler getCuffHandler() { return cuffHandler; }
	
	public JailManager getJailManager() { return jailManager; } 
	
	public CuffManager getCuffManager() { return cuffManager; }
	
	public Economy getEconomy() { return economy; }
}
