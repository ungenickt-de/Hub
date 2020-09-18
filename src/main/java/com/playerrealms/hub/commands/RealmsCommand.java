package com.playerrealms.hub.commands;

import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.menu.ServerListMenu;
import com.playerrealms.hub.menu.ServerListPageEntry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealmsCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Bukkit.getScheduler().runTaskAsynchronously(HubPlugin.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (sender instanceof Player)
				{
					Player player = (Player) sender;
					String type = "players";

					if (args.length > 0)
					{
						type = args[0];
					}

					ServerListPageEntry.ServerListCompareMethod sortMethod = ServerListPageEntry.ServerListCompareMethod.ByOnline;

					boolean official = false;

					if (type.equalsIgnoreCase("players"))
					{
						sortMethod = ServerListPageEntry.ServerListCompareMethod.ByOnline;
					}
					else if (type.equalsIgnoreCase("votes"))
					{
						sortMethod = ServerListPageEntry.ServerListCompareMethod.ByVotes;
					}else if(type.equalsIgnoreCase("official")){
						official = true;
					}
					else
					{
						return;
					}

					new ServerListMenu(sortMethod, player, official, false).open(player);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Player only.");
				}
			}
		});
		return true;
	}
}