package com.playerrealms.hub.player;

import com.nirvana.menu.Item;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.redis.JedisAPI;
import com.playerrealms.droplet.util.PunchCardApi;
import com.playerrealms.hub.goldenweek.GoldenWeekMapRenderer;
import com.playerrealms.hub.goldenweek.WelcomeBackMapRenderer;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import static com.playerrealms.hub.HubPlugin.getSentry;

public class HubPlayer
{
	
	private static short adMapId = -1;
	
	@Getter private UUID uuid;
	@Getter private String name;
	@Getter @Setter private long nextChat;

	@Getter @Setter private boolean referred;
	
	public HubPlayer(Player player)
	{
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		referred = true;//Default to true
		
	}

	public Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}

	@SuppressWarnings("deprecation")
	public void giveHubItems()
	{
		Player player = getPlayer();
		player.getInventory().clear();

		player.getInventory().setItem(0, new Item(Material.COMPASS).setTitle(Language.getText(player, "hub_items.compass")).build());
		player.getInventory().setItem(3, new Item(Material.NETHER_STAR).setTitle(Language.getText(player, "hub_items.netherstar")).build());
		player.getInventory().setItem(4, new Item(Material.CHEST).setTitle(Language.getText(player, "hub_items.chest")).build());
		player.getInventory().setItem(8, new Item(Material.FEATHER).setTitle(Language.getText(player, "hub_items.ingot")).build());
	
		if(PunchCardApi.isGoldenWeek()) {
			try {
				PunchCardApi.givePunchCard(player, 1, new GoldenWeekMapRenderer(), ChatColor.GOLD+"Golden Week Point Card");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(PunchCardApi.isWelcomeBackEvent()){
			try{
				PunchCardApi.givePunchCard(player, 1, new WelcomeBackMapRenderer(), ChatColor.AQUA+"Okaeri Card");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		if(JedisAPI.keyExists("hub.ad.image")) {
			String adTitle = JedisAPI.getCachedValue("hub.ad.title", 60000);
			adTitle = ChatColor.translateAlternateColorCodes('&', adTitle);
			if(adMapId == -1) {
				MapView view = Bukkit.createMap(player.getWorld());
				adMapId = view.getId();
				view.getRenderers().clear();
				view.addRenderer(new MapRenderer(false) {
					@Override
					public void render(MapView map, MapCanvas canvas, Player player) {
						String ad = JedisAPI.getCachedValue("hub.ad.image", 60000);
						if(ad == null) {
							player.getInventory().remove(Material.MAP);
							return;
						}
						byte[] data = Base64.getDecoder().decode(ad);
						ByteArrayInputStream bis = new ByteArrayInputStream(data);
						BufferedImage image;
						try {
							image = ImageIO.read(bis);
							canvas.drawImage(0, 0, image);
						} catch (IOException e) {
							e.printStackTrace();
							getSentry().sendException(e);
						}
					}
				});
			}
			player.getInventory().setItemInOffHand(new Item(Material.MAP).setTitle(adTitle).setData(adMapId).build());
		}
	}
}
