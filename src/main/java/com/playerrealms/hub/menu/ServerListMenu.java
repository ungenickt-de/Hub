package com.playerrealms.hub.menu;

import com.nirvana.menu.Interaction;
import com.nirvana.menu.Item;
import com.nirvana.menu.Item.SkullType;
import com.nirvana.menu.PacketMenu;
import com.nirvana.menu.PacketMenuSlotHandler;
import com.nirvana.menu.menus.PagedMenu;
import com.playerrealms.common.ServerInformation;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.hub.menu.ServerListPageEntry.ServerListCompareMethod;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ServerListMenu extends PagedMenu
{
	private UUID player;
	private ServerListCompareMethod method;
	private boolean official;

	public ServerListMenu(ServerListCompareMethod sortMethod, Player player, boolean official, boolean ignoreLanguage)
	{
		super((official ? DropletAPI.getOfficalGameServers() : DropletAPI.getPlayerServers()).stream()
				.filter(info -> info.getStatus() == ServerStatus.ONLINE || (info.isUltraPremium() && info.getStatus() == ServerStatus.OFFLINE))
				.filter(info -> ignoreLanguage || info.getLanguage().equals(Language.getLocale(player)))
				.filter(info -> !info.isClosedForDevelopment())
				.map(info -> new ServerListPageEntry(info.getName(), sortMethod, player.getUniqueId()))
				.collect(Collectors.toList()),
				ChatColor.DARK_AQUA + "Server List");
		this.player = player.getUniqueId();
		method = sortMethod;
		this.official = official;
		initialize(player);
	}
	
	@Override
	public void remake(boolean refreshTitle)
	{
		super.remake(refreshTitle);
		initialize(Bukkit.getPlayer(player));
	}

	private void initialize(Player player)
	{
		addItem(5, 6, new Item(Material.ENDER_PEARL).setTitle(Language.getText(player, "menu_items.server_list.random_server.title")).setLore(Language.getText(player, "menu_items.server_list.random_server.lore")).build(), new PacketMenuSlotHandler()
		{
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo)
			{
				List<ServerInformation> possible = DropletAPI.getPlayerServers().stream().filter(s -> s.getStatus() == ServerStatus.ONLINE).collect(Collectors.toList());

				if (possible.size() == 0)
				{
					Language.sendMessage(player, "server_command.no_servers");
					menu.close();
					return;
				}

				Random r = new Random();

				DropletAPI.connectToServer(player, possible.get(r.nextInt(possible.size())));
			}
		});

		if(method == ServerListCompareMethod.ByOnline) {
			addItem(9, 1, new Item(Material.EYE_OF_ENDER).setTitle(Language.getText(player, "menu_items.server_list.sort.online")).setLore(Language.getText(player, "menu_items.server_list.sort.online_lore")).build(), new PacketMenuSlotHandler() {
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					menu.close();
					player.performCommand("islands votes");
				}
			});
		}else if(method == ServerListCompareMethod.ByVotes) {
			addItem(9, 1, new Item(Material.EYE_OF_ENDER).setTitle(Language.getText(player, "menu_items.server_list.sort.vote")).setLore(Language.getText(player, "menu_items.server_list.sort.vote_lore")).build(), new PacketMenuSlotHandler() {
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					menu.close();
					player.performCommand("islands players");
				}
			});
		}
		
		String of = JedisAPI.getCachedValue("official_featured", TimeUnit.SECONDS.toMillis(60));
		String redirect = JedisAPI.getCachedValue("official_featured_redirect", TimeUnit.SECONDS.toMillis(60));
		
		if(of != null && of.equals("dvz")) {

			ServerInformation dvz = DropletAPI.getServerInfo(redirect);
			addItem(4, new Item(Material.SKULL_ITEM).setSkullType(SkullType.ZOMBIE).setTitle(Language.getText(player, "featured_servers.dvz.title")).setLore(Language.getText(player, "featured_servers.dvz.lore.1"), Language.getText(player, "featured_servers.dvz.lore.2"), Language.getText(player, "featured_servers.dvz.lore.3"), "", Language.getText(player, "featured_servers.online", dvz.getPlayersOnline())).build(), new PacketMenuSlotHandler() {
				
				@Override
				public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
					
					DropletAPI.connectToServer(player, dvz);
					
				}
			});
		}else if(of != null && of.startsWith("player")) {
			
			String[] args = of.split(" ");
			
			if(args.length > 1) {
				String name = args[1];
				
				ServerInformation info = DropletAPI.getServerInfo(name);
				
				if(info != null) {
					
					ServerListPageEntry entry = new ServerListPageEntry(name, ServerListCompareMethod.ByOnline, player.getUniqueId());
					
					entry.forceEnchant = true;
					
					addItem(4, entry.getItem(), entry.getHandler());
					
				}
			}
			
		}
		

		addItem(0, new Item(Material.ENDER_CHEST).setTitle(Language.getText(player, "menu_items.server_list.show_all")).build(), new PacketMenuSlotHandler()
		{
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo)
			{
				ServerListMenu newMenu = new ServerListMenu(method, player, official, true);

				newMenu.open(player);
			}
		});
	}
}
