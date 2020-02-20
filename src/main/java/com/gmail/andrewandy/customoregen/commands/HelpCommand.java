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
                "&a - nearme [Displays the number of generators near you]",
                "&a - reload [Reloads settings.yml]",
                "&3 - help [Shows this menu]",
                "&e - save [Saves all data to disk]");
        return true;
    }
}
