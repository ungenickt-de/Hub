package com.playerrealms.hub.player;

import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TableTabList;
import com.playerrealms.common.ServerInformation;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.rank.Rank;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.droplet.sql.DatabaseAPI;
import com.playerrealms.droplet.sql.QueryResult;
import com.playerrealms.droplet.util.PunchCardApi;
import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.ProCosmetics;
import com.playerrealms.hub.menu.CrateMenu;
import com.playerrealms.hub.menu.ServerNavigatorMenu;
import me.bowser123467.hikariboard.ScoreboardEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerListener implements Listener
{

	private static Map<UUID, Long> updateTimes = new HashMap<>();
	
	private static int onlineServer = 0;
	private static long lastUpdateTime = 0;
	private Map<UUID, Long> clickTime;
	private Map<UUID,Rank> playerRanks = new HashMap<>();
	
	public PlayerListener() {
		clickTime = new HashMap<>();
	}

	private final Random random = new Random();

	private boolean random() {
		if (random.nextBoolean()) {
			return true;
		} else {
			return false;
		}
	}

	public static Map<Player, Long> Parkour = new HashMap<>();
	public static Map<Player, Location> StartLoc = new HashMap<>();
	public static Map<Player, Location> CheckLoc = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		HubPlayer hp = HubPlugin.getInstance().getPlayerManager().load(player);
		hp.giveHubItems();

		Language.sendMessage(player, "hub.join_message.1");
		Language.sendMessage(player, "hub.join_message.2");

		player.teleport(new Location(Bukkit.getServer().getWorld("world"), -1087, 47, 1130, -179, (float) -0.5));

		/*if(random() == true) {
			player.teleport(new Location(Bukkit.getServer().getWorld("world"), 0.5, 85, 0.5, 353, (float) 12.5));
		}else{
			player.teleport(new Location(Bukkit.getServer().getWorld("world"), 1, 76, 56, 353, (float) -0.5));
		}

		InetSocketAddress address = e.getPlayer().getAddress();
		for(Player other : Bukkit.getOnlinePlayers()) {
			if(e.getPlayer().equals(other)) {
				continue;
			}
			if(other.getAddress().getAddress().equals(address.getAddress())) {
				e.getPlayer().kickPlayer("Multiple logins from same IP address");
			}
		}*/

		if(PunchCardApi.isGoldenWeek()) {
			player.sendTitle("", ChatColor.GOLD+"Golden Week Event", 35, 25, 40);
		}else if(PunchCardApi.isWelcomeBackEvent()){
			player.sendTitle("", ChatColor.GOLD+"Welcome Event", 35, 25, 40);
		}

		if(player != null) {
			TableTabList tab = HubPlugin.getTabbed().newTableTabList(player);

			tab.setBatchEnabled(true);
			PlayerRealmsTabUtil.updateCoins(player);
			PlayerRealmsTabUtil.updateGems(player);
			PlayerRealmsTabUtil.updateServerInfo(player);

			PlayerRealmsTabUtil.updatePlayers(player, PlayerRealmsTabUtil.getDefaultList());

			tab.set(0, 2, new TextTabItem(ChatColor.BOLD + "--------------", 0));
			tab.set(1, 2, new TextTabItem(ChatColor.BOLD + "--------------", 0));
			tab.set(2, 2, new TextTabItem(ChatColor.BOLD + "--------------", 0));
			tab.set(3, 2, new TextTabItem(ChatColor.BOLD + "--------------", 0));

			tab.batchUpdate();
			tab.setBatchEnabled(false);

			PlayerRealmsTabUtil.updateEverybodyForPlayers(player);

			playerRanks.put(player.getUniqueId(), DropletAPI.getRank(player));

					/*List<QueryResult> results = DatabaseAPI.query("SELECT `referral` FROM `players` WHERE `uuid`=?", player.getUniqueId().toString());

					int referral = results.get(0).get("referral");

					Bukkit.getScheduler().runTask(HubPlugin.getInstance(), new Runnable() {

						@Override
						public void run() {
							HubPlugin.getInstance().getPlayerManager().getPlayer(player).setReferred(referral == 1);
						}

					});*/
		}

	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		HubPlugin.getInstance().getPlayerManager().unload(player);
		boolean msg = true;
		if(JedisAPI.keyExists("hiderank.toggle."+player.getUniqueId()) || JedisAPI.keyExists("vanish.toggle."+player.getUniqueId())){
			msg = false;
		}
		if (msg && player.hasPermission("playerrealms.rank.vip"))
		{

			Rank rank = playerRanks.get(player.getUniqueId());

			if(rank == null){
				rank = DropletAPI.getRank(player);
			}

			e.setQuitMessage(rank.getPrefix() + " " + player.getName() + ChatColor.YELLOW + " has left the lobby!");
		}
		else
		{
			e.setQuitMessage(null);
		}
					playerRanks.remove(player.getUniqueId());
					PlayerRealmsTabUtil.updateEverybodyForPlayers(player);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		HubPlayer hp = HubPlugin.getInstance().getPlayerManager().getPlayer(player);
		if (hp != null)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(e.getClickedBlock() != null) {
					if(e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
						CrateMenu cm = new CrateMenu(player);
						cm.open(player);
						e.setCancelled(true);
						return;
					}else if(e.getClickedBlock().getType() == Material.CHEST) {
						e.setCancelled(true);
						return;
					}else if(e.getClickedBlock().getType() == Material.ENDER_CHEST) {
						e.setCancelled(true);
						return;
					}
				}
				if (item != null)
				{
					if(item.getType() == Material.COMPASS){
						new ServerNavigatorMenu(player).open(player);
					}else if(item.getType() == Material.CHEST){
						CrateMenu menu = new CrateMenu(player);
						menu.open(player);
					}else if(item.getType() == Material.FEATHER){
						/*ReferFriendMenu rfm = new ReferFriendMenu();
						rfm.open(player);*/
						Language.sendMessage(player, "generic.twitter");
					}else if(item.getType() == Material.NETHER_STAR){
						ProCosmetics.openMainMenu(player);
					}else if(item.getType() == Material.MAP){
						player.performCommand("golden");
					}
				}
			}
		}

	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		Player player = e.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE)
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e)
	{
		Player player = e.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE)
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE)
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player player = e.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE)
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		Entity dtaker = e.getEntity();
		if(dtaker instanceof Player){
			Player taker = (Player) dtaker;
			if(taker.getLevel() == 1){
				return;
			}
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e)
	{
		e.setFoodLevel(20);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		if (player.getGameMode() != GameMode.CREATIVE)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event){
		Player pl = event.getPlayer();
		
		long lastClick = clickTime.getOrDefault(pl.getUniqueId(), 0L);
		
		if(System.currentTimeMillis() - lastClick < 50){
			return;
		}
		
		clickTime.put(pl.getUniqueId(), System.currentTimeMillis());
		
		if(event.getRightClicked() instanceof Player){
			Player clicked = (Player) event.getRightClicked();
			if(clicked.hasMetadata("NPC")) {
				return;
			}
			List<ServerInformation> servers = DropletAPI.getByOwner(clicked);
			
			if(servers.size() == 0){
				Language.sendMessage(pl, "player_click_hub.no_servers", clicked.getName());
			}else{
				ServerInformation info = servers.get(0);

				Language.sendMessage(pl, "player_click_hub.owns", clicked.getName(), info.getName());
			}
		}
	}

	@EventHandler
	public void onScoreboardUpdate(ScoreboardEvent e)
	{
		Player player = e.getPlayer();

		if(System.currentTimeMillis() - lastUpdateTime > 10000) {
			onlineServer = (int) DropletAPI.getPlayerServers().stream().filter(s -> s.getStatus() == ServerStatus.ONLINE).count();
			lastUpdateTime = System.currentTimeMillis();
		}

		e.setScoreboardName(ChatColor.AQUA.toString() + "Player" + ChatColor.LIGHT_PURPLE + "Islands");

		e.setHeader(ChatColor.YELLOW.toString() + ChatColor.STRIKETHROUGH + "--*----------*--");

		e.writeLine(ChatColor.GREEN.toString() + ChatColor.BOLD + "Players Online");
		e.writeLine(ChatColor.GRAY.toString() + DropletAPI.getOnlinePlayers());
		e.writeLine("");
		e.writeLine(ChatColor.BLUE.toString() + ChatColor.BOLD + "Online Islands");
		e.writeLine(ChatColor.GRAY.toString() + onlineServer);
		e.writeLine("");
		e.writeLine(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Island " +ChatColor.GOLD.toString() + ChatColor.BOLD+ "Coins");
		e.writeLine(ChatColor.GRAY.toString() + DropletAPI.getCoins(player));

		e.setFooter(ChatColor.YELLOW.toString() + ChatColor.STRIKETHROUGH + "--*----------*--");

		if(player != null) {
			Bukkit.getScheduler().runTaskAsynchronously(HubPlugin.getInstance(), new Runnable() {
				@Override
				public void run() {
					long update = updateTimes.getOrDefault(player.getUniqueId(), 0L);

					if (System.currentTimeMillis() - update > 10000) {
						TableTabList tab = (TableTabList) HubPlugin.getTabbed().getTabList(player);
						tab.setBatchEnabled(true);

						boolean changed = false;

						changed |= PlayerRealmsTabUtil.updateCoins(player);
						changed |= PlayerRealmsTabUtil.updateGems(player);
						changed |= PlayerRealmsTabUtil.updateServerInfo(player);
						updateTimes.put(player.getUniqueId(), System.currentTimeMillis());
						if (changed)
							tab.batchUpdate();
						tab.setBatchEnabled(false);
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e)
	{
		Player player = e.getPlayer();
		HubPlayer hp = HubPlugin.getInstance().getPlayerManager().getPlayer(player);

		if (hp != null)
		{
			if (hp.getNextChat() > System.currentTimeMillis() && !player.hasPermission("playerrealms.chat.bypass"))
			{
				long timeLeft = hp.getNextChat() - System.currentTimeMillis();
				long seconds = timeLeft / 1000;
				player.sendMessage(ChatColor.RED + "Please wait " + seconds + " seconds before sending another chat messaage.");
				e.setCancelled(true);
			}
			else
			{
				hp.setNextChat(System.currentTimeMillis() + 3000);
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event){
		if(event.getPlayer().getItemInHand().getType() == null) { return; }
		if(event.getPlayer().getItemInHand().getType() == Material.STICK && event.getPlayer().isOp()) {
			Player player = event.getPlayer();
			for(Entity e : player.getNearbyEntities(3,5,3)){
				e.remove();
			}
		}
	}
}
