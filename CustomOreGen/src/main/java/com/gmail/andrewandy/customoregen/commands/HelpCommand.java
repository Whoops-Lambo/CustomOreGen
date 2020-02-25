package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import org.bukkit.command.CommandSender;

public class HelpCommand extends NestedCommand {

    private static HelpCommand instance;

    private HelpCommand() {
        super("help");
    }

    public static HelpCommand getInstance() {
        instance = instance == null ? new HelpCommand() : instance;
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        Common.tell(commandSender,
                "&b&lAvailable Commands: ",
                "&e - NearMe --> [Displays the number of generators near you]",
                "&a - Reload --> [Reloads settings.yml]",
                "&c - Help --> [Shows this menu]",
                "&b - Save --> [Saves all data to disk]");
        return true;
    }
}
