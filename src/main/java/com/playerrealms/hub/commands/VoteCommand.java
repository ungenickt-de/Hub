package com.playerrealms.hub.commands;

import java.util.concurrent.TimeUnit;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.common.ServerInformation;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;

import net.md_5.bungee.api.ChatColor;

public class VoteCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				Language.sendMessage(player, "hub.vote.help");
				return true;
			}

			String jedisKey = "servervote." + player.getUniqueId() + ".time";
			
			if (JedisAPI.keyExists(jedisKey))
			{
				long time = Long.parseLong(JedisAPI.getValue(jedisKey));

				if (System.currentTimeMillis() - time < TimeUnit.DAYS.toMillis(1))
				{
					Language.sendMessage(player, "hub.vote.time");
					return true;
				}
			}

			String serverName = args[0];

			ServerInformation info = DropletAPI.getServerInfo(serverName);
			if (info == null)
			{
				Language.sendMessage(player, "response_codes.server_unknown", serverName);
			}
			else
			{
				int votes = info.getVotes();

				DropletAPI.setMetadata(info.getName(), "votes", String.valueOf(votes + 1));
				DropletAPI.saveMetadata(info.getName());

				Language.sendMessage(player, "hub.vote.success", serverName);
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);

				JedisAPI.setKey(jedisKey, String.valueOf(System.currentTimeMillis()));
				JedisAPI.publish("server_vote", player.getUniqueId()+" "+info.getName());
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Player only.");
		}
		return true;
	}
}