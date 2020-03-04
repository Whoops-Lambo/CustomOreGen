package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends NestedCommand {

    private static HelpCommand instance;

    private HelpCommand() {
        super("&aHelp");
        setDescription("&eShows this page.");
    }

    public static HelpCommand getInstance() {
        instance = instance == null ? new HelpCommand() : instance;
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        List<NestedCommand> commands = BaseCommand.getInstance().getSortedNestedCommands(commandSender);
        String[] messages = new String[commands.size() + 1];
        int index = 0;
        messages[index++] = "&b&lAvailable Commands: ";
        for (NestedCommand cmd : commands) {
            String message = cmd.getLabel() + " --> " + (cmd.hasDescription() ? "[" + cmd.getDescription() + "]" : "");
            messages[index++] = message;
        }
        Common.tell(commandSender, messages);
        return true;
    }

}
