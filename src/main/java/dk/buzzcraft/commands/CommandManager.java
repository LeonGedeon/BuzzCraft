package dk.buzzcraft.commands;

import dk.buzzcraft.commands.subcommands.LeaderboardCommand;
import dk.buzzcraft.commands.subcommands.ScoreboardCommand;
import dk.buzzcraft.commands.subcommands.StatsCommand;
import dk.buzzcraft.commands.subcommands.ResetToleranceCommand;
import dk.buzzcraft.commands.subcommands.GiveCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager() {
        subcommands.add(new StatsCommand());
        subcommands.add(new LeaderboardCommand());
        subcommands.add(new ScoreboardCommand());
        subcommands.add(new ResetToleranceCommand());
        subcommands.add(new GiveCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length > 0) {
            for (SubCommand sub : getSubcommands()) {
                if (args[0].equalsIgnoreCase(sub.getName())) {
                    sub.perform(p, args);
                    return true;
                }
            }
            p.sendMessage(ChatColor.RED + "Unknown subcommand '" + args[0] + "'. Use /snus for help.");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
            for (SubCommand sub : getSubcommands()) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + sub.getSyntax() + ChatColor.GRAY + " - " + sub.getDescription());
            }
            p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (SubCommand sub : subcommands) {
                names.add(sub.getName());
            }
            return filter(names, args[0]);
        }

        if (args.length >= 2) {
            SubCommand sub = getSubcommand(args[0]);
            if (sub != null) {
                return filter(sub.tabComplete(player, args), args[args.length - 1]);
            }
        }

        return Collections.emptyList();
    }

    private SubCommand getSubcommand(String name) {
        for (SubCommand sub : subcommands) {
            if (sub.getName().equalsIgnoreCase(name)) {
                return sub;
            }
        }
        return null;
    }

    private List<String> filter(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lower)) {
                result.add(option);
            }
        }
        return result;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

}
