package com.playerrealms.hub.management.commands;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.ServerDroplet;
import com.playerrealms.droplet.rank.Rank;
import com.playerrealms.droplet.sql.DatabaseAPI;

import net.md_5.bungee.api.ChatColor;

public class PermissionCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length < 3){
			return false;
		}
		
		Player pl = (Player) sender;
		
		if(!DropletAPI.getRank(pl).hasPermission("playerrealms.manage")){
			pl.sendMessage(ChatColor.RED+"No permission.");
			return true;
		}
		
		String rankName = args[0];
		String operation = args[1];
		String permission = args[2];
		
		Rank rank = ServerDroplet.getInstance().getRank(rankName);
		
		if(rank == null){
			sender.sendMessage(ChatColor.RED+"Unknown rank "+rankName);
			return true;
		}
		
		if(operation.equalsIgnoreCase("add")){
			
			try {
				if(!rank.hasPermission(permission)){
					DatabaseAPI.execute("INSERT INTO `permissions` (`rank`,`permission`) VALUES (?, ?)", rank.getId(), permission);
					
					DropletAPI.reloadRanks();
					sender.sendMessage(ChatColor.GREEN+"Added permission.");
				}else{
					sender.sendMessage(ChatColor.RED+rank.getName()+" already has that permission.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.DARK_RED+"Failed to add permission");
			}
			
		}else if(operation.equalsIgnoreCase("remove")){
			try {
				if(rank.hasPermission(permission)){
					DatabaseAPI.execute("DELETE FROM `permissions` WHERE `rank`=? AND `permission`=?", rank.getId(), permission);
					DropletAPI.reloadRanks();
					sender.sendMessage(ChatColor.GREEN+"Removed permission");
				}else{
					sender.sendMessage(ChatColor.RED+rank.getName()+" doesn't have that permission.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.DARK_RED+"Failed to remove permission");
			}
		}else{
			sender.sendMessage(ChatColor.RED+"Unknown operation "+operation);
			return false;
		}
		
		
		return true;
	}

}
