package com.playerrealms.hub.management;

import java.util.UUID;

import com.nirvana.menu.PacketMenuSlotHandler;
import com.nirvana.menu.menus.PageMenuEntry;
import com.playerrealms.droplet.menu.hub.ServerMenu;
import com.playerrealms.hub.menu.ServerListPageEntry;

public class DebugServerEntry extends ServerListPageEntry implements PageMenuEntry
{
	public DebugServerEntry(String server, UUID player)
	{
		super(server, ServerListCompareMethod.ByOnline, player);
	}

	@Override
	public PacketMenuSlotHandler getHandler()
	{
		return (pl, menu, interaction) -> {
			ServerMenu serverMenu = new ServerMenu(getServer(), pl, true);
			serverMenu.open(pl);
		};
	}
}
