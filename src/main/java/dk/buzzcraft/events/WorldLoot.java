package dk.buzzcraft.events;

import dk.buzzcraft.BuzzCraft;
import dk.buzzcraft.SnusItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.List;
import java.util.Random;

/**
 * Lets players find Snus in the world: inside generated loot chests and as a
 * rare drop from wild plants.
 */
public class WorldLoot implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        double chance = BuzzCraft.getPlugin().getConfig().getDouble("lootChestChance", 0.1);
        if (chance <= 0 || random.nextDouble() >= chance) {
            return;
        }
        int amount = Math.max(1, BuzzCraft.getPlugin().getConfig().getInt("lootChestAmount", 1));
        event.getLoot().add(SnusItem.create(amount));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        List<String> materials = BuzzCraft.getPlugin().getConfig().getStringList("wildPlantMaterials");
        if (materials.isEmpty() || !materials.contains(type.name())) {
            return;
        }
        double chance = BuzzCraft.getPlugin().getConfig().getDouble("wildPlantChance", 0.05);
        if (chance <= 0 || random.nextDouble() >= chance) {
            return;
        }
        int amount = Math.max(1, BuzzCraft.getPlugin().getConfig().getInt("wildPlantAmount", 1));
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), SnusItem.create(amount));
    }
}
