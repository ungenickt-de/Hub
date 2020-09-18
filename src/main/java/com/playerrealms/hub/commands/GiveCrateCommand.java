package com.playerrealms.hub.commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.crates.CrateAPI;

import net.md_5.bungee.api.ChatColor;

public class GiveCrateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length < 3){
			return false;
		}
		
		String pl = args[0];
		String type = args[1];
		int amount = Integer.parseInt(args[2]);
		
		
		Player player = Bukkit.getPlayer(pl);
		
		if(player == null){
			sender.sendMessage(ChatColor.RED+"Unknown player "+pl);
			return true;
		}
		
		try {
			for(int i = 0; i < amount;i++){
				CrateAPI.giveCrate(player.getUniqueId(), type);
			}
			sender.sendMessage(ChatColor.GREEN+"Gave "+amount+" "+type+" to "+player.getName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}

}
