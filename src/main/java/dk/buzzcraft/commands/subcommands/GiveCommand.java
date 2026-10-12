package dk.buzzcraft.commands.subcommands;

import dk.buzzcraft.SnusItem;
import dk.buzzcraft.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GiveCommand extends SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give Snus or Tin of Pablo Mini";
    }

    @Override
    public String getSyntax() {
        return "/snus give <player> <snus|tin> [amount]";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("buzzcraft.give")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to give items.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: " + getSyntax());
            return;
        }
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player '" + args[1] + "' is not online.");
            return;
        }
        String type = args[2].toLowerCase(Locale.ROOT);
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Math.max(1, Integer.parseInt(args[3]));
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "'" + args[3] + "' is not a valid amount.");
                return;
            }
        }
        ItemStack stack;
        if (type.equals("snus")) {
            stack = SnusItem.create(amount);
        } else if (type.equals("tin")) {
            stack = SnusItem.createTin(amount);
        } else {
            player.sendMessage(ChatColor.RED + "Unknown item: " + args[2] + ". Use 'snus' or 'tin'.");
            return;
        }
        Map<Integer, ItemStack> leftover = targetPlayer.getInventory().addItem(stack);
        for (ItemStack drop : leftover.values()) {
            targetPlayer.getWorld().dropItemNaturally(targetPlayer.getLocation(), drop);
        }
        player.sendMessage(ChatColor.GRAY + "Gave " + ChatColor.YELLOW + amount + ChatColor.GRAY
                + " " + (type.equals("tin") ? "Tin of Pablo Mini" : "Snus") + ChatColor.GRAY
                + " to " + ChatColor.WHITE + targetPlayer.getName() + ChatColor.GRAY + ".");
        targetPlayer.sendMessage(ChatColor.GRAY + "You received " + ChatColor.YELLOW + amount + ChatColor.GRAY
                + " " + (type.equals("tin") ? "Tin of Pablo Mini" : "Snus") + ChatColor.GRAY + ".");
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                names.add(online.getName());
            }
            return names;
        }
        if (args.length == 3) {
            List<String> types = new ArrayList<>();
            types.add("snus");
            types.add("tin");
            return types;
        }
        return super.tabComplete(player, args);
    }
}
