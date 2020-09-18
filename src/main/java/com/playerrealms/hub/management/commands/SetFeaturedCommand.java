package com.playerrealms.hub.management.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.common.ServerInformation;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;

import net.md_5.bungee.api.ChatColor;

public class SetFeaturedCommand implements CommandExecutor
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
			Player player = (Player) sender;

			if (args.length > 0)
			{
				String name = args[0];

				if (name.equals("*"))
				{
					DropletAPI.removeFeaturedServer();
					Language.sendMessage(player, "hub.setfeatured.removed");
					return true;
				}

				ServerInformation info = DropletAPI.getServerInfo(name);

				if (info == null)
				{
					Language.sendMessage(player, "response_codes.server_unknown", name);
				}
				else
				{
					DropletAPI.setFeaturedServer(info);
					Language.sendMessage(player, "hub.setfeatured.set");
				}
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Player only.");
		}
		return true;
	}
}
