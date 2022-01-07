package io.github.alexeymartynov.police;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.github.alexeymartynov.police.main.PolicePlugin;
import io.github.alexeymartynov.util.Util;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;

public class Call {

	private static List<Call> calls = new ArrayList<>();
	private static int callReward = Config.MAIN.get().getInt("call_reward");
	
	private String nick;
	private Location location;
	private int time = 5;
	
	public static void startCalls() 
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PolicePlugin.getInstance(), new Runnable()
		{
		    @Override
		    public void run() 
		    { 
		    	List<Call> remove = new ArrayList<>();
		    	for(int index = 0; index < calls.size(); index++)
		    	{
		    		Call call = calls.get(index);
		    		if(call.time < 0)
		    		{
		    			Player player = Bukkit.getPlayer(call.getNick());
		    			if(player != null)
		    				player.sendMessage(Message.CALL_DECLINE.get());
		    			
		    			remove.add(call);
		    			continue;
		    		}
		    		
		    		call.time--;
		    	}
		    	
		    	for(Call call : remove)
		    		calls.remove(call);
		    }
		    
		}, 0L, 100L);
	}
	
	public Call(String nick, Location location, int index) 
	{
		this.nick = nick;
		this.location = location;
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + Message.CALL.get().replace("#nick#", nick)
				+ "\",\"extra\":[{\"text\":\"§aÏÐÈÍßÒÜ\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§aÏðèíÿòü âûçîâ\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/police callaccept " + index + "\"}}]}");
        PacketPlayOutChat packet = new PacketPlayOutChat(comp);
		for(Policeman policeman : PolicePlugin.getInstance().getPoliceManager().getPolicemen())
		{
			Player player = Bukkit.getPlayer(policeman.getNick());
			if(player != null)			
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
		
		calls.add(this);
	}	
	
	public String getNick() { return new String(nick); }
	
	public void accept(Policeman policeman) 
	{
		if(policeman == null)
			return;
		
		Player player = Bukkit.getPlayer(policeman.getNick());
		if(player == null)
			return;
	
		player.teleport(location);
		Util.addMoney(nick, callReward);
		PolicePlugin.getInstance().getPoliceManager().sendMessage(Message.CALL_ACCEPT_BY.get().replace("#policeman_nick#", policeman.getNick()).replace("#nick#", getNick())); player.sendMessage("4");
		player.sendMessage(Message.CALL_ACCEPT.get() + Math.round(location.getX()) + ";" + Math.round(location.getY()) + ";" + Math.round(location.getZ()));
		calls.remove(this);
	}
	
	public static List<Call> getCalls() { return calls; }
}
