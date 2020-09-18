package com.playerrealms.hub.menu;

import com.nirvana.menu.*;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.crates.Crate;
import com.playerrealms.droplet.crates.CrateAPI;
import com.playerrealms.droplet.crates.GlobalCrateTypes;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.ProCosmetics;
import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.utils.mysteryboxes.MysteryBoxType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateMenu extends ChestPacketMenu {

	private boolean shouldOpen;
	private boolean error;
	
	public CrateMenu(Player player) {
		super(9*5, "Crates");
	
		shouldOpen = true;
		error = false;
		try {
			List<Crate> crates = CrateAPI.getCrates(player.getUniqueId(), getSize()-2);
			if(crates.size() == 0) {
				shouldOpen = false;
				return;
			}
			if(DropletAPI.getRank(player).hasPermission("playerrealms.rank.mvp")) {
				addItem(new Item(Material.CHEST).setTitle(Language.getText(player, "crates.open_once.title", "Common")).build(), new PacketMenuSlotHandler() {
					@Override
					public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
						menu.close();
						CrateMultiOpen(player, GlobalCrateTypes.COMMON);
					}
				});
				addItem(new Item(Material.CHEST).setTitle(Language.getText(player, "crates.open_once.title", "Rare")).build(), new PacketMenuSlotHandler() {
					@Override
					public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
						menu.close();
						CrateMultiOpen(player, GlobalCrateTypes.RARE);
					}
				});
			}
			for(Crate crate : crates) {
				addItem(new Item(crate.getItemType()).setTitle(crate.getName(player)).setLore("", Language.getText(player, "crates.click_to_open")).build(), new PacketMenuSlotHandler() {
					@Override
					public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
						if(crate.openCrate()) {
							menu.close();
							CrateOpenMenu om = new CrateOpenMenu(crate, player);
							om.open(player);
							if(!crate.getSubtype().equals("common")) {
								for(Player other : Bukkit.getOnlinePlayers()) {
									Language.sendMessage(other, "crates.opening", DropletAPI.getRank(player).getPlayerName(player.getName()), crate.getName(other));
								}
							}
						} else {
							Language.sendMessage(player, "response_codes.server_unknown_error");
							menu.close();
						}
						
					}
					
				});
			}
		} catch (SQLException e) {
			e.printStackTrace();
			error = true;
		}
	}
	
	public boolean isShouldOpen() {
		return shouldOpen;
	}
	
	
	@Override
	public void open(Player pl) {
		if(error) {
			Language.sendMessage(pl, "response_codes.server_unknown_error");
			return;
		}
		if(!shouldOpen) {
			Language.sendMessage(pl, "crates.no_crates");
			return;
		}
		super.open(pl);
	}

	public void CrateMultiOpen(Player player, GlobalCrateTypes sel) {
		try {
			List<Crate> crates = CrateAPI.getCrates(player.getUniqueId(), sel.getTypeString());
			int totalCrates = 0;
			int wonCoin = 0;
			int wonBox = 0;
			int wonGem = 0;
			Random r = new Random();

			for (Crate crate : crates) {
				GlobalCrateTypes type = GlobalCrateTypes.getByCrate(crate);
				if (!sel.equals(type)) {
					continue;
				}
				if (!crate.openCrate()){
					Language.sendMessage(player, "response_codes.server_unknown_error");
					continue;
				}

				int wonAmount = 0;
				int findBoxChance = 100;
				int maxBoxes = 1;
				int minBoxes = 0;
				List<Object> possible = new ArrayList<Object>();

				if (type == GlobalCrateTypes.COMMON) {
					wonAmount = 35 + r.nextInt(51);
					findBoxChance = 40;
					if (HubPlugin.isGadgetMenuLoaded()) {
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					} else if (HubPlugin.isProcosmeticsLoaded()) {
						possible.add("normal");
					}
				} else if (type == GlobalCrateTypes.RARE) {
					wonAmount = 65 + r.nextInt(75);
					findBoxChance = 20;
					maxBoxes = 2;
					if(HubPlugin.isGadgetMenuLoaded()) {
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					}else if(HubPlugin.isProcosmeticsLoaded()){
						possible.add("normal");
						possible.add("mythical");
					}
				}

				totalCrates++;
				wonCoin += wonAmount;

				if (HubPlugin.isGadgetMenuLoaded() || HubPlugin.isProcosmeticsLoaded()) {
					int bonus = r.nextInt(maxBoxes + 1) + minBoxes;
					for (int i = 0; i < bonus; i++) {
						if (r.nextInt(findBoxChance) == 0) {
							wonBox++;
							if (HubPlugin.isGadgetMenuLoaded()) {
								MysteryBoxType mtype = (MysteryBoxType) possible.get(r.nextInt(possible.size()));
								GadgetsMenuAPI.getPlayerManager(player).giveMysteryBoxes(mtype, null, false, Language.getText(player, "crates.random_crate"), 1);
							} else if (HubPlugin.isProcosmeticsLoaded()) {
								ProCosmetics.addTreasure(player, (String) possible.get(r.nextInt(possible.size())), 1);
							}
						}
					}
				}
			}

			Language.sendMessage(player, "crates.open_once.result", totalCrates);
			if(wonCoin > 0) {
				Language.sendMessage(player, "crates.open_once.result_coin", wonCoin);
				DropletAPI.setCoins(player, DropletAPI.getCoins(player) + wonCoin);
			}
			if(wonGem > 0) {
				Language.sendMessage(player, "crates.open_once.result_gem", wonGem);
				DropletAPI.createGemTransaction(player.getUniqueId(), player.getName(), wonGem, "Found in crate");
				JedisAPI.setKey("gems."+player.getUniqueId(), String.valueOf(DropletAPI.getGems(player)));
			}
			if(wonBox > 0) {
				Language.sendMessage(player, "crates.open_once.result_box", wonBox);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
}
