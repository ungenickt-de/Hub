package com.playerrealms.hub.commands;

import com.playerrealms.hub.player.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParkourCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(args.length != 1){
            player.sendMessage(ChatColor.GREEN+"/parkour [reset/checkpoint]");
            return true;
        }
        if(!PlayerListener.Parkour.containsKey(player)){
            player.sendMessage(ChatColor.GREEN+"You are not playing parkour.");
            return true;
        }
        if(args[0].equals("reset")){
            Location startloc = PlayerListener.StartLoc.get(player);
            player.teleport(startloc);
            return true;
        }else if(args[0].equals("checkpoint")){
            if(!PlayerListener.CheckLoc.containsKey(player)){
                player.sendMessage(ChatColor.GREEN+"You are not reached in checkpoint.");
                return true;
            }
            Location checkloc = PlayerListener.CheckLoc.get(player);
            player.teleport(checkloc);
            return true;
        }else{
            player.sendMessage(ChatColor.GREEN+"/parkour [reset/checkpoint]");
            return true;
        }
    }
}
