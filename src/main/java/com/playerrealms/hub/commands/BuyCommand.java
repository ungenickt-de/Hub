package com.playerrealms.hub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.lang.Language;

public class BuyCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender instanceof Player){
			Player pl = (Player) sender;
			Language.sendMessage(pl, "generic.store_message", Language.getText(pl, "generic.store_url"));
		}
		
		return true;
	}
}