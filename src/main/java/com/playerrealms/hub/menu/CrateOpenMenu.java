package com.playerrealms.hub.menu;

import com.nirvana.menu.*;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.crates.Crate;
import com.playerrealms.droplet.crates.GlobalCrateTypes;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.hub.HubPlugin;
import com.playerrealms.hub.ProCosmetics;
import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.utils.mysteryboxes.MysteryBoxType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateOpenMenu extends ChestPacketMenu {

	private GlobalCrateTypes type;
	
	public CrateOpenMenu(Crate crate, Player player) {
		super(9 * 6, crate.getName(player));
		
		if(crate.isPlayerServerCrate()) {
			throw new IllegalArgumentException("Not implemented");
		}
		
		int wonAmount = 0;
		
		Random r = new Random();
		
		if(crate.isGlobalCrate()) {
			type = GlobalCrateTypes.getByCrate(crate);
			
			int findBoxChance = 100;
			int maxBoxes = 1;
			int minBoxes = 0;
			int findCouponChance = 10000;
			
			int foundGems = 0;
			
			List<Object> possible = new ArrayList<Object>();
			
			if(type == GlobalCrateTypes.COMMON || type == GlobalCrateTypes.GEM_BOX_SMALL) {
				wonAmount = 35 + r.nextInt(51);
				findBoxChance = 40;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
				}
				
				findCouponChance = 500;
				if(type == GlobalCrateTypes.GEM_BOX_SMALL) {
					foundGems = r.nextInt(5) + 1;
					wonAmount += 35;
					findBoxChance = 20;
					if(HubPlugin.isGadgetMenuLoaded()) {
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					}else if(HubPlugin.isProcosmeticsLoaded()){
						possible.add("normal");
					}
				}
			}else if(type == GlobalCrateTypes.STARTER) {
				wonAmount = 500;
				findBoxChance = 1;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
				}
				findCouponChance = Integer.MAX_VALUE;//Dont give it here
			}else if(type == GlobalCrateTypes.RARE || type == GlobalCrateTypes.ANKETO || type == GlobalCrateTypes.GEM_BOX_MEDIUM) {
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
				findCouponChance = 250;
				if(type == GlobalCrateTypes.GEM_BOX_MEDIUM) {
					foundGems = r.nextInt(5) + 5;
					wonAmount += 55;
					findBoxChance = 10;
					if(HubPlugin.isGadgetMenuLoaded()) {
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					}else if(HubPlugin.isProcosmeticsLoaded()){
						possible.add("mythical");
					}
				}
			}else if(type == GlobalCrateTypes.LEGENDARY || type == GlobalCrateTypes.GEM_BOX_LARGE) {
				wonAmount = 300 + r.nextInt(401);
				findBoxChance = 15;
				maxBoxes = 5;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("mythical");
					possible.add("legendary");
				}
				findCouponChance = 25;
				
				if(type == GlobalCrateTypes.GEM_BOX_LARGE) {
					foundGems = r.nextInt(5) + 6;
					wonAmount += 100;
					findBoxChance = 5;
					if(HubPlugin.isGadgetMenuLoaded()) {
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
					}else if(HubPlugin.isProcosmeticsLoaded()){
						possible.add("legendary");
					}
				}
			}else if(type == GlobalCrateTypes.GIFT || type == GlobalCrateTypes.EVENT) {
				int min = Integer.valueOf(crate.getData(2, "0"));
				int rValue = Integer.valueOf(crate.getData(3, "100"));
				findBoxChance = Integer.valueOf(crate.getData(4, "1000"));
				maxBoxes = Integer.valueOf(crate.getData(5, "1"));
				findCouponChance = Integer.valueOf(crate.getData(6, "10000"));

				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("mythical");
					possible.add("legendary");
				}
				wonAmount = min + r.nextInt(rValue);
			}else if(type == GlobalCrateTypes.DONATION) {
				
				maxBoxes = 0;
				
				minBoxes = Integer.valueOf(crate.getData(2, "1"));
				
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("normal");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("legendary");
				}
				wonAmount = maxBoxes;
				
				findBoxChance = 1;
				
				findCouponChance = Integer.MAX_VALUE;
				
				foundGems = new Random().nextInt(5) + 1;
				
			}else if(type == GlobalCrateTypes.VOTE) {
				
				findBoxChance = 1;
				maxBoxes = 1;
				minBoxes = 1;
				wonAmount = 125;

				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("legendary");
					possible.add("legendary");
					possible.add("legendary");
				}
				
				findCouponChance = 200;
			}else if(type == GlobalCrateTypes.KINENN_2018_COMMON) {
				
				wonAmount = (35 + r.nextInt(51)) * 2;
				findBoxChance = 20;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("normal");
				}
				findCouponChance = 250;
				
			}else if(type == GlobalCrateTypes.KINENN_2018_RARE) {
				
				wonAmount = (65 + r.nextInt(75)) * 2;
				findBoxChance = 10;
				maxBoxes = 2;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("mythical");
				}
				findCouponChance = 125;
				
			}else if(type == GlobalCrateTypes.KINENN_2018_LEGENDARY) {
				wonAmount = (400 + r.nextInt(601)) * 2;
				findBoxChance = 5;
				maxBoxes = 5;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("mythical");
					possible.add("legendary");
				}
				findCouponChance = 20;
			}else if(type == GlobalCrateTypes.BIRTHDAY){
				wonAmount = (400 + r.nextInt(601)) * 2;
				findBoxChance = 5;
				maxBoxes = 5;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("mythical");
					possible.add("legendary");
				}
				findCouponChance = 1;
			}else if(type == GlobalCrateTypes.GOLDEN_WEEK || type == GlobalCrateTypes.WELCOME_BACK) {
				wonAmount = 500 + r.nextInt(601);
				findBoxChance = 3;
				maxBoxes = 5;
				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("normal");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("legendary");
				}
				findCouponChance = 50;
			}else if(type == GlobalCrateTypes.CM_REWARD) {
				findBoxChance = 10;
				maxBoxes = 1;
				minBoxes = 1;
				wonAmount = 100;

				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_1);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_2);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("normal");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("mythical");
				}
				findCouponChance = 1000000;
			}else if(type == GlobalCrateTypes.SUMMER_SMALL) {
				findBoxChance = 1;
				maxBoxes = 1;
				wonAmount = 50 + r.nextInt(51);


				if (HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				} else if (HubPlugin.isProcosmeticsLoaded()) {
					possible.add("normal");
					possible.add("mythical");
					possible.add("legendary");
				}
			}else if(type == GlobalCrateTypes.SUMMER_MEDIUM){
					findBoxChance = 1;
					maxBoxes = 1;
					minBoxes = 1;
					wonAmount = 250 + r.nextInt(51);

					if(HubPlugin.isGadgetMenuLoaded()) {
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
						possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
					}else if(HubPlugin.isProcosmeticsLoaded()){
						possible.add("normal");
						possible.add("mythical");
						possible.add("mythical");
						possible.add("legendary");
					}
			}else if(type == GlobalCrateTypes.SUMMER_BIG){
				findBoxChance = 2;
				maxBoxes = 2;
				minBoxes = 2;
				wonAmount = 500;

				if(HubPlugin.isGadgetMenuLoaded()) {
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_3);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_4);
					possible.add(MysteryBoxType.NORMAL_MYSTERY_BOX_5);
				}else if(HubPlugin.isProcosmeticsLoaded()){
					possible.add("normal");
					possible.add("normal");
					possible.add("mythical");
					possible.add("mythical");
					possible.add("legendary");
				}
			}
			
			while(wonAmount > 0) {
				int amount = r.nextInt(Math.min(wonAmount, 266304));//266304 = 64 + (64^2) + (64^3)
				amount = Math.max(amount, 4);
				wonAmount -= amount;
				addCoins(r.nextInt(getSize()), amount);
			}
			
			if(HubPlugin.isGadgetMenuLoaded() || HubPlugin.isProcosmeticsLoaded()) {
				int crates = r.nextInt(maxBoxes + 1) + minBoxes;
				for(int i = 0; i < crates;i++) {
					if(r.nextInt(findBoxChance) == 0)
						addCrate(r.nextInt(getSize()), possible, player);
				}
			}

			boolean foundCoupon = r.nextInt(findCouponChance) == 0;
			
			/*if(foundCoupon) {
				
				int cType = r.nextInt(2);
				
				addItem(r.nextInt(getSize()), new Item(Material.PAPER).addEnchantment(Enchantment.ARROW_FIRE, 1).setTitle(Language.getText(player, "crates.coupon")).build(), new PacketMenuSlotHandler() {
					
					@Override
					public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
						
						try {

							((ChestPacketMenu)menu).addItem(interactionInfo.getSlot(), new ItemStack(Material.AIR), null);
							
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
							
							boolean empty = true;
							for(ItemStack item : menu.getItems()) {
								if(item != null && item.getType() != Material.AIR) {
									empty = false;
								}
							}
							if(empty) {
								CrateMenu cm = new CrateMenu(player);
								cm.open(player);
							}
							
							String cpnName = "WB-";
							cpnName += player.getName().substring(0, Math.min(4, player.getName().length()));
							cpnName += "-";
							cpnName += r.nextInt(10);
							cpnName += r.nextInt(10);
							cpnName += r.nextInt(10);
							cpnName += "-";
							
							if(cType == 0) {
								cpnName += "PRO";
							}else if(cType == 1){
								cpnName += "LEG";
							}
							
							int off = cType == 0 ? 30 : 20;
							
							player.sendMessage(ChatColor.GOLD.toString()+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----------------------");
							player.sendMessage("");
							player.sendMessage("        "+Language.getText(player, "crates.coupon")+ChatColor.GOLD+ChatColor.BOLD+" "+off+"% OFF!");
							player.sendMessage("        "+Language.getText(player, "crates.code", cpnName));
							player.sendMessage("        "+Language.getText(player, "crates.spend_at"));
							player.sendMessage("");
							player.sendMessage(ChatColor.GOLD.toString()+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----------------------");
							
							
							BuycraftAPI.createCoupon(cpnName, BuycraftAPI.PACKAGE,
									new int[] { cType == 0 ? BuycraftAPI.VIP_PACKAGE : BuycraftAPI.MVP_PACKAGE }, 
									off, BuycraftAPI.EXPIRE_LIMIT, 1, "2018-01-24", player.getName(), player.getName()+"'s won coupon");
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				});
			}*/
			
			if(foundGems > 0) {
				int gems = foundGems;
				addItem(r.nextInt(getSize()), new Item(Material.EMERALD).setTitle(ChatColor.AQUA.toString()+ChatColor.BOLD+foundGems+" Gem").build(), new PacketMenuSlotHandler() {
					
					@Override
					public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
						DropletAPI.createGemTransaction(player.getUniqueId(), player.getName(), gems, "Found in crate");
						JedisAPI.setKey("gems."+player.getUniqueId(), String.valueOf(DropletAPI.getGems(player)));
						ChestPacketMenu cpm = (ChestPacketMenu) menu;
						cpm.addItem(interactionInfo.getSlot(), new ItemStack(Material.AIR), null);
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
						
						boolean empty = true;
						for(ItemStack item : cpm.getItems()) {
							if(item != null && item.getType() != Material.AIR) {
								empty = false;
							}
						}
						if(empty) {
							CrateMenu cm = new CrateMenu(player);
							cm.open(player);
						}
					}
				});
				
			}
			
		}else {
			throw new IllegalArgumentException("Not implemented 2");
		}
	}
	
	@Override
	public void open(Player pl) {
		super.open(pl);
		pl.playSound(pl.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
		if(type == GlobalCrateTypes.KINENN_2018_COMMON || type == GlobalCrateTypes.KINENN_2018_RARE || type == GlobalCrateTypes.KINENN_2018_LEGENDARY) {
			pl.getWorld().spawnParticle(Particle.TOTEM, pl.getLocation(), 30);
		}else{
			pl.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, pl.getLocation(), 2);
		}
	}
	
	private void addCrate(int slot, List<Object> possible, Player player) {
		Random r = new Random();
		addItem(slot, new Item(Material.ENDER_CHEST).setTitle(Language.getText(player, "crates.random_crate")).build(), new PacketMenuSlotHandler() {
			
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
				if(HubPlugin.isGadgetMenuLoaded()) {
					MysteryBoxType type = (MysteryBoxType) possible.get(r.nextInt(possible.size()));
					GadgetsMenuAPI.getPlayerManager(player).giveMysteryBoxes(type, null, false, Language.getText(player, "crates.random_crate"), 1);
				}else if(HubPlugin.isProcosmeticsLoaded()) {
					ProCosmetics.addTreasure(player, (String) possible.get(r.nextInt(possible.size())), 1);
				}

				ChestPacketMenu cpm = (ChestPacketMenu) menu;
				cpm.addItem(interactionInfo.getSlot(), new ItemStack(Material.AIR), null);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
				boolean empty = true;
				for(ItemStack item : cpm.getItems()) {
					if(item != null && item.getType() != Material.AIR) {
						empty = false;
					}
				}
				if(empty) {
					CrateMenu cm = new CrateMenu(player);
					cm.open(player);
				}
			}
		});
	}
	
	private void addCoins(int slot, int amount) {
		Material m = Material.GOLD_NUGGET;
		int displayAmount = amount;
		if(displayAmount > 64) {
			displayAmount /= 64;
			m = Material.GOLD_INGOT;
		
			if(displayAmount > 64) {
				displayAmount /= 64;
				m = Material.GOLD_BLOCK;
			}
		}
		displayAmount = Math.min(displayAmount, 64);//Safety
		addItem(slot, new Item(m).setTitle(ChatColor.GRAY.toString()+amount+" "+ChatColor.AQUA+"Island"+ChatColor.GOLD+"Coins").setAmount(displayAmount).build(), new PacketMenuSlotHandler() {
			
			@Override
			public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
				DropletAPI.setCoins(player, DropletAPI.getCoins(player) + amount);
				ChestPacketMenu cpm = (ChestPacketMenu) menu;
				cpm.addItem(interactionInfo.getSlot(), new ItemStack(Material.AIR), null);
				
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
				
				boolean empty = true;
				for(ItemStack item : cpm.getItems()) {
					if(item != null && item.getType() != Material.AIR) {
						empty = false;
					}
				}
				if(empty) {
					CrateMenu cm = new CrateMenu(player);
					cm.open(player);
				}
			}
			
		});
	}
	
	
}
