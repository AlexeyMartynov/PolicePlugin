package io.github.alexeymartynov.police.managers;

import java.util.ArrayList;
import java.util.List;

import io.github.alexeymartynov.police.Config;

public class CuffManager implements IManager {
	
	private Config config = Config.CUFFED;
	private List<String> cuffed = new ArrayList<>();
	
	public CuffManager() { cuffed = config.get().getStringList("cuffed") == null ? new ArrayList<>() : config.get().getStringList("cuffed"); }
	
	public boolean isCuffed(String nick) { return cuffed.contains(nick); }
	
	public boolean cuff(String nick) 
	{ 
		if(cuffed.contains(nick))
			return false;
		
		cuffed.add(nick); 
		sync();
		return true;
	} 

	public boolean uncuff(String nick) 
	{ 
		if(!cuffed.contains(nick))
			return false;
		
		cuffed.remove(nick);
		sync();
		return true;
	}
	
	@Override
	public void sync() 
	{
		config.get().set("cuffed", cuffed);
		config.save();
	}
}
