package dk.buzzcraft.commands.subcommands;

import dk.buzzcraft.commands.SubCommand;
import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ScoreboardCommand extends SubCommand {
    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public String getDescription() {
        return "Toggle scoreboard";
    }

    @Override
    public String getSyntax() {
        return "/snus scoreboard";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("buzzcraft.scoreboard")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to toggle the scoreboard.");
            return;
        }
        PlayerData data = PlayerDataUtility.loadPlayerData(player);
        data.setShowScoreboard(!data.isShowScoreboard());
        PlayerDataUtility.savePlayerData(player, data);
        player.sendMessage(ChatColor.GRAY + "Snus scoreboard "
                + (data.isShowScoreboard() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")
                + ChatColor.GRAY + ".");
    }
}
