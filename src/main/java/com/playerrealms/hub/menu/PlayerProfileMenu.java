package com.playerrealms.hub.menu;

import com.nirvana.menu.ChestPacketMenu;
import com.nirvana.menu.Item;
import com.nirvana.menu.Item.SkullType;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.rank.Rank;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerProfileMenu extends ChestPacketMenu {

	public PlayerProfileMenu(Player observed) {
		super(54, observed.getName()+"'s Profile");
		initialize(observed);
	}
	
	public void initialize(Player who){
		
		for(int i = 0; i < 9;i++){
			for(int j = 0; j < 6;j++){
				if(i == 0 || i == 8){
					if(j == 0 || j == 5){
						addItem(i + 1, j + 1, new Item(Material.STAINED_GLASS_PANE).setTitle("").setData(5).build());
					}
				}
			}
		}
		
		Rank rank = DropletAPI.getRank(who);
		
		ItemStack playerSkull = new Item(Material.SKULL_ITEM).setSkullType(SkullType.PLAYER).setTitle(rank.getPlayerName(who.getName())).build();
		
		/*Skin skin = SkinManagerPlugin.getSkinManager().createSkin(who.getUniqueId());
		
		playerSkull = SkinManagerPlugin.getSkullManager().setPlayerSkull(playerSkull, skin);*/
		addItem(2, 2, playerSkull);
	}

}
