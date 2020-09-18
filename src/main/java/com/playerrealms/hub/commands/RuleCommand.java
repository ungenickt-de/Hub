package com.playerrealms.hub.commands;

import com.playerrealms.droplet.lang.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RuleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Language.sendMessage((Player) sender, "generic.rule");
        }

        return true;
    }

}
