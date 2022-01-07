package io.github.alexeymartynov.police;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class PoliceInvite {

	private String invited, inviter;
	
	public PoliceInvite(String invited, String inviter) 
	{
		this.invited = invited;
		this.inviter = inviter;
		
        IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + Message.POLICE_INVITE.get() + "\",\"extra\":[{\"text\":\"§aПРИНЯТЬ\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§aПрисоединиться в ряды Полиции\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/police join\"}}]}");
        PacketPlayOutChat packet = new PacketPlayOutChat(comp);
        ((CraftPlayer) Bukkit.getPlayer(invited)).getHandle().playerConnection.sendPacket(packet);
	}
	
	public String getInvited() { return new String(invited); }
	
	public String getInviter() { return new String(inviter); }
}
