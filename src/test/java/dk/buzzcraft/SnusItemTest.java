package dk.buzzcraft;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the persistent-data tags that tell Snus and the Tin of Pablo Mini
 * apart, using MockBukkit so no real server is required.
 */
class SnusItemTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        SnusItem.init(MockBukkit.createMockPlugin());
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void tinIsRecognisedAndUsesCompassMaterial() {
        ItemStack tin = SnusItem.createTin(1);

        assertEquals(Material.COMPASS, tin.getType());
        assertTrue(SnusItem.isTin(tin));
        assertFalse(SnusItem.isSnus(tin));
    }

    @Test
    void snusIsNotMistakenForATin() {
        ItemStack snus = SnusItem.create(1);

        assertTrue(SnusItem.isSnus(snus));
        assertFalse(SnusItem.isTin(snus));
    }

    @Test
    void plainCompassIsNotATin() {
        assertFalse(SnusItem.isTin(new ItemStack(Material.COMPASS)));
    }
}
