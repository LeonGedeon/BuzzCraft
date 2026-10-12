package dk.buzzcraft.systems;

import dk.buzzcraft.BuzzCraft;
import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Single source of truth for Snus effects: resolves the tolerance tier and
 * applies the matching buffs/debuffs.
 */
public final class SnusManager {

    public enum Tier {
        FRESH("Fresh", ChatColor.GREEN),
        TOLERANT("Tolerant", ChatColor.YELLOW),
        DEPENDENT("Dependent", ChatColor.GOLD),
        WITHDRAWAL("Withdrawal", ChatColor.RED);

        private final String displayName;
        private final ChatColor color;

        Tier(String displayName, ChatColor color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ChatColor getColor() {
            return color;
        }
    }

    private SnusManager() {
    }

    /** Pure tier resolution, independent of config state — kept separate for testing. */
    static Tier tierFor(int tolerance, int tolerant, int dependent, int withdrawal) {
        if (tolerance >= withdrawal) {
            return Tier.WITHDRAWAL;
        }
        if (tolerance >= dependent) {
            return Tier.DEPENDENT;
        }
        if (tolerance >= tolerant) {
            return Tier.TOLERANT;
        }
        return Tier.FRESH;
    }

    /** Resolves the tolerance tier for a given tolerance level using the configured thresholds. */
    public static Tier tierFor(int tolerance) {
        FileConfiguration cfg = BuzzCraft.getPlugin().getConfig();
        return tierFor(tolerance,
                cfg.getInt("tierThresholds.tolerant", 30),
                cfg.getInt("tierThresholds.dependent", 60),
                cfg.getInt("tierThresholds.withdrawal", 90));
    }

    /** Short, sidebar-friendly description of a tier's effect. */
    public static String describeBuffs(Tier tier) {
        return switch (tier) {
            case FRESH -> ChatColor.GREEN + "Strength II";
            case TOLERANT -> ChatColor.YELLOW + "Strength I";
            case DEPENDENT -> ChatColor.GOLD + "Strength I + Slowness";
            case WITHDRAWAL -> ChatColor.RED + "Weak + Slowness + Nausea";
        };
    }

    /**
     * Applies the full effect of using one Snus: records the use, raises
     * tolerance (capped) and applies the tier's buffs/debuffs.
     */
    public static void useSnus(Player player) {
        FileConfiguration cfg = BuzzCraft.getPlugin().getConfig();

        PlayerData data = PlayerDataUtility.loadPlayerData(player);
        data.incrementSnusTaken();

        int max = cfg.getInt("maxTolerance", 100);
        int perUse = cfg.getInt("tolerancePerUse", 15);
        data.setTolerance(Math.min(data.getTolerance() + perUse, max));
        data.setLastSnusTime(System.currentTimeMillis());
        PlayerDataUtility.savePlayerData(player, data);

        Tier tier = tierFor(data.getTolerance());
        applyEffects(player, tier, data);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
        player.sendMessage(tier.getColor() + "You used snus! " + ChatColor.GRAY + "Tolerance: "
                + ChatColor.WHITE + data.getTolerance() + ChatColor.GRAY + " (" + tier.getColor()
                + tier.getDisplayName() + ChatColor.GRAY + ")");
    }

    /** Applies the buffs and debuffs associated with a tier. */
    public static void applyEffects(Player player, Tier tier) {
        applyEffects(player, tier, PlayerDataUtility.loadPlayerData(player));
    }

    /** Applies the buffs and debuffs associated with a tier with player data context. */
    public static void applyEffects(Player player, Tier tier, PlayerData data) {
        FileConfiguration cfg = BuzzCraft.getPlugin().getConfig();
        int baseHeal = cfg.getInt("healAmount", 10);

        switch (tier) {
            case FRESH -> {
                heal(player, baseHeal);
                addEffect(player, PotionEffectType.INCREASE_DAMAGE, 30, 1);
            }
            case TOLERANT -> {
                heal(player, Math.max(1, baseHeal / 2));
                addEffect(player, PotionEffectType.INCREASE_DAMAGE, 15, 0);
            }
            case DEPENDENT -> {
                heal(player, Math.max(1, baseHeal / 4));
                addEffect(player, PotionEffectType.INCREASE_DAMAGE, 15, 0);
                addEffect(player, PotionEffectType.SLOW, 20, 0);
            }
            case WITHDRAWAL -> {
                addEffect(player, PotionEffectType.WEAKNESS, 30, 0);
                addEffect(player, PotionEffectType.SLOW, 30, 0);
                addEffect(player, PotionEffectType.CONFUSION, 30, 0);
            }
        }
    }

    private static void heal(Player player, int amount) {
        player.setHealth(Math.min(player.getHealth() + amount, player.getMaxHealth()));
    }

    private static void addEffect(Player player, PotionEffectType type, int seconds, int amplifier) {
        // Duration in ticks; no ambient particles for a cleaner look.
        player.addPotionEffect(new PotionEffect(type, 20 * seconds, amplifier, false, false));
    }
}
