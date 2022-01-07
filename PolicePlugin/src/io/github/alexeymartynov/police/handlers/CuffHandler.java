package io.github.alexeymartynov.police.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import io.github.alexeymartynov.police.main.PolicePlugin;
import io.github.alexeymartynov.police.managers.CuffManager;

public class CuffHandler implements Listener {

	private CuffManager manager;
	
	public CuffHandler() { manager = PolicePlugin.getInstance().getCuffManager(); } 
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) 
	{
		if(!(event.getDamager() instanceof Player))
			return;
		
		if(manager.isCuffed(event.getDamager().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) 
	{
		if(manager.isCuffed(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) 
	{
		if(manager.isCuffed(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) 
	{
		if(manager.isCuffed(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) 
	{
		if(manager.isCuffed(event.getPlayer().getName()))
			event.setCancelled(true);
	}
}
