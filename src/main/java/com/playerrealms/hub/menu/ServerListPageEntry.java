package com.playerrealms.hub.menu;

import com.nirvana.menu.Item;
import com.nirvana.menu.PacketMenuSlotHandler;
import com.nirvana.menu.menus.PageMenuEntry;
import com.playerrealms.common.ResponseCodes;
import com.playerrealms.common.ServerInformation;
import com.playerrealms.common.ServerStatus;
import com.playerrealms.droplet.DropletAPI;
import com.playerrealms.droplet.lang.Language;
import com.playerrealms.droplet.rank.Rank;
import com.playerrealms.droplet.util.MojangAPI;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerListPageEntry implements PageMenuEntry {

    private static final Map<UUID, Long> lastServerJumpStart = new HashMap<>();

    private String server;
    private ServerListCompareMethod compareMethod;
    private UUID player;

    protected boolean forceEnchant;

    public ServerListPageEntry(String server, ServerListCompareMethod compareMethod, UUID player) {
        this.server = server;
        this.compareMethod = compareMethod;
        this.player = player;
        forceEnchant = false;
    }

    @Override
    public int compareTo(PageMenuEntry o) {
        if (!(o instanceof ServerListPageEntry)) {
            return 0;// Cant sort
        }

        Player pl = Bukkit.getPlayer(player);
        ServerInformation otherInfo = DropletAPI.getServerInfo(((ServerListPageEntry) o).server);
        ServerInformation us = DropletAPI.getServerInfo(server);

        if (DropletAPI.getFeaturedServer() != null) {
            if (us.getName().equals(DropletAPI.getFeaturedServer().getName())) {
                return -1;
            } else if (otherInfo.getName().equals(DropletAPI.getFeaturedServer().getName())) {
                return 1;
            }
        }

        if (!us.getLanguage().equals(Language.getLocale(pl)) && otherInfo.getLanguage().equals(Language.getLocale(pl))) {
            return 1;
        }

        if (compareMethod == ServerListCompareMethod.ByOnline) {
            return Integer.compare(otherInfo.getPlayersOnline(), us.getPlayersOnline());
        } else if (compareMethod == ServerListCompareMethod.ByVotes) {
            return Integer.compare(otherInfo.getVotes(), us.getVotes());
        } else if (compareMethod == ServerListCompareMethod.ByScore) {


            return Double.compare(otherInfo.getScore(), us.getScore());

        }

        return 0;
    }

    private static String getFinalColor(String line) {
        ChatColor color = ChatColor.WHITE;
        boolean bold = false;
        boolean underlined = false;
        boolean italic = false;
        boolean strikethrough = false;

        boolean next = false;

        for (char c : line.toCharArray()) {
            if (next) {
                next = false;
                ChatColor co = ChatColor.getByChar(c);
                if (co == ChatColor.BOLD) {
                    bold = true;
                } else if (co == ChatColor.ITALIC) {
                    italic = true;
                } else if (co == ChatColor.UNDERLINE) {
                    underlined = true;
                } else if (co == ChatColor.RESET) {
                    color = ChatColor.WHITE;
                    bold = false;
                    underlined = false;
                    italic = false;
                    strikethrough = false;
                } else if (co == ChatColor.STRIKETHROUGH) {
                    strikethrough = true;
                } else {
                    color = co;
                    bold = false;
                    underlined = false;
                    italic = false;
                    strikethrough = false;
                }
            }
            if (c == ChatColor.COLOR_CHAR) {
                next = true;
            }
        }

        if (color == null) {
            color = ChatColor.WHITE;
        }

        String co = color.toString();
        if (bold) {
            co += ChatColor.BOLD;
        }
        if (italic) {
            co += ChatColor.ITALIC;
        }
        if (underlined) {
            co += ChatColor.UNDERLINE;
        }
        if (strikethrough) {
            co += ChatColor.STRIKETHROUGH;
        }

        return co;
    }

    protected ServerInformation getServer() {
        return DropletAPI.getServerInfo(server);
    }

    @Override
    public ItemStack getItem() {
        ServerInformation info = DropletAPI.getServerInfo(server);
        List<String> lore = new ArrayList<>();
        boolean featured = false;
        Material material = DropletAPI.getServerIcon(info);

        if (DropletAPI.getFeaturedServer() != null) {
            if (info.getName().equals(DropletAPI.getFeaturedServer().getName())) {
                lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "** " + ChatColor.GOLD.toString() + ChatColor.BOLD + "Featured" + ChatColor.YELLOW.toString() + ChatColor.BOLD + " **");
                lore.add("");
                material = Material.TOTEM;
                featured = true;
            }
        }

        if (info.hasMotd()) {
            String motd = info.getMotd();
            motd = WordUtils.wrap(motd, 30, "\n", true);
            String lastColor = ChatColor.WHITE.toString();
            for (String s : motd.split("\n")) {
                lore.add(lastColor + s);
                lastColor = getFinalColor(s);
            }
            lore.add("");
        }

        Player pl = Bukkit.getPlayer(player);

        if (!info.isThirdParty()) {
            lore.add(Language.getText(pl, "menu_items.page_entry.online", info.getPlayersOnline(), info.getMaxPlayers()));
        } else {
            lore.add(Language.getText(pl, "menu_items.page_entry.online", info.getPlayersOnline(), "*"));
        }
        lore.add(Language.getText(pl, "menu_items.page_entry.votes", info.getVotes()));

        if (info.getOwner() != null) {
            String owner = MojangAPI.getUsername(info.getOwner());
            Rank rank = DropletAPI.getRank(info.getOwner());
            owner = rank.getPlayerName(owner);
            lore.add(Language.getText(pl, "menu_items.page_entry.owner", owner));
        }

        if (!info.getLanguage().equals(Language.getLocale(pl))) {
            lore.add(Language.getText(pl, "menu_items.page_entry.language") + Language.getText(pl, "language." + info.getLanguage()));
        }

        if (info.getCoinMultiplier() > 0D) {
            int multi = (int) (info.getCoinMultiplier() * 100);
            lore.add(Language.getText(pl, "menu_items.page_entry.multi", multi));
        }

        if (info.isWhitelistEnabled()) {
            lore.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Whitelisted");
        }

        if (info.isThirdParty()) {
            lore.add("");
            lore.add(ChatColor.YELLOW + ChatColor.ITALIC.toString() + "Third-Party");
        }

        if (info.isUltraPremium()) {
            featured = true;//Add enchantment
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "[UltraPremium]");
        }

        if (featured || forceEnchant) {
            return new Item(material).setTitle(ChatColor.GREEN + info.getName()).setLore(lore).addEnchantment(Enchantment.DAMAGE_ALL, 1).build();
        }

        return new Item(material).setTitle(ChatColor.GREEN + info.getName()).setLore(lore).build();
    }

    @Override
    public PacketMenuSlotHandler getHandler() {
        return (pl, menu, interaction) -> {
            ServerInformation info = DropletAPI.getServerInfo(server);
            if (info.isBan()) {
                Language.sendMessage(pl, "response_codes.server_banned");
                menu.close();
            }
            if (info.getStatus() == ServerStatus.OFFLINE) {
                boolean allowed = true;
                if (lastServerJumpStart.containsKey(pl.getUniqueId())) {
                    if (System.currentTimeMillis() - lastServerJumpStart.get(pl.getUniqueId()) < TimeUnit.MINUTES.toMillis(1)) {
                        allowed = false;
                    }
                }
                if (allowed) {
                    DropletAPI.startServer(info.getName(), true, code -> {
                        if (code == ResponseCodes.SERVER_STARTING) {
                            DropletAPI.connectToServer(pl, info);
                        } else if (code == ResponseCodes.MEMORY_LIMIT_REACHED) {
                            Language.sendMessage(pl, "response_codes.memory_limit_reached");
                        }
                    });
                    menu.close();
                    lastServerJumpStart.put(pl.getUniqueId(), System.currentTimeMillis());
                } else {
                    DropletAPI.connectToServer(pl, info);
                    menu.close();
                }

            }else if (info.getStatus() == ServerStatus.STOPPING || info.getStatus() == ServerStatus.STARTING){
                menu.close();
            } else {
                DropletAPI.connectToServer(pl, info);
                menu.close();
            }
			/*DropletAPI.connectToServer(pl, info);
			menu.close();*/
        };
    }

    public enum ServerListCompareMethod {
        ByVotes, ByOnline, ByScore
    }

}
