package dk.buzzcraft.systems;

import dk.buzzcraft.BuzzCraft;
import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Periodically reduces every online player's tolerance so that casual Snus
 * use is rewarded and the effects fade over time.
 */
public class ToleranceDecayTask implements Runnable {

    @Override
    public void run() {
        int amount = BuzzCraft.getPlugin().getConfig().getInt("decayAmount", 1);
        if (amount <= 0) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = PlayerDataUtility.loadPlayerData(player);
            if (data.getTolerance() <= 0) {
                continue;
            }
            data.setTolerance(Math.max(0, data.getTolerance() - amount));
            PlayerDataUtility.savePlayerData(player, data);
            SnusManager.Tier tier = SnusManager.tierFor(data.getTolerance());
            if (tier == SnusManager.Tier.WITHDRAWAL) {
                SnusManager.applyEffects(player, tier, data);
            }
        }
    }
}
