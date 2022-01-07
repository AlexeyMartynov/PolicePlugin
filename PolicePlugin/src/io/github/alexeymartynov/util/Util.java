package io.github.alexeymartynov.util;

import io.github.alexeymartynov.police.main.PolicePlugin;
import net.milkbowl.vault.economy.Economy;

public class Util {

	private static Economy economy = PolicePlugin.getInstance().getEconomy();
	
    public static void addMoney(String nick, int amount) { economy.depositPlayer(nick, amount); }
    
    public static double getMoney(String nick) { return economy.getBalance(nick); }
    
    public static String formatString(String string) { return string.replace("&", "§"); }
}
