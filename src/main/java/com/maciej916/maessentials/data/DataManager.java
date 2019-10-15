package com.maciej916.maessentials.data;

import com.maciej916.maessentials.classes.Homes;
import com.maciej916.maessentials.classes.Location;
import com.maciej916.maessentials.config.Config;
import com.maciej916.maessentials.libs.Json;
import com.maciej916.maessentials.libs.Log;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {

    // Mod

    private static ModData modData = new ModData();

    public static void setModData(ModData data) {
        modData = data;
    }

    public static ModData getModData() {
        return modData;
    }

    public static void saveModData() {
        Log.debug("Saving mod data");
        Json.save(modData, "data");
    }

    // Players

    private static HashMap<UUID, PlayerData> playerData = new HashMap<>();

    public static void cleanPlayerData() {
        playerData.clear();
    }

    public static void setPlayerData(UUID uuid, PlayerData data) {
        playerData.put(uuid, data);
    }

    public static PlayerData getPlayerData(ServerPlayerEntity player) {
        UUID playerUUID = player.getUniqueID();
        return getPlayerData(playerUUID);
    }

    public static PlayerData getPlayerData(UUID playerUUID) {
        if (playerData.containsKey(playerUUID)) {
            return playerData.get(playerUUID);
        } else {
            PlayerData essentialPlayer = new PlayerData();
            essentialPlayer.setPlayerUUID(playerUUID);
            Log.debug("Creating player profile: " + playerUUID);
            playerData.put(playerUUID, essentialPlayer);
            savePlayerData(playerUUID, essentialPlayer);
            return essentialPlayer;
        }
    }

    public static void savePlayerData(UUID playerUUID, PlayerData essentialPlayer) {
        Log.debug("Saving player data for player: " + playerUUID);
        Json.save(essentialPlayer, "players/" + playerUUID);
    }

    public static void savePlayerHome(UUID uuid, Homes homes) {
        Log.debug("Saving homes for player: " + uuid);
        Json.save(homes, "homes/" + uuid);
    }

    // WarpData

    private static WarpData warpData = new WarpData();

    public static void cleanWarpData() {
        warpData.cleanWarps();
    }

    public static WarpData getWarpData() {
        return warpData;
    }

    public static void saveWarp(String name, Location location) {
        Log.debug("Saving warp: " + name);
        Json.save(location, "warps/" + name);
    }

    public static void removeWarp(String name) {
        Log.debug("Removing warp: " + name);
        File file = new File(Config.getMainCatalog()+"warps/" + name + ".json");
        file.delete();
    }
}