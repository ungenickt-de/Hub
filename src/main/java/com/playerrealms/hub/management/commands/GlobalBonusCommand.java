package com.playerrealms.hub.management.commands;

import java.util.concurrent.TimeUnit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.common.ServerInformation;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;

import net.md_5.bungee.api.ChatColor;

public class GlobalBonusCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player pl = (Player) sender;
		
		if(!DropletAPI.getRank(pl).hasPermission("playerrealms.manage")){
			pl.sendMessage(ChatColor.RED+"No permission.");
			return true;
		}
		if (args.length > 1)
		{
			int hours = Integer.parseInt(args[0]);
			double bonus = Double.parseDouble(args[1]);
			int servers = 0;

			for (ServerInformation server : DropletAPI.getPlayerServers())
			{
				if (server.getStatus() == ServerStatus.ONLINE)
				{
					long bonusTime = server.getCoinMultiplierTimeLeft();
					bonusTime += TimeUnit.HOURS.toMillis(hours);
					bonusTime += System.currentTimeMillis();
					DropletAPI.setMetadata(server.getName(), "multi", String.valueOf(bonus));
					DropletAPI.setMetadata(server.getName(), "multitime", String.valueOf(bonusTime));
					servers++;
				}
			}

			sender.sendMessage(ChatColor.GREEN + String.valueOf(servers) + " servers affected.");
			return true;
		}
		else
		{
			return false;
		}
	}
}