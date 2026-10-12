package dk.buzzcraft.scoreboard;

import dk.buzzcraft.data.PlayerData;
import dk.buzzcraft.systems.SnusManager;
import dk.buzzcraft.utils.PlayerDataUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import net.md_5.bungee.api.ChatColor;

public class Board implements Runnable {
    private final static Board instance = new Board();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = PlayerDataUtility.loadPlayerData(player);
            if (data.isShowScoreboard()) {
                if (player.getScoreboard() != null && player.getScoreboard().getObjective("BuzzCraft") != null) {
                    updateScoreboard(player);
                } else {
                    createNewScoreboard(player);
                }
            } else if (player.getScoreboard().getObjective("BuzzCraft") != null) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
    }

    private void createNewScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("BuzzCraft", "dummy",
                ChatColor.AQUA + "" + ChatColor.BOLD + "BuzzCraft");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore(ChatColor.WHITE + " ").setScore(6);
        objective.getScore(ChatColor.AQUA + "Buffs").setScore(5);

        // Current buff/debuff summary (dynamic)
        Team buffs = scoreboard.registerNewTeam("buffs");
        String buffsKey = ChatColor.WHITE.toString();
        buffs.addEntry(buffsKey);
        objective.getScore(buffsKey).setScore(4);

        objective.getScore(ChatColor.WHITE + "  ").setScore(3);

        // Snus taken (dynamic)
        Team snusTaken = scoreboard.registerNewTeam("snusTaken");
        String snusTakenKey = ChatColor.GOLD.toString();
        snusTaken.addEntry(snusTakenKey);
        snusTaken.setPrefix("Snus taken: ");
        objective.getScore(snusTakenKey).setScore(2);

        // Tolerance (dynamic)
        Team tolerance = scoreboard.registerNewTeam("tolerance");
        String toleranceKey = ChatColor.GREEN.toString();
        tolerance.addEntry(toleranceKey);
        tolerance.setPrefix("Tolerance: ");
        objective.getScore(toleranceKey).setScore(1);

        player.setScoreboard(scoreboard);
        updateScoreboard(player);
    }

    private void updateScoreboard(Player player) {
        PlayerData data = PlayerDataUtility.loadPlayerData(player);
        Scoreboard scoreboard = player.getScoreboard();
        SnusManager.Tier tier = SnusManager.tierFor(data.getTolerance());

        Team snusTaken = scoreboard.getTeam("snusTaken");
        if (snusTaken != null) {
            snusTaken.setSuffix(ChatColor.YELLOW + "" + data.getSnusTaken());
        }
        Team tolerance = scoreboard.getTeam("tolerance");
        if (tolerance != null) {
            tolerance.setSuffix(ChatColor.LIGHT_PURPLE + "" + data.getTolerance());
        }
        Team buffs = scoreboard.getTeam("buffs");
        if (buffs != null) {
            buffs.setSuffix(SnusManager.describeBuffs(tier));
        }
    }

    public static Board getInstance() {
        return instance;
    }
}
