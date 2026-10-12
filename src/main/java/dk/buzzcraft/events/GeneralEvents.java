package dk.buzzcraft.events;

import dk.buzzcraft.BuzzCraft;
import dk.buzzcraft.SnusItem;
import dk.buzzcraft.systems.SnusManager;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GeneralEvents implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataUtility.createPlayerDataFile(player);
    }

    @EventHandler
    private void onPlayerUseSnus(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!player.hasPermission("buzzcraft.use")) {
            return;
        }

        // Unpack a Tin of Pablo Mini back into Snus.
        if (SnusItem.isTin(item)) {
            unpackTin(player, item);
            return;
        }

        if (!SnusItem.isSnus(item)) {
            return;
        }

        // Sneak + right-click a large enough stack to pack it into a Tin.
        int perTin = Math.max(1, BuzzCraft.getPlugin().getConfig().getInt("tin.snusPerTin", 30));
        if (player.isSneaking() && item.getAmount() >= perTin) {
            packTin(player, item, perTin);
            return;
        }

        // Consume one snus from the stack
        item.setAmount(item.getAmount() - 1);

        // Record the use, raise tolerance and apply the tier effects
        SnusManager.useSnus(player);
    }

    /** Compresses {@code perTin} Snus into a single Tin of Pablo Mini. */
    private void packTin(Player player, ItemStack snus, int perTin) {
        snus.setAmount(snus.getAmount() - perTin);
        giveItem(player, SnusItem.createTin(1));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.2f);
        player.sendMessage(ChatColor.GRAY + "You packed " + ChatColor.YELLOW + perTin + ChatColor.GRAY
                + " Snus into a " + ChatColor.AQUA + "Tin of Pablo Mini" + ChatColor.GRAY + ".");
    }

    /** Unpacks a Tin of Pablo Mini back into Snus. */
    private void unpackTin(Player player, ItemStack tin) {
        int perUnpack = Math.max(1, BuzzCraft.getPlugin().getConfig().getInt("tin.snusPerUnpack", 30));
        tin.setAmount(tin.getAmount() - 1);
        giveItem(player, SnusItem.create(perUnpack));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 0.8f);
        player.sendMessage(ChatColor.GRAY + "You unpacked " + ChatColor.YELLOW + perUnpack + ChatColor.GRAY
                + " Snus from the " + ChatColor.AQUA + "Tin of Pablo Mini" + ChatColor.GRAY + ".");
    }

    /** Adds to the inventory, dropping anything that does not fit at the player's feet. */
    private void giveItem(Player player, ItemStack stack) {
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
        for (ItemStack drop : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }
    }
}
