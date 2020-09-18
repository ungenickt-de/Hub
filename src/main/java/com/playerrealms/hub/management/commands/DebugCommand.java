package com.playerrealms.hub.management.commands;

import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.hub.management.DebugMenu;
import com.playerrealms.hub.ProCosmetics;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player pl = (Player) sender;
		
		if(!DropletAPI.getRank(pl).hasPermission("playerrealms.manage")){
			pl.sendMessage(ChatColor.RED+"No permission.");
			return true;
		}
		if (sender instanceof Player)
		{
			if(args.length == 0) {
				Player player = (Player) sender;
				new DebugMenu(player).open(player);
			}else{
				if(args[0].equals("help")){
					pl.sendMessage(ChatColor.RED+"/debug <getchest/getcoin> <player> <normal/mythical/legendary>");
					return true;
				} else if(args[0].equals("getchest")){
					int count;
					Player other = Bukkit.getPlayer(args[1]);
					if(other != null) {
						count = ProCosmetics.getTreasure(other, args[2]);
						pl.sendMessage(String.valueOf(count));
					}else{
						pl.sendMessage("offline player?");
					}
					return true;
				}else if(args[0].equals("getcoin")){
					int count;
					Player other = Bukkit.getPlayer(args[1]);
					if(other != null) {
						count = ProCosmetics.getCoins(other);
						pl.sendMessage(String.valueOf(count));
					}else{
						pl.sendMessage("offline player?");
					}
					return true;
				}
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Player only.");
		}
		return true;
	}
}