package com.playerrealms.hub.commands;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.playerrealms.droplet.crates.CrateAPI;
import com.playerrealms.droplet.crates.GlobalCrateTypes;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.droplet.util.BirthdayAPI;
import com.playerrealms.droplet.util.BirthdayAPI.Birthday;

public class BirthdayCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		
		Player player = (Player) sender;

		
		try {
			boolean birthdaySet = BirthdayAPI.isBirthdaySet(player.getUniqueId());
			
			if(args.length == 2){
				
				try {
					int month = Integer.parseInt(args[0]);
					int day = Integer.parseInt(args[1]);
					
					if(birthdaySet){
						Language.sendMessage(player, "birthday.already_set");
					}else{
						if(month < 1 || month > 12){
							Language.sendMessage(player, "birthday.set_birthday");
						}else if(day < 1 || day > 31){
							Language.sendMessage(player, "birthday.set_birthday");
						}else{
							BirthdayAPI.updateBirthday(player.getUniqueId(), month, day);
							Language.sendMessage(player, "birthday.birthday_set", month+"/"+day);
						}
					}

					return true;
				} catch (NumberFormatException e) {
					Language.sendMessage(player, "birthday.set_birthday");
				}

				return true;
			}
			
			if(birthdaySet){
				
				Birthday birthday = BirthdayAPI.getBirthday(player.getUniqueId());
				
				GlobalCrateTypes birthdayCrate = GlobalCrateTypes.BIRTHDAY;
				
				if(birthday.canClaimPrize()){
					Language.sendMessage(player, "birthday.reward", birthdayCrate.getName(player));
					BirthdayAPI.updatePrizeYear(player.getUniqueId());
					CrateAPI.giveCrate(player.getUniqueId(), birthdayCrate.getTypeString());
					
				}else{
					Language.sendMessage(player, "birthday.not_your_birthday", birthday.getMonth()+"/"+birthday.getDay());
				}
				
			}else{
				Language.sendMessage(player, "birthday.set_birthday");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			Language.sendMessage(player, "birthday.error");
		}
		
		return true;
	}

}
