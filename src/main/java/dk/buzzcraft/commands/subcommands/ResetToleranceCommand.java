package dk.buzzcraft.commands.subcommands;

import dk.buzzcraft.commands.SubCommand;
import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResetToleranceCommand extends SubCommand {

    @Override
    public String getName() {
        return "resettolerance";
    }

    @Override
    public String getDescription() {
        return "Reset tolerance for a player";
    }

    @Override
    public String getSyntax() {
        return "/snus resettolerance [player]";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("buzzcraft.resettolerance")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to reset tolerance.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: " + getSyntax());
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(ChatColor.RED + "No player data found for '" + args[1] + "'.");
            return;
        }
        String name = target.getName() != null ? target.getName() : args[1];
        PlayerData data = PlayerDataUtility.loadPlayerData(target);
        data.setTolerance(0);
        data.setLastSnusTime(0);
        PlayerDataUtility.savePlayerData(target.getUniqueId(), data);
        player.sendMessage(ChatColor.GRAY + "Tolerance reset for " + ChatColor.WHITE + name + ChatColor.GRAY + ".");
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
}
