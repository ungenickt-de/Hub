package com.playerrealms.hub.management.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.ServerDroplet;
import com.playerrealms.droplet.rank.Rank;

import net.md_5.bungee.api.ChatColor;

public class SetRankCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length < 2){
			return false;
		}
		
		Player pl = (Player) sender;
		
		if(!DropletAPI.getRank(pl).hasPermission("playerrealms.manage")){
			pl.sendMessage(ChatColor.RED+"No permission.");
			return true;
		}
		
		String plName = args[0];
		
		Player target = Bukkit.getPlayer(plName);
		
		if(target == null){
			sender.sendMessage(ChatColor.RED+"Unknown player "+plName);
			return true;
		}
		
		String rankName = args[1];
		
		Rank rank = ServerDroplet.getInstance().getRank(rankName);
		
		if(rank == null){
			sender.sendMessage(ChatColor.RED+"Unknown rank "+rankName);
			return true;
		}
		
		DropletAPI.setRank(target, rank);
		
		sender.sendMessage(ChatColor.GREEN+"Rank set");
		
		return true;
	}

}
