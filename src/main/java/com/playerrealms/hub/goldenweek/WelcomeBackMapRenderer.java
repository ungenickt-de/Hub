package com.playerrealms.hub.goldenweek;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import com.playerrealms.droplet.util.PunchCardApi;
import com.playerrealms.droplet.util.PunchCardApi.PunchDays;
import com.playerrealms.hub.HubPlugin;

public class WelcomeBackMapRenderer extends MapRenderer {

	private static Image punchCard, check;
	
	public WelcomeBackMapRenderer() throws IOException {
		if(punchCard == null || check == null) {
			punchCard = ImageIO.read(HubPlugin.getInstance().getResource("welcomeback.png"));
			check = ImageIO.read(HubPlugin.getInstance().getResource("check.png"));
		}
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if(punchCard == null || check == null) {
			canvas.drawText(0, 0, MinecraftFont.Font, "Error");
			return;
		}
		
		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = image.getGraphics();
		
		g.drawImage(punchCard, 0, 0, null);
		
		try {
			for(PunchDays days : PunchCardApi.getPunchedDays(player, true)) {
				if(days == PunchDays.WELCOME_BACK_ONE) {
					g.drawImage(check, 13, 54, null);
				}else if(days == PunchDays.WELCOME_BACK_TWO) {
					g.drawImage(check, 48, 54, null);
				}else if(days == PunchDays.WELCOME_BACK_THREE){
					g.drawImage(check, 94, 54, null);
				}else if(days == PunchDays.WELCOME_BACK_FOUR){
					g.drawImage(check, 13, 96, null);
				}else if(days == PunchDays.WELCOME_BACK_FIVE){
					g.drawImage(check, 53, 96, null);
				}else if(days == PunchDays.WELCOME_BACK_SIX){
					g.drawImage(check, 97, 96, null);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		canvas.drawImage(0, 0, image);
		
	}

}
