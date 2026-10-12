package dk.buzzcraft;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SnusItem {

    private static NamespacedKey key;
    private static NamespacedKey tinKey;

    public static void init(JavaPlugin plugin) {
        key = new NamespacedKey(plugin, "snus");
        tinKey = new NamespacedKey(plugin, "pablo_tin");
    }

    public static ItemStack create(int amount) {
        if (key == null) {
            throw new IllegalStateException("SnusItem.init(plugin) must be called before SnusItem.create(...)");
        }
        ItemStack item = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fSnus");
        meta.setLore(List.of("§7Right-click to use"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSnus(ItemStack item) {
        if (key == null || item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    /**
     * Creates the "Tin of Pablo Mini": a compass-textured container holding
     * {@code amount} tins (normally one). Right-clicking it unpacks Snus again.
     */
    public static ItemStack createTin(int amount) {
        if (tinKey == null) {
            throw new IllegalStateException("SnusItem.init(plugin) must be called before SnusItem.createTin(...)");
        }
        ItemStack item = new ItemStack(Material.COMPASS, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fTin of Pablo Mini");
        meta.setLore(List.of("§7Right-click to unpack"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        // A resource pack can give the compass this custom texture; 0 disables it.
        int customModelData = customModelData();
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        meta.getPersistentDataContainer().set(tinKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isTin(ItemStack item) {
        if (tinKey == null || item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(tinKey, PersistentDataType.BYTE);
    }

    private static int customModelData() {
        BuzzCraft plugin = BuzzCraft.getPlugin();
        return plugin != null ? plugin.getConfig().getInt("tin.customModelData", 1) : 0;
    }

    public static void registerRecipe(JavaPlugin plugin) {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "snus_recipe");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, create(4));
        recipe.shape("TTT", "SPS", "TTT");
        recipe.setIngredient('T', Material.DRIED_KELP);
        recipe.setIngredient('S', Material.SUGAR);
        recipe.setIngredient('P', Material.PAPER);
        if (plugin.getServer().getRecipe(recipeKey) != null) {
            plugin.getServer().removeRecipe(recipeKey);
        }
        plugin.getServer().addRecipe(recipe);
    }
}
