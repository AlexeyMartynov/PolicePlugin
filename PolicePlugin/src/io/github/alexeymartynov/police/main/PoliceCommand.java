package io.github.alexeymartynov.police.main;

public enum PoliceCommand {

	ARREST(true, 2),
	//CALL(false, 2),
	CUFF(true, 2),
	UNCUFF(true, 2),
	FRISK(true, 2),
	INVITE(true, 2),
	KICK(true, 2),
	SETRANK(true, 3),
	PAYDAY(true, 1),
	SETLEADER(true, 2),
	CALLACCEPT(true, 2),
	UNARREST(true, 2),
	JOIN(false, 1);
	
	private boolean policeCommand;
	private int length;
	
	private PoliceCommand(boolean policeCommand, int length) 
	{ 
		this.policeCommand = policeCommand; 
		this.length = length;
	}
	
	public boolean isPoliceCommand() { return policeCommand; }
	
	public int getArgsLength() { return length; }
}
