package io.github.alexeymartynov.police.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.alexeymartynov.police.Config;
import io.github.alexeymartynov.police.Frisk;
import io.github.alexeymartynov.police.Message;
import io.github.alexeymartynov.police.Policeman;
import io.github.alexeymartynov.police.main.PoliceCommand;
import io.github.alexeymartynov.police.main.PolicePlugin;
import io.github.alexeymartynov.util.UtilItem;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class FriskHandler implements Listener {

	private ItemStack friskItem;
	
	public FriskHandler() 
	{ 
		ConfigurationSection section = Config.MAIN.get().getConfigurationSection("frisk_item");
		friskItem = UtilItem.create(CraftItemStack.asNewCraftStack(Item.REGISTRY.get(new MinecraftKey(section.getString("id")))).getType(), section.getString("name"));
		friskItem = UtilItem.create(friskItem, section.getStringList("lore"));
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) 
	{
		if(event.getHand() == EquipmentSlot.OFF_HAND)
			return;
		
		if(!(event.getRightClicked() instanceof Player))
			return;
		
		Player player = event.getPlayer();
		Policeman policeman = PolicePlugin.getInstance().getPoliceManager().getPoliceman(player.getName());
		if(policeman == null || !policeman.getRank().hasPermission(PoliceCommand.FRISK))
			return;
		
		ItemStack item = player.getItemInHand();
		if(item == null || !UtilItem.areTheSameItems(item, friskItem))
			return;
		
		if(!new Frisk((Player) event.getRightClicked(), player).doIt())
			player.sendMessage(Message.NOTHING_ON_FRISK.get());
	}
}
