package dk.buzzcraft.commands.subcommands;

import dk.buzzcraft.commands.SubCommand;
import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.systems.SnusManager;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsCommand extends SubCommand {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Show Snus stats for yourself or another player";
    }

    @Override
    public String getSyntax() {
        return "/snus stats [player]";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length >= 2) {
            if (!player.hasPermission("buzzcraft.stats.others")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to view other players' stats.");
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.isOnline() && !target.hasPlayedBefore()) {
                player.sendMessage(ChatColor.RED + "No player data found for '" + args[1] + "'.");
                return;
            }
            String name = target.getName() != null ? target.getName() : args[1];
            printStats(player, name, PlayerDataUtility.loadPlayerData(target));
        } else {
            if (!player.hasPermission("buzzcraft.stats")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to view Snus stats.");
                return;
            }
            printStats(player, player.getName(), PlayerDataUtility.loadPlayerData(player));
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        List<String> names = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            names.add(online.getName());
        }
        return names;
    }

    private void printStats(Player viewer, String name, PlayerData data) {
        SnusManager.Tier tier = SnusManager.tierFor(data.getTolerance());
        viewer.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
        viewer.sendMessage(ChatColor.AQUA + "Snus stats for " + ChatColor.WHITE + name);
        viewer.sendMessage(ChatColor.GRAY + "Snus taken: " + ChatColor.YELLOW + data.getSnusTaken());
        viewer.sendMessage(ChatColor.GRAY + "Tolerance: " + ChatColor.LIGHT_PURPLE + data.getTolerance()
                + ChatColor.GRAY + " (" + tier.getColor() + tier.getDisplayName() + ChatColor.GRAY + ")");
        viewer.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
    }
}
