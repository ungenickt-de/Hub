package com.playerrealms.hub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerLocationChecker implements Runnable
{

	public void run()
	{
		for (Player pl : Bukkit.getOnlinePlayers())
		{
			if (pl.getLocation().getY() < 1)
			{
				pl.teleport(new Location(pl.getWorld(), -1087, 47, 1130, -179, (float) -0.5));
			}
		}
	}

}
