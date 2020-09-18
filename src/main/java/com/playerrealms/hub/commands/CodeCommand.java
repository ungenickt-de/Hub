package com.playerrealms.hub.commands;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;

import net.md_5.bungee.api.ChatColor;

public class CodeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		
		JedisAPI.removeKey("login."+player.getUniqueId());
		
		String sessionKey = JedisAPI.getValue("api.uuid."+player.getUniqueId());
		
		if(sessionKey != null) {
			JedisAPI.removeKey("api.uuid."+player.getUniqueId());
			JedisAPI.removeKey("api."+sessionKey);
		}
		
		String code = "";
		
		for(int i = 0; i < 4;i++) code += new Random().nextInt(10);
		
		Language.sendMessage(player, "hub_code", ChatColor.AQUA+code);
		
		JedisAPI.setKey("login."+player.getUniqueId(), code);
		
		return true;
	}

}
