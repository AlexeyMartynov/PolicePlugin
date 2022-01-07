package io.github.alexeymartynov.police.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import io.github.alexeymartynov.police.Arrested;
import io.github.alexeymartynov.police.main.PolicePlugin;
import io.github.alexeymartynov.police.managers.JailManager;

public class JailHandler implements Listener {
	
	private JailManager manager = PolicePlugin.getInstance().getJailManager();
	
	public JailHandler() 
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PolicePlugin.getInstance(), new Runnable()
		{
		    @Override
		    public void run() 
		    { 
		    	for(Arrested arrested : manager.getArrested()) 
		    	{
		    		if(arrested.secondsLeft - 5 < 0) 
		    		{
		    			manager.unarrest(Bukkit.getPlayer(arrested.getNick()), null);
		    			continue;
		    		}
		    			
		    		arrested.secondsLeft -= 5;
		    	}
		    }
		    
		}, 0L, 100L);
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) 
	{
		if(manager.isArrested(event.getPlayer().getName()))
			event.setCancelled(true);
	}
}
