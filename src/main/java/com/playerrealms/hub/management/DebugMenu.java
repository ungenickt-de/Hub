package com.playerrealms.hub.management;

import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.nirvana.menu.Interaction;
import com.nirvana.menu.Item;
import com.nirvana.menu.PacketMenu;
import com.nirvana.menu.PacketMenuSlotHandler;
import com.nirvana.menu.menus.PagedMenu;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;
import net.md_5.bungee.api.ChatColor;

public class DebugMenu extends PagedMenu
{
	public DebugMenu(Player player)
	{
		super(DropletAPI.getPlayerServers().stream().filter(server -> server.getStatus() == ServerStatus.ONLINE || server.getStatus() == ServerStatus.STARTING || server.getStatus() == ServerStatus.STOPPING).map(server -> new DebugServerEntry(server.getName(), player.getUniqueId())).collect(Collectors.toList()), "Debug Server Menu");
		initialize();
	}

	@Override
	public void remake(boolean refreshTitle)
	{
		super.remake(refreshTitle);
		initialize();
	}

	private void initialize()
	{
		addItem(0, new Item(Material.TORCH).setTitle(ChatColor.YELLOW + "Refresh Ranks (Global)").build(), new PacketMenuSlotHandler()
		{

			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo)
			{
				DropletAPI.reloadRanks();
			}
		});

		addItem(1, new Item(Material.PAPER).setTitle(ChatColor.YELLOW + "Refresh List (Global)").build(), new PacketMenuSlotHandler()
		{

			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo)
			{
				DropletAPI.reloadLists();
			}
		});
	}
}
