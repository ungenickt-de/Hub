package com.playerrealms.hub;

import org.bukkit.entity.Player;
import sv.file14.procosmetics.api.API;

public class ProCosmetics {
    public static boolean openMainMenu(Player p){
        if(p == null){
            return false;
        }
        API.openMainMenu(p);
        return true;
    }

    public static boolean giveMainmenu(Player p){
        if(p == null){
            return false;
        }
        API.giveCosmeticMenu(p);
        return true;
    }

    public static boolean setCoins(Player p, int coin){
        if(p == null || coin <= 0){
            return false;
        }
        API.setCoins(p, coin);
        return true;
    }

    public static boolean addCoins(Player p, int coin){
        if(p == null || coin <= 0){
            return false;
        }
        int old = API.getCoins(p);
        int rep = old + coin;
        API.setCoins(p, rep);
        return true;
    }

    public static int getCoins(Player p) {
        if(p == null){
            return -1;
        }
        return API.getCoins(p);
    }

    public static boolean setTreasure(Player p, String Treasure, int num) {
        if(Treasure.isEmpty() || Treasure == null || p == null || num <= 0){
            return false;
        }
        if(Treasure.equals("normal")){
            API.setNormalTreasures(p, num);
        }else if(Treasure.equals("mythical")){
            API.setMythicalTreasures(p, num);
        }else if(Treasure.equals("legendary")){
            API.setLegendaryTreasures(p, num);
        }else{
            return false;
        }
        return true;
    }

    public static boolean addTreasure(Player p, String Treasure, int num) {
        if(Treasure.isEmpty() || Treasure == null || p == null || num <= 0){
            return false;
        }
        if(Treasure.equals("normal")){
            int old = API.getNormalTreasures(p);
            int rep = old + num;
            API.setNormalTreasures(p, rep);
        }else if(Treasure.equals("mythical")){
            int old = API.getMythicalTreasures(p);
            int rep = old + num;
            API.setMythicalTreasures(p, rep);
        }else if(Treasure.equals("legendary")){
            int old = API.getLegendaryTreasures(p);
            int rep = old + num;
            API.setLegendaryTreasures(p, rep);
        }else{
            return false;
        }
        return true;
    }

    public static int getTreasure(Player p, String Treasure) {
        if(Treasure.isEmpty() || Treasure == null || p == null){
            return -1;
        }
        int result = -1;
        if(Treasure.equals("normal")){
            result = API.getNormalTreasures(p);
        }else if(Treasure.equals("mythical")){
            result = API.getMythicalTreasures(p);
        }else if(Treasure.equals("legendary")){
            result = API.getLegendaryTreasures(p);
        }else{
            return -1;
        }
        return result;
    }
}
