package com.playerrealms.hub.commands;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.common.ServerInformation;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.redis.JedisAPI;

import net.md_5.bungee.api.ChatColor;

public class ThirdPartyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(sender instanceof Player){
			
			Player pl = (Player) sender;
			
			List<ServerInformation> servers = DropletAPI.getByOwner(pl);
			
			if(servers.size() > 0){
				
				ServerInformation target = null;
				
				if(servers.size() == 1){
					target = servers.get(0);
				}else{
					if(args.length == 0){
						sender.sendMessage(ChatColor.RED+"Syntax: /thirdparty [server name]");
					}else{
						String name = args[0];
						
						for(ServerInformation info : servers){
							if(info.getName().equalsIgnoreCase(name)){
								target = info;
								break;
							}
						}
						
						if(target == null){
							sender.sendMessage(ChatColor.RED+"You don't own the server "+name);
							return true;
						}
					}
				}
				
				if(!target.isThirdParty()){
					String code = generateRandomCode();
					
					DropletAPI.stopServer(target.getName(), false);
					
					DropletAPI.setMetadata(target.getName(), "code", code);
					DropletAPI.setMetadata(target.getName(), "thirdparty", "y");
					
					JedisAPI.setKey("thirdparty."+code, target.getName());
					
					sender.sendMessage(ChatColor.GREEN+"Your server has been converted to third-party. Use the code "+ChatColor.RED+code+ChatColor.GREEN+" in your config to link your server to PlayerRealms.");
					
				}else{
					DropletAPI.setMetadata(target.getName(), "thirdparty", "");
					DropletAPI.setMetadata(target.getName(), "code", "");
					sender.sendMessage(ChatColor.GREEN+"Your server is no longer third party.");
				}
				
			}else{
				sender.sendMessage(ChatColor.RED+"You don't own any servers.");
			}
			
		}
		
		
		return true;
	}
	
	protected String generateRandomCode() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 7) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr.toLowerCase();

    }

}
