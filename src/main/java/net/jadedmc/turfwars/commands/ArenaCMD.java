package net.jadedmc.turfwars.commands;

import net.jadedmc.turfwars.TurfWars;
import net.jadedmc.turfwars.game.arena.Arena;
import net.jadedmc.turfwars.game.arena.ArenaBuilder;
import net.jadedmc.turfwars.utils.chat.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class ArenaCMD implements CommandExecutor {
    private final TurfWars plugin;

    public ArenaCMD(TurfWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!sender.hasPermission("turfwars.admin")) {
            return true;
        }

        if(!(sender instanceof Player player)) {
            return true;
        }

        if(args.length == 0) {
            return true;
        }

        switch (args[0]) {
            case "addspawn" -> addSpawnCMD(player, args);
            case "cancel" -> cancelCMD(player);
            case "create" -> createCMD(player, args);
            case "finish" -> finishCMD(player);
            case "setbounds1" -> setBounds1CMD(player, args);
            case "setbounds2" -> setBounds2CMD(player, args);
            case "setname" -> setNameCMD(player, args);
            case "setwaitingarea" -> setWaitingAreaCMD(player);
        }

        return true;
    }

    private void addSpawnCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! <white>/arena create<red>.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena addspawn [team]");
            return;
        }

        String team = args[1].toLowerCase();

        if(team.equals("team1")) {
            plugin.getArenaManager().getArenaBuilder().addTeam1Spawn(player.getLocation());

            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Added spawn point to <white>team 1<green>.");
            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Add as many as you want, then set the first boundary with <white>/arena setbounds1 team1<green>.");
        }

        if(team.equals("team2")) {
            plugin.getArenaManager().getArenaBuilder().addTeam2Spawn(player.getLocation());

            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Added spawn point to <white>team 2<green>.");
            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Add as many as you want, then set the first boundary with <white>/arena setbounds1 team2<green>.");
        }
    }

    /**
     * Runs the /arena cancel command.
     * This command cancels the arena creation process.
     * @param player Player running the command.
     */
    private void cancelCMD(Player player) {
        // Makes sure there is an arena builder present.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>There is no arena currently being set up.");
        }

        plugin.getArenaManager().setArenaBuilder(null);
        ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>You have canceled the arena setup.");
    }

    /**
     * Runs the /arena create command.
     * This command starts the arena creation process.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void createCMD(Player player, String[] args) {
        // Makes sure there is no arena builder already being set up.
        if(plugin.getArenaManager().getArenaBuilder() != null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>There is already an arena being set up.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena create [id]");
            return;
        }

        // Gets the arena id.
        String id = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");

        // Makes sure the id isn't already in use.
        for(Arena arena : plugin.getArenaManager().getArenas()) {
            if(arena.getId().equalsIgnoreCase(id)) {
                ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>There is already an arena with that id.");
                return;
            }
        }

        // Starts the arena setup process.
        plugin.getArenaManager().setArenaBuilder(new ArenaBuilder(plugin, id));
        ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Created an arena with the id <white>" + id + "<green>.");
        ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Next, set the arena name with <white>/arena setname [name]<green>.");
    }

    /**
     * Runs the /arena finish command.
     * This command checks if the arena is done and saves it if so.
     * @param player Player running the command.
     */
    private void finishCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! <white>/arena create<red>.");
            return;
        }

        // Saves the arena.
        ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Arena has been saved.");
        plugin.getArenaManager().getArenaBuilder().save();
    }

    private void setBounds1CMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! <white>/arena create<red>.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena setbounds1 [team]");
            return;
        }

        String team = args[1].toLowerCase();

        if(team.equals("team1")) {
            plugin.getArenaManager().getArenaBuilder().setTeam1Bounds1(player.getTargetBlock((Set<Material>) null, 5));

            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Set Boundary 1 for <white>team 1<green>.");
            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Now, set the second boundary with <white>/arena setbounds2 team1<green>.");
        }

        if(team.equals("team2")) {
            plugin.getArenaManager().getArenaBuilder().setTeam2Bounds1(player.getTargetBlock((Set<Material>) null, 5));

            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Set Boundary 1 for <white>team 2<green>.");
            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Now, set the second boundary with <white>/arena setbounds2 team2<green>.");
        }
    }

    private void setBounds2CMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! <white>/arena create<red>.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena setbounds2 [team]");
            return;
        }

        String team = args[1].toLowerCase();

        if(team.equals("team1")) {
            plugin.getArenaManager().getArenaBuilder().setTeam1Bounds2(player.getTargetBlock((Set<Material>) null, 5));

            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Set Boundary 2 for <white>team 1<green>.");
            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Now, add the team 2 spawn points with <white>/arena addspawn team2<green>.");
        }

        if(team.equals("team2")) {
            plugin.getArenaManager().getArenaBuilder().setTeam2Bounds2(player.getTargetBlock((Set<Material>) null, 5));

            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Set Boundary 1 for <white>team 2<green>.");
            ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Now, set the waiting area spawn with <white>/arena setwaitingarea<green>.");
        }
    }

    /**
     * Runs the /arena setname command.
     * This command sets the name of the arena.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setNameCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! <white>/arena create<red>.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena setname [name]");
            return;
        }

        // Gets the arena name.
        String name = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");

        // Sets the arena name.
        plugin.getArenaManager().getArenaBuilder().setName(name);
        ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Arena name set to <white>" + name + "<green>.");
        ChatUtils.chat(player, "<green><bold>TurfWars</bold> <dark_gray>» <green>Next, set the team1 spawns with <white>/arena addspawn team1<green>.");
    }

    /**
     * Runs the /arena setwaitingarea command.
     * This command sets the waiting area spawn for the new arena.
     * @param player Player running the command.
     */
    private void setWaitingAreaCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! <white>/arena create<red>.");
            return;
        }

        // Sets the waiting area spawn.
        plugin.getArenaManager().getArenaBuilder().setWaitingArea(player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aYou have set the waiting area spawn to your location.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aFinally, finish the setup with <white>/arena finish<green>.");
    }
}
