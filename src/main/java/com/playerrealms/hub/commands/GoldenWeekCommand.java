package com.playerrealms.hub.commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.crates.CrateAPI;
import com.playerrealms.droplet.crates.GlobalCrateTypes;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.droplet.util.PunchCardApi;
import com.playerrealms.droplet.util.PunchCardApi.PunchDays;
import com.playerrealms.hub.HubPlugin;

public class GoldenWeekCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equals("golden")) {
			
			Player player = (Player) sender;
			
			PunchDays days = PunchCardApi.getToday();
			
			if(days == PunchDays.UNKNOWN) {
				Language.sendMessage(player, "golden.no_punch_today");
				return true;
			}
			
			try {
				if(PunchCardApi.getPunchedDays(player, false).contains(days)) {
					Language.sendMessage(player, "golden.already");
				}else {
					PunchCardApi.punchCard(player, days);
					Language.sendMessage(player, "golden.punch");
					
					int collected = PunchCardApi.getPunchedDays(player, true).size();
					int crates = 0;
					
					String reward = "";
					
					if(collected < 6) {
						reward = Language.getText(player, "crates.name.global", GlobalCrateTypes.WELCOME_BACK.getName(player))+" x "+collected;
						crates = collected;
					}else if(collected == 6) {
						reward = Language.getText(player, "golden.grand_prize")+" + "+Language.getText(player, "crates.name.global", GlobalCrateTypes.WELCOME_BACK.getName(player))+" x 10";
						crates = 10;
						JedisAPI.publish("broadcast", "golden.other_player_finish "+player.getName());
					}
					final int f_crates = crates;
					Bukkit.getScheduler().runTaskAsynchronously(HubPlugin.getInstance(), () -> {
						try {
							CrateAPI.giveManyCrates(player.getUniqueId(), GlobalCrateTypes.WELCOME_BACK.getTypeString(), f_crates);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
					
					Language.sendMessage(player, "golden.punch_reward", reward);
					
					for(Player other : Bukkit.getOnlinePlayers()) {
						if(!other.getUniqueId().equals(player.getUniqueId())) {
							Language.sendMessage(other, "golden.other_player_punch", player.getName(), collected);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				Language.sendMessage(player, "golden.error");
			}
			
			return true;
		}
		return false;
	}

}
