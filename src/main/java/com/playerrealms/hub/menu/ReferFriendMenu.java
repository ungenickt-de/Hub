package com.playerrealms.hub.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.nirvana.menu.AnvilPacketMenu;
import com.nirvana.menu.AnvilPacketMenuHandler;
import com.nirvana.menu.Item;
import com.nirvana.menu.Item.SkullType;
import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.player.HubPlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class ReferFriendMenu extends AnvilPacketMenu implements AnvilPacketMenuHandler {

	public static List<Refer> refers = new ArrayList<>();
	
	public ReferFriendMenu() {
		setResult(new Item(Material.SKULL_ITEM).setSkullType(SkullType.PLAYER).build());
		setClickSound(Sound.ENTITY_PLAYER_LEVELUP);
		setHandler(this);
		setDefaultText("Enter refered friend");
	}

	@Override
	public void onResult(String name, Player pl) {
		
		Player friend = Bukkit.getPlayer(name);
		
		if(friend == null){
			pl.sendMessage(ChatColor.RED+"No player is online with that name!");
			return;
		}
		
		if(friend.getUniqueId().equals(pl.getUniqueId())){
			pl.sendMessage(ChatColor.RED+"You cannot refer yourself.");
			return;
		}
		
		HubPlayer friendHp = HubPlugin.getInstance().getPlayerManager().getPlayer(friend);
		
		if(friendHp.isReferred()){
			pl.sendMessage(ChatColor.YELLOW+friend.getName()+ChatColor.RED+" has already been refered.");
			return;
		}
		
		friend.sendMessage(ChatColor.YELLOW+pl.getName()+ChatColor.GREEN+" has said they refered you. If this is true please use "+ChatColor.YELLOW+"/raccept "+pl.getName());
	
		pl.sendMessage(ChatColor.YELLOW+friend.getName()+ChatColor.GREEN+" has been sent a confirm message. If they accept you will receive your rewards.");
		
		refers.add(new Refer(pl.getUniqueId(), friend.getUniqueId()));
		
		UUID referer = pl.getUniqueId();
		UUID refered = friend.getUniqueId();
		
		Bukkit.getScheduler().runTaskLater(HubPlugin.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				refers.removeIf(refer -> refer.getRefered().equals(refered) && refer.getReferer().equals(referer));
			}
		}, 20 * 60 * 5);
	}
	
	@Getter
	@AllArgsConstructor
	public static class Refer {
		
		private final UUID referer;
		private final UUID refered;
		
		public UUID getRefered() {
			return refered;
		}
		
		public UUID getReferer() {
			return referer;
		}
		
		@Override
		public int hashCode() {
			return referer.hashCode() * 31 + refered.hashCode();
		}
		
	}

	public static boolean referExists(UUID referer, UUID refered) {
		return refers.removeIf(refer -> refer.getRefered().equals(refered) && refer.getReferer().equals(referer));
	}

}
