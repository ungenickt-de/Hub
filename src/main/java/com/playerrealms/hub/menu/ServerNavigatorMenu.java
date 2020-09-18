package com.playerrealms.hub.menu;

import com.nirvana.menu.*;
import com.playerrealms.common.ServerInformation;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.crates.CrateAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.menu.hub.ServerManagerMenu;
import com.playerrealms.droplet.menu.hub.ServerMenu;
import com.playerrealms.droplet.sql.DatabaseAPI;
import com.playerrealms.droplet.sql.QueryResult;
import com.playerrealms.hub.menu.ServerListPageEntry.ServerListCompareMethod;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerNavigatorMenu extends ChestPacketMenu
{
	public ServerNavigatorMenu(Player player)
	{
		super(27, "PlayerIslands Navigator");
		init(player);
	}

	public void init(Player player)
	{
		this.addItem(3, 2, new Item(Material.GOLD_NUGGET).setTitle(Language.getText(player, "hub.popular_servers.title")).setLore(Language.getText(player, "hub.popular_servers.lore.1"), Language.getText(player, "hub.popular_servers.lore.2")).build(), new PacketMenuSlotHandler()
		{
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interaction)
			{
				ServerListCompareMethod sortMethod = ServerListCompareMethod.ByOnline;
				new ServerListMenu(sortMethod, player, false, false).open(player);
			}
		});

		this.addItem(5, 2, new Item(Material.CHEST).setTitle(Language.getText(player, "hub.your_server.title")).setLore(Language.getText(player, "hub.your_server.lore")).build(), new PacketMenuSlotHandler()
		{
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interaction)
			{
				List<ServerInformation> ownedServers = DropletAPI.getByOwner(player);
				
				if(ownedServers.size() == 1){
					ServerMenu sm = new ServerMenu(ownedServers.get(0), player);
					
					sm.open(player);
				}else{
					ServerManagerMenu smm = new ServerManagerMenu(player);
					
					smm.open(player);
				}
				
			}
		});

		this.addItem(7, 2, new Item(Material.DIAMOND).setTitle(Language.getText(player, "hub.redeem_code.title")).setLore(Language.getText(player, "hub.redeem_code.lore.1"), Language.getText(player, "hub.redeem_code.lore.2")).build(), new PacketMenuSlotHandler()
		{
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interaction)
			{
				AnvilPacketMenu anvil = new AnvilPacketMenu();
				
				anvil.setDefaultText("Code");
				anvil.setResult(new ItemStack(Material.DIAMOND));
				
				anvil.setClickSound(Sound.ENTITY_ITEMFRAME_ADD_ITEM);
				
				anvil.setHandler(new AnvilPacketMenuHandler() {
					
					@Override
					public void onResult(String text, Player pl) {
						
						try {
							List<QueryResult> results = DatabaseAPI.query("SELECT * FROM `promo` WHERE `code`=?", text);
							
							if(results.size() == 0) {
								Language.sendMessage(pl, "promo_code.unknown", text);
							}else {
								
								int id = results.get(0).get("id");
								int max = results.get(0).get("max_uses");
								int expireMinutes = results.get(0).get("expire_time");
								Timestamp timestamp = results.get(0).get("creation_time");
								String prize = results.get(0).get("prize");
								String prize_name = results.get(0).get("prize_name");
								
								if(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(expireMinutes) >= timestamp.getTime()) {
									Language.sendMessage(pl, "promo_code.expired");
								}else {

									List<QueryResult> users = DatabaseAPI.query("SELECT * FROM `promo_uses` WHERE `code`=?", id);
									
									if(users.size() < max || max < 0) {
										
										boolean hasRedeemed = false;
										
										for(QueryResult user : users) {
											if(user.get("uuid").equals(player.getUniqueId().toString())) {
												hasRedeemed = true;
												break;
											}
										}
										
										if(!hasRedeemed) {
											DatabaseAPI.execute("INSERT INTO `promo_uses` (`uuid`,`code`) VALUES (?, ?)", player.getUniqueId().toString(), id);
											CrateAPI.giveCrate(player.getUniqueId(), prize);
											
											Language.sendMessage(pl, "promo_code.success", prize_name);
										}else {
											Language.sendMessage(pl, "promo_code.already_used");
										}
										
										
									}else {
										Language.sendMessage(pl, "promo_code.over_used");
									}
									
								}
								
							}
						} catch (SQLException e) {
							e.printStackTrace();
							Language.sendMessage(pl, "promo_code.error");
						}
						
					}
				});
				
				anvil.open(player);
			}
		});
	}
}
