package com.playerrealms.hub.commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.sql.DatabaseAPI;
import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.menu.ReferFriendMenu;

import net.md_5.bungee.api.ChatColor;

public class ReferAcceptCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED+"Player only.");
			return true;
		}
		
		Player refered = (Player) sender;
		
		if(HubPlugin.getInstance().getPlayerManager().getPlayer(refered).isReferred()){
			sender.sendMessage(ChatColor.RED+"You have already accepted another persons referal before.");
			return true;
		}
		
		if(args.length < 1){
			return false;
		}
		
		String plName = args[0];
		
		Player pl = Bukkit.getPlayer(plName);
		
		if(pl == null){
			sender.sendMessage(ChatColor.RED+"That player is currently offline");
			return true;
		}
		
		if(ReferFriendMenu.referExists(pl.getUniqueId(), refered.getUniqueId())){
			try {
				DatabaseAPI.execute("UPDATE `players` SET `referral`=? WHERE `uuid`=?", 1, refered.getUniqueId().toString());
			
				final int bonus = 200;
				
				DropletAPI.changeCoins(refered, bonus);
				DropletAPI.changeCoins(pl, bonus);
				
				sender.sendMessage(ChatColor.GREEN+"You and "+ChatColor.YELLOW+pl.getName()+ChatColor.GREEN+" have been credited "+bonus+" coins!");
				pl.sendMessage(ChatColor.GREEN+"Thank you for referring "+ChatColor.YELLOW+sender.getName()+ChatColor.GREEN+"! You have received "+bonus+" coins.");
				
				HubPlugin.getInstance().getPlayerManager().getPlayer(refered).setReferred(true);
				
			} catch (SQLException e) {
				e.printStackTrace();
				pl.sendMessage(ChatColor.RED+"Referring a friend failed! Please alert a staff member.");
			}
			
		}else{
			sender.sendMessage(ChatColor.RED+"Sorry, I couldn't find that referal. They expire after 5 minutes.");
		}
		
		return true;
	}

}
