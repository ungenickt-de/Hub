package com.playerrealms.hub.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerManager
{
	private Map<UUID, HubPlayer> players = new HashMap<>();

	public HubPlayer getPlayer(Player player)
	{
		return players.get(player.getUniqueId());
	}

	public HubPlayer load(Player player)
	{
		HubPlayer hp = new HubPlayer(player);
		players.put(player.getUniqueId(), hp);
		
		return hp;
	}

	public void unload(Player player)
	{
		players.remove(player.getUniqueId());
	}
}
