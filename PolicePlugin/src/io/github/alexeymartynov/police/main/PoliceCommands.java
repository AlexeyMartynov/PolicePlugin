package io.github.alexeymartynov.police.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import io.github.alexeymartynov.police.Call;
import io.github.alexeymartynov.police.Config;
import io.github.alexeymartynov.police.Message;
import io.github.alexeymartynov.police.PoliceInvite;
import io.github.alexeymartynov.police.Policeman;
import io.github.alexeymartynov.police.Rank;
import io.github.alexeymartynov.police.managers.CuffManager;
import io.github.alexeymartynov.police.managers.JailManager;
import io.github.alexeymartynov.police.managers.PoliceManager;
import io.github.alexeymartynov.util.Util;

public class PoliceCommands implements CommandExecutor {

	private List<PoliceInvite> invites = new ArrayList<>();
	private double nearRadius = Config.MAIN.get().getDouble("near_radius");
	
	private boolean isPlayerNear(Player player1, Player player2) 
	{ 
		if(player1 == null || player2 == null || player1.getLocation().distance(player2.getLocation()) > nearRadius) 
		{
			player1.sendMessage(Message.PLAYER_IS_NOT_EXIST.get());
			return false;
		}
		
		return true;
	}
	
	private PoliceInvite getInvite(String nick) 
	{
		for(PoliceInvite invite : invites)
			if(invite.getInvited().equals(nick))
				return invite;
		
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		PoliceManager policeManager = PolicePlugin.getInstance().getPoliceManager();
		CuffManager cuffManager = PolicePlugin.getInstance().getCuffManager(); 
		String nick = sender.getName();
		if(command.getName().equalsIgnoreCase("police"))
		{		
			if(args.length < 1)
				return false;
			
			if(args[0].equalsIgnoreCase("help")) 
			{
				ConfigurationSection section = Config.MAIN.get().getConfigurationSection("help");
				if(section == null)
					return true;
				
				List<String> messages = new ArrayList<>();
				for(String key : section.getKeys(false)) 
				{
					switch(key)
					{
					case "all":
						messages.addAll(section.getStringList("all"));
						break;
					case "police":
						if(policeManager.getPoliceman(nick) != null) 
							messages.addAll(section.getStringList("police"));
							
						break;
					case "op":
						if(sender.isOp()) 
							messages.addAll(section.getStringList("op"));
						
						break;
					default: break;
					}
				}
				
				for(String message : messages)
					sender.sendMessage(Util.formatString(message));
				
				return true;
			}
			
			JailManager jailManager = PolicePlugin.getInstance().getJailManager();
			for(PoliceCommand permission : PoliceCommand.values()) 
			{	
				if(!args[0].equalsIgnoreCase(permission.toString())) continue;
				
				if(permission.getArgsLength() < args.length)
					return false;
				
				if(permission != PoliceCommand.SETLEADER && !(sender instanceof Player))
					return false;
				 
				if(permission.isPoliceCommand())
				{
					if(!sender.isOp()) 
					{
						Policeman policeman = policeManager.getPoliceman(nick);
						if(policeman == null) 
						{
							sender.sendMessage(Message.YOU_ARE_NOT_POLICEMAN.get());
							return true;
						}
						
						if(!policeman.getRank().hasPermission(permission))
						{
							sender.sendMessage(Message.NOT_ENOUGH_PERMISSIONS.get());
							return true;
						}
					}
				}
				
				Player player = args.length > 1 ? Bukkit.getPlayer(args[1]) : null;
				if(permission.getArgsLength() >= 2 && permission != permission.CALLACCEPT)
				{
					if(player == null) 
					{
					    sender.sendMessage(Message.PLAYER_IS_NOT_EXIST.get());
						return true;
					}
				}
				
				Policeman policeman;
				Player user = Bukkit.getPlayer(nick);
				switch(permission) 
				{
				case SETLEADER:
					if(!sender.isOp())
						return false;
					
					if(Bukkit.getPlayer(args[1]) == null)
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_EXIST.get());
						return true;
					}
					
					List<Policeman> kick = new ArrayList<>();
					for(Policeman value : policeManager.getPolicemen()) 
					{
						if(value.getRank() == Rank.getHighestRank())
							kick.add(value);
					}
					
					for(Policeman value : kick)
						value.kick();

					policeman = policeManager.getPoliceman(args[1]);
					if(policeman == null)
						new Policeman(args[1], Rank.getHighestRank(), 0, 0, false);
					else policeman.setRank(Rank.getHighestRank());
					
					for(Player onlinePlayer : Bukkit.getOnlinePlayers())
						onlinePlayer.sendMessage(Message.SET_LEADER.get().replace("#nick#", args[1]));
						
					return true;
				case CUFF:
					if(!isPlayerNear(user, player))
						return true;
					
					if(!PolicePlugin.getInstance().getCuffManager().cuff(args[1])) 
					{
						sender.sendMessage(Message.PLAYER_IS_ALREADY_CUFFED.get());
						return true;
					}
					
					sender.sendMessage(Message.SUCCESFUL.get());					
					player.sendMessage(Message.CUFF.get());				
					return true;
				case UNCUFF:
					if(!isPlayerNear(user, player))
						return true;
					
					if(!PolicePlugin.getInstance().getCuffManager().uncuff(args[1])) 
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_CUFFED.get());
						return true;
					}
					
					sender.sendMessage(Message.SUCCESFUL.get());					
					player.sendMessage(Message.UNCUFF.get());
					return true;
				case ARREST:
					if(!isPlayerNear(user, player))
						return true;
					
					if(!cuffManager.isCuffed(args[1])) 
					{
						sender.sendMessage(Message.PLAYER_MUST_ME_CUFFED.get());
						return true;
					}
						
					if(jailManager.arrest(player, nick))
						sender.sendMessage(Message.SUCCESFUL.get());
					
					return true;
				case UNARREST:
					if(!jailManager.isArrested(args[1])) 
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_ARRESTED.get());
						return true;
					}
					
					if(jailManager.unarrest(player, nick))
						sender.sendMessage(Message.SUCCESFUL.get());
					
					return true;
				case INVITE:
					if(!isPlayerNear(user, player))
						return true;
					
					if(policeManager.getPoliceman(args[1]) != null) 
					{
						sender.sendMessage(Message.PLAYER_IS_ALREADY_POLICEMAN.get());
						return true;
					}
					
					if(getInvite(args[1]) != null) 
					{
						sender.sendMessage(Message.PLAYER_IS_ALREADY_INVITED.get());
						return true;
					}
	
					if(policeManager.getPolicemanAmountWithRank(Rank.getLowestRank()) >= Rank.getLowestRank().getPlaces())
					{
						sender.sendMessage(Message.TOO_MUCH_POLICEMEN_WITH_THIS_RANK.get());
						return true;
					}
					
					invites.add(new PoliceInvite(args[1], nick));
					sender.sendMessage(Message.SUCCESFUL.get());
					return true;
				case KICK:
					if(Bukkit.getPlayer(args[1]) == null)
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_EXIST.get());
						return true;
					}
					
					policeman = policeManager.getPoliceman(args[1]);
					if(policeman == null) 
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_POLICEMAN.get());
						return true;
					}
					
					if(player != null)
						player.sendMessage(Message.YOU_HAVE_BEEN_KICKED.get());
					
					policeman.kick();
					sender.sendMessage(Message.SUCCESFUL.get());
					return true;
				case JOIN:
					PoliceInvite invite = getInvite(nick);
					if(invite == null) 
					{
						sender.sendMessage(Message.YOU_ARE_NOT_INVITED.get());
						return true;
					}
					
					if(policeManager.getPolicemanAmountWithRank(Rank.getLowestRank()) >= Rank.getLowestRank().getPlaces())
					{
						sender.sendMessage(Message.TOO_MUCH_POLICEMEN_WITH_THIS_RANK.get());
						return true;
					}
					
					new Policeman(nick, Rank.getLowestRank(), 0, 0, false);
					invites.remove(invite);
					sender.sendMessage(Message.SUCCESFUL.get());
					return true;
				case PAYDAY:
					policeManager.payDay();
					return true;
				case SETRANK:
					if(args.length < 3)
						return false;
					
					if(Bukkit.getPlayer(args[1]) == null)
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_EXIST.get());
						return true;
					}
					
					policeman = policeManager.getPoliceman(args[1]);
					if(policeman == null) 
					{
						sender.sendMessage(Message.PLAYER_IS_NOT_POLICEMAN.get());
						return true;
					}
					
					Rank rank = Rank.getRank(args[2]);
					if(rank == null) 
					{
						sender.sendMessage(Message.RANK_IS_NOT_EXIST.get());
						return true;
					}
					
					if(policeManager.getPolicemanAmountWithRank(rank) >= rank.getPlaces())
					{
						sender.sendMessage(Message.TOO_MUCH_POLICEMEN_WITH_THIS_RANK.get());
						return true;
					}
					
					policeman.setRank(rank);
					sender.sendMessage(Message.SUCCESFUL.get());
					return true;
				case CALLACCEPT:
					Call call; 
					try { call = Call.getCalls().get(Integer.parseInt(args[1])); }
					catch(Exception exception) 
					{ 
						sender.sendMessage(Message.CALL_IS_NOT_EXIST.get());
						return true;
					} 
					
					call.accept(policeManager.getPoliceman(nick));
					return true;
				default: break;
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("911")) 
		{
			if(policeManager.getPoliceman(nick) != null) 
				return true;
			
			for(Call call : Call.getCalls()) 
			{
				if(call.getNick().equals(nick)) 
				{
					sender.sendMessage(Message.YOU_HAVE_ALREADY_CALLED.get());
					return true;
				}
			}
			
			new Call(nick, Bukkit.getPlayer(nick).getLocation(), Call.getCalls().size());
			sender.sendMessage(Message.SUCCESFUL.get());
			return true;
		}
		
		return false;
	}

}
