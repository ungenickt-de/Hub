package com.playerrealms.hub.player;

import com.keenant.tabbed.item.PlayerTabItem;
import com.keenant.tabbed.item.PlayerTabItem.PlayerProvider;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TableTabList;
import com.keenant.tabbed.tablist.TableTabList.TableCell;
import com.keenant.tabbed.util.Skins;
import com.playerrealms.common.ServerInformation;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.rank.Rank;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.hub.HubPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerRealmsTabUtil {
	private static final PlayerProvider<String> PLAYER_NAME_PROVIDER = new PlayerProvider<String>() {
		
		@Override
		public String get(Player pl) {
			boolean tab = true;
			if(JedisAPI.keyExists("hiderank.toggle."+pl.getUniqueId())){
				tab = false;
				JedisAPI.cacheKey("hiderank.toggle."+pl.getUniqueId(), 60000);
			}
			Rank rank = DropletAPI.getRank(pl.getUniqueId());
			String name;
			if(tab){
				rank.getPrefix();
				name = rank.getPlayerName(pl.getName());
			}else{
			    String old = rank.getPrefix();
				rank.setPrefix("&7");
				name = rank.getPlayerName(pl.getName());
				rank.setPrefix2(old);
			}
			return name;
		}
	};

	private static boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished")) {
			if (meta.asBoolean()) return true;
		}
		return false;
	}

	public static List<Player> getDefaultList(){
		List<Player> vlist = new ArrayList<>();
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(isVanished(pl)){
				vlist.add(pl);
			}
		}
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		players.removeAll(vlist);

		Collections.sort(players, new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				Rank rank1 = DropletAPI.getRank(o1);
				Rank rank2 = DropletAPI.getRank(o2);
				return Integer.compare(rank1.getTabPriority(), rank2.getTabPriority());
			}
		});
		
		return players;
	}

	public static boolean updateServerInfo(Player player) {
		TableTabList tab = (TableTabList) HubPlugin.getTabbed().getTabList(player);
		List<ServerInformation> infos = DropletAPI.getByOwner(player.getUniqueId());
		
		boolean changed = false;
		
		if(infos.size() > 0) {
			changed |= set(tab, 3, 0, new TextTabItem(ChatColor.YELLOW.toString()+ChatColor.BOLD+"Your Server", 0));
			
			ServerInformation info = infos.get(0);
			
			if(info.getStatus() == ServerStatus.ONLINE) {
				changed |= set(tab, 3, 1, new TextTabItem(ChatColor.GREEN+info.getName()+" ("+info.getPlayersOnline()+"/"+ChatColor.GRAY+info.getMaxPlayers()+ChatColor.GREEN+")", 0, Skins.getDot(org.bukkit.ChatColor.GREEN)));
			}else{
				changed |= set(tab, 3, 1, new TextTabItem(ChatColor.RED+info.getName(), 0, Skins.getDot(org.bukkit.ChatColor.RED)));
			}
		}
		return changed;
	}
	
	public static boolean updateGems(Player player) {
		TableTabList tab = (TableTabList) HubPlugin.getTabbed().getTabList(player);
		boolean changed = false;
		changed |= set(tab, 2, 0, new TextTabItem(ChatColor.AQUA.toString()+ChatColor.BOLD+"Gems", 0, Skins.getDot(org.bukkit.ChatColor.AQUA)));
		changed |= set(tab, 2, 1, new TextTabItem(ChatColor.GRAY.toString()+DropletAPI.getGems(player), 0, Skins.getDot(org.bukkit.ChatColor.AQUA)));
		return changed;
	}
	
	public static boolean updateCoins(Player player) {
		TableTabList tab = (TableTabList) HubPlugin.getTabbed().getTabList(player);

		boolean changed = false;
		
		changed |= set(tab, 1, 0, new TextTabItem(ChatColor.GOLD.toString()+ChatColor.BOLD+"Coins", 0, Skins.getDot(org.bukkit.ChatColor.GOLD)));
		changed |= set(tab, 1, 1, new TextTabItem(ChatColor.GRAY.toString()+DropletAPI.getCoins(player), 0, Skins.getDot(org.bukkit.ChatColor.GOLD)));
	
		return changed;
	}

	public static void updateEverybodyForPlayers(Player exclude) {
		List<Player> data = getDefaultList();
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(exclude != null) {
				if(exclude.getUniqueId().equals(pl.getUniqueId())) {
					continue;
				}
			}
			TableTabList tab = (TableTabList) HubPlugin.getTabbed().getTabList(pl);
			tab.setBatchEnabled(true);
			updatePlayers(pl, data);
			tab.batchUpdate();
			tab.setBatchEnabled(false);
		}
	}
	
	public static void updatePlayers(Player player, List<Player> players) {
		TableTabList tab = (TableTabList) HubPlugin.getTabbed().getTabList(player);
		
		set(tab, 0, 0, new TextTabItem(ChatColor.BOLD+"Profile", 0, Skins.getDot(org.bukkit.ChatColor.WHITE)));
		set(tab, 0, 1, new PlayerTabItem(player, PLAYER_NAME_PROVIDER));
		
		for(int j = 0; j < 17;j++) {
			for(int i = 0; i < 4;i++) {
				
				if(players.isEmpty()) {
					break;
				}
				
				PlayerTabItem newItem = new PlayerTabItem(players.remove(0), PLAYER_NAME_PROVIDER);
				
				set(tab, i, j + 3, newItem);

			}
		}
		
	}
	
	private static boolean set(TableTabList tab, int x, int y, TabItem newItem) {
		TabItem item = tab.get(new TableCell(x, y));
		if(item == null || !item.getText().equals(newItem.getText())) {
			tab.set(x, y, newItem);	
			return true;
		}
		return false;
	}

}
