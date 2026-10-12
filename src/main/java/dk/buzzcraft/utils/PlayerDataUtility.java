package dk.buzzcraft.utils;

import dk.buzzcraft.data.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataUtility {
    private static File dataFolder;
    private static FileConfiguration settings;

    public static void initialize(File pluginDataFolder, FileConfiguration pluginConfig) {
        settings = pluginConfig;
        dataFolder = new File(pluginDataFolder, "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static File getDataFolder() {
        return dataFolder;
    }

    public static void savePlayerData(Player player, PlayerData data) {
        savePlayerData(player.getUniqueId(), data);
    }

    public static void savePlayerData(UUID uuid, PlayerData data) {
        File playerFile = new File(dataFolder, uuid + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("snusTaken", data.getSnusTaken());
        config.set("tolerance", data.getTolerance());
        config.set("showScoreboard", data.isShowScoreboard());
        if (data.getLastSnusTime() > 0) {
            config.set("lastSnusTime", data.getLastSnusTime());
        } else {
            config.set("lastSnusTime", null);
        }

        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData loadPlayerData(Player player) {
        return loadPlayerData(player.getUniqueId());
    }

    public static PlayerData loadPlayerData(OfflinePlayer player) {
        return loadPlayerData(player.getUniqueId());
    }

    public static PlayerData loadPlayerData(UUID uuid) {
        File playerFile = new File(dataFolder, uuid + ".yml");
        if (!playerFile.exists()) {
            return new PlayerData();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        PlayerData data = new PlayerData();
        data.setSnusTaken(config.getInt("snusTaken"));
        // Migrate the legacy "snusHealth" key to "tolerance" on first load.
        int tolerance = config.isSet("tolerance")
                ? config.getInt("tolerance")
                : config.getInt("snusHealth");
        data.setTolerance(tolerance);
        data.setShowScoreboard(config.getBoolean("showScoreboard"));
        if (config.isSet("lastSnusTime")) {
            data.setLastSnusTime(config.getLong("lastSnusTime"));
        }
        return data;
    }

    /**
     * Loads the data of every player that has ever played on the server.
     * Used by the leaderboard.
     */
    public static Map<UUID, PlayerData> getAllPlayerData() {
        Map<UUID, PlayerData> all = new HashMap<>();
        if (dataFolder == null || !dataFolder.isDirectory()) {
            return all;
        }
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return all;
        }
        for (File file : files) {
            String name = file.getName();
            String uuidPart = name.substring(0, name.length() - ".yml".length());
            try {
                UUID uuid = UUID.fromString(uuidPart);
                all.put(uuid, loadPlayerData(uuid));
            } catch (IllegalArgumentException ignored) {
                // Not a UUID-based data file; skip it.
            }
        }
        return all;
    }

    public static void createPlayerDataFile(Player player) {
        File playerDataFile = new File(dataFolder, player.getUniqueId() + ".yml");
        if (!playerDataFile.exists()) {
            PlayerData playerData = new PlayerData();
            YamlConfiguration config = new YamlConfiguration();
            config.set("snusTaken", playerData.getSnusTaken());
            config.set("tolerance", settings.getInt("startingTolerance"));
            config.set("showScoreboard", settings.getBoolean("showScoreboard"));
            try {
                config.save(playerDataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
