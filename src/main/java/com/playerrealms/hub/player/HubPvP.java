package com.playerrealms.hub.player;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.ProCosmetics;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HubPvP implements Listener {
    @EventHandler
    public void onPvPRegionEnter(RegionEnterEvent e)
    {
        if(!e.getRegion().getId().equals("pvp")){
            return;
        }
        Player player = e.getPlayer();
        player.getInventory().clear();
        ItemStack[] armor = new ItemStack[] {
                new ItemStack(Material.IRON_BOOTS, 1),
                new ItemStack(Material.IRON_LEGGINGS, 1),
                new ItemStack(Material.IRON_CHESTPLATE, 1),
                new ItemStack(Material.IRON_HELMET, 1)
        };
        player.getInventory().setArmorContents(armor);
        player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD,1));
        player.getInventory().setItem(1, new ItemStack(Material.GOLDEN_APPLE, 1));
        player.setLevel(1);
        if (player.getGameMode().equals(GameMode.CREATIVE))
        {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onPvPRegionLeave(RegionLeaveEvent e)
        {
        if(!e.getRegion().getId().equals("pvp")){
            return;
        }
        Player player = e.getPlayer();
        ProCosmetics.giveMainmenu(player);
        player.setLevel(0);
        HubPlayer hp = HubPlugin.getInstance().getPlayerManager().load(player);
        hp.giveHubItems();
        player.setHealth(20.0);
    }

}
