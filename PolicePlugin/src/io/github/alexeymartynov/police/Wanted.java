package io.github.alexeymartynov.police;

public class Wanted {

	private String nick;
	private int wantedLevel;
	
	public Wanted(String nick, int wantedLevel) 
	{ 
		this.nick = nick; 
		this.wantedLevel = wantedLevel;
	}
	
	public int getWantedLevel() { return wantedLevel; }
	
	public String getNick() { return new String(nick); }
	
	public void setWantedLevel(int wantedLevel) { this.wantedLevel = wantedLevel; }
}
