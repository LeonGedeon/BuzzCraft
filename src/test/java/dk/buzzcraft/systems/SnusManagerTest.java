package dk.buzzcraft.systems;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure unit tests for the tolerance tier rules. They use the config-free
 * tierFor(int, int, int, int) overload so no Bukkit runtime is required.
 */
class SnusManagerTest {

    private static final int TOLERANT = 30;
    private static final int DEPENDENT = 60;
    private static final int WITHDRAWAL = 90;

    @Test
    void freshBelowTolerantThreshold() {
        assertEquals(SnusManager.Tier.FRESH, SnusManager.tierFor(0, TOLERANT, DEPENDENT, WITHDRAWAL));
        assertEquals(SnusManager.Tier.FRESH, SnusManager.tierFor(29, TOLERANT, DEPENDENT, WITHDRAWAL));
    }

    @Test
    void tolerantAtAndAboveItsThreshold() {
        assertEquals(SnusManager.Tier.TOLERANT, SnusManager.tierFor(30, TOLERANT, DEPENDENT, WITHDRAWAL));
        assertEquals(SnusManager.Tier.TOLERANT, SnusManager.tierFor(59, TOLERANT, DEPENDENT, WITHDRAWAL));
    }

    @Test
    void dependentAtAndAboveItsThreshold() {
        assertEquals(SnusManager.Tier.DEPENDENT, SnusManager.tierFor(60, TOLERANT, DEPENDENT, WITHDRAWAL));
        assertEquals(SnusManager.Tier.DEPENDENT, SnusManager.tierFor(89, TOLERANT, DEPENDENT, WITHDRAWAL));
    }

    @Test
    void withdrawalAtAndAboveItsThreshold() {
        assertEquals(SnusManager.Tier.WITHDRAWAL, SnusManager.tierFor(90, TOLERANT, DEPENDENT, WITHDRAWAL));
        assertEquals(SnusManager.Tier.WITHDRAWAL, SnusManager.tierFor(100, TOLERANT, DEPENDENT, WITHDRAWAL));
    }

    @Test
    void customThresholdsAreRespected() {
        assertEquals(SnusManager.Tier.FRESH, SnusManager.tierFor(9, 10, 20, 40));
        assertEquals(SnusManager.Tier.TOLERANT, SnusManager.tierFor(10, 10, 20, 40));
        assertEquals(SnusManager.Tier.DEPENDENT, SnusManager.tierFor(20, 10, 20, 40));
        assertEquals(SnusManager.Tier.WITHDRAWAL, SnusManager.tierFor(40, 10, 20, 40));
    }

    @Test
    void everyTierHasBuffDescription() {
        for (SnusManager.Tier tier : SnusManager.Tier.values()) {
            assertTrue(SnusManager.describeBuffs(tier).length() > 0);
        }
        assertNotEquals(
                SnusManager.describeBuffs(SnusManager.Tier.FRESH),
                SnusManager.describeBuffs(SnusManager.Tier.WITHDRAWAL));
    }
}
