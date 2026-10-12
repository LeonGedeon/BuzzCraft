package dk.buzzcraft.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dk.buzzcraft.data.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDataUtilityTest {

    @TempDir
    Path tempDir;

    private ServerMock server;

    @BeforeEach
    void setUp() {
        // MockBukkit is used here purely as a server mock (no plugin loading,
        // which is broken in the v1.20 line on JDK 17 — see MockBukkit#1628).
        server = MockBukkit.mock();
        server.addSimpleWorld("world");

        YamlConfiguration config = new YamlConfiguration();
        config.set("startingTolerance", 0);
        config.set("showScoreboard", false);
        PlayerDataUtility.initialize(tempDir.toFile(), config);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void saveAndLoadRoundTrip() {
        UUID id = UUID.randomUUID();
        PlayerData data = new PlayerData();
        data.setSnusTaken(7);
        data.setTolerance(42);
        data.setShowScoreboard(true);
        PlayerDataUtility.savePlayerData(id, data);

        PlayerData loaded = PlayerDataUtility.loadPlayerData(id);
        assertEquals(7, loaded.getSnusTaken());
        assertEquals(42, loaded.getTolerance());
        assertTrue(loaded.isShowScoreboard());
    }

    @Test
    void missingPlayerReturnsDefaults() {
        PlayerData loaded = PlayerDataUtility.loadPlayerData(UUID.randomUUID());
        assertEquals(0, loaded.getSnusTaken());
        assertEquals(0, loaded.getTolerance());
        assertFalse(loaded.isShowScoreboard());
    }

    @Test
    void legacySnusHealthKeyMigratesToTolerance() throws IOException {
        UUID id = UUID.randomUUID();
        File file = new File(PlayerDataUtility.getDataFolder(), id + ".yml");
        file.getParentFile().mkdirs();

        YamlConfiguration legacy = new YamlConfiguration();
        legacy.set("snusHealth", 42);
        legacy.save(file);

        assertEquals(42, PlayerDataUtility.loadPlayerData(id).getTolerance());
    }

    @Test
    void getAllPlayerDataIncludesSavedPlayers() {
        UUID id = UUID.randomUUID();
        PlayerData data = new PlayerData();
        data.setSnusTaken(3);
        PlayerDataUtility.savePlayerData(id, data);

        assertTrue(PlayerDataUtility.getAllPlayerData().containsKey(id));
    }

    @Test
    void createPlayerDataFileWritesInitialValues() {
        Player player = server.addPlayer("Alice");
        PlayerDataUtility.createPlayerDataFile(player);

        File file = new File(tempDir.toFile(),
                "playerdata" + File.separator + player.getUniqueId() + ".yml");
        assertTrue(file.exists());
        assertEquals(0, PlayerDataUtility.loadPlayerData(player).getTolerance());
    }
}
