package io.github.alexeymartynov.police;

import org.bukkit.Location;

public class Arrested {

	private String nick;
	private Location initialLocation;
	
	public long secondsLeft;
	
	public Arrested(String nick, long secondsLeft, Location initialLocation) 
	{
		this.nick = nick;
		this.secondsLeft = secondsLeft;
		this.initialLocation = initialLocation;
	}
	
	public String getNick() { return new String(nick); }
	
	public Location getInitialLocation() { return initialLocation; }
}
