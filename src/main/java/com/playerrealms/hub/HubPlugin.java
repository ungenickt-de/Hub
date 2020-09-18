package com.playerrealms.hub;

import com.keenant.tabbed.Tabbed;
import com.playerrealms.client.ServerUpdateAdapter;
import com.playerrealms.common.ServerInformation;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.hub.commands.*;
import com.playerrealms.hub.management.commands.*;
import com.playerrealms.hub.player.HubPvP;
import com.playerrealms.hub.player.PlayerListener;
import com.playerrealms.hub.player.PlayerManager;
import io.sentry.SentryClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HubPlugin extends JavaPlugin
{

	public static final Set<UUID> flyGame = new HashSet<>();

	@Getter
	private static HubPlugin instance;
	
	@Getter
	public PlayerManager playerManager;

	@Getter
	private static Tabbed tabbed;

	private static SentryClient sentry;

	public void onEnable()
	{
		instance = this;
		tabbed = new Tabbed(this);
		playerManager = new PlayerManager();
		registerCommands();
		registerListeners();
		registerRunnables();

		DropletAPI.addServerUpdateListener(new ServerUpdateAdapter()
		{
			@Override
			public void onServerStatusChange(ServerInformation info, ServerStatus old)
			{
				if (old == ServerStatus.STARTING)
				{
					if (info.getStatus() == ServerStatus.ONLINE)
					{
						UUID uuid = info.getOwner();
						if (uuid != null)
						{
							Player player = Bukkit.getPlayer(uuid);

							if (player != null)
							{
								Language.sendMessage(player, "hub.server_started");
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {
									@Override
									public void run() {
										DropletAPI.connectToServer(player, info);
									}
								}, 100);
							}
						}
					}
				}
				else if (info.getStatus() == ServerStatus.STARTING && old == ServerStatus.OFFLINE)
				{

					UUID uuid = info.getOwner();

					DropletAPI.listenToConsole(info.getName(), (line, contract) -> {
						Player player = Bukkit.getPlayer(uuid);

						if (player == null)
						{
							contract.cancelContract();
							return;
						}
						if(line.contains("region")){
							return;
						}
						if(line.startsWith("Preparing")){
							player.sendMessage(ChatColor.BLUE+line);
						}
						if (line.contains("Done"))
						{
							contract.cancelContract();
							player.sendMessage(ChatColor.GREEN + line.substring(0, line.indexOf('!')));
						}
					});
				}
			}
		});
	}

	public void onDisable()
	{
		sentry.closeConnection();
	}

	public static boolean isGadgetMenuLoaded() {
		return Bukkit.getPluginManager().isPluginEnabled("GadgetMenu");
	}

	public static boolean isProcosmeticsLoaded() {
		return Bukkit.getPluginManager().isPluginEnabled("ProCosmetics");
	}

	public void registerCommands()
	{
		getCommand("buy").setExecutor(new BuyCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("islands").setExecutor(new RealmsCommand());
		getCommand("debug").setExecutor(new DebugCommand());
		getCommand("globalbonus").setExecutor(new GlobalBonusCommand());
		getCommand("setfeatured").setExecutor(new SetFeaturedCommand());
		getCommand("setrank").setExecutor(new SetRankCommand());
		getCommand("perm").setExecutor(new PermissionCommand());
		getCommand("raccept").setExecutor(new ReferAcceptCommand());
		getCommand("thirdparty").setExecutor(new ThirdPartyCommand());
		getCommand("discord").setExecutor(new DiscordCommand());
		getCommand("givecrate").setExecutor(new GiveCrateCommand());
		getCommand("code").setExecutor(new CodeCommand());
		getCommand("golden").setExecutor(new GoldenWeekCommand());
		getCommand("birthday").setExecutor(new BirthdayCommand());
		getCommand("rule").setExecutor(new RuleCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("parkour").setExecutor(new ParkourCommand());
	}

	public void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new HubPvP(), this);
		//getServer().getPluginManager().registerEvents(new HubRuleCheck(), this);
	}

	public void registerRunnables()
	{
		getServer().getScheduler().runTaskTimerAsynchronously(this, new PlayerLocationChecker(), 0, 15);
	}

	public static HubPlugin getInstance() {
		return instance;
	}

	public static Tabbed getTabbed() {return tabbed; }

	public static SentryClient getSentry() { return sentry; }
}
