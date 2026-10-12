package dk.buzzcraft.commands.subcommands;

import dk.buzzcraft.commands.SubCommand;
import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardCommand extends SubCommand {

    private static final int PAGE_SIZE = 10;

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "Show the players who have taken the most Snus";
    }

    @Override
    public String getSyntax() {
        return "/snus leaderboard [page]";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("buzzcraft.leaderboard")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to view the leaderboard.");
            return;
        }

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Math.max(1, Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid page number.");
                return;
            }
        }

        List<Map.Entry<UUID, PlayerData>> entries =
                new ArrayList<>(PlayerDataUtility.getAllPlayerData().entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue().getSnusTaken(), a.getValue().getSnusTaken()));

        int total = entries.size();
        int pages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        page = Math.min(page, pages);
        int from = (page - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);

        player.sendMessage(ChatColor.DARK_GRAY + "-------- " + ChatColor.AQUA + "Snus Leaderboard"
                + ChatColor.GRAY + " (" + page + "/" + pages + ")" + ChatColor.DARK_GRAY + " --------");

        if (total == 0) {
            player.sendMessage(ChatColor.GRAY + "No Snus has been taken yet.");
            return;
        }

        for (int i = from; i < to; i++) {
            Map.Entry<UUID, PlayerData> entry = entries.get(i);
            player.sendMessage(ChatColor.GOLD + "" + (i + 1) + ". " + ChatColor.WHITE + nameOf(entry.getKey())
                    + ChatColor.GRAY + " - " + ChatColor.YELLOW + entry.getValue().getSnusTaken());
        }
    }

    private String nameOf(UUID uuid) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        String name = offline.getName();
        return name != null ? name : uuid.toString();
    }
}
