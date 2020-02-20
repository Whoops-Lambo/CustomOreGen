package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class BaseCommand implements com.gmail.andrewandy.corelib.api.command.BaseCommand {

    private Collection<NestedCommand> nestedCommands;
    private static final BaseCommand instance = new BaseCommand();

    private BaseCommand() {
        nestedCommands = Arrays.asList(
                HelpCommand.getInstance(),
                NearMeCommand.getInstance(),
                ReloadCommand.getInstance(),
                SaveCommand.getInstance());
    }

    public static BaseCommand getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings == null || strings.length < 1) {
            Common.tell(commandSender, "&3CustomOreGen version &b" + CustomOreGen.getInstance().getDescription().getVersion());
            return true;
        }
        String label = strings[0];
        Optional<NestedCommand> nested = getNested(label, false);
        boolean result = false;
        if (nested.isPresent()) {
            result = nested.get().onCommand(commandSender, Arrays.copyOfRange(strings, 1, strings.length));
        }
        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    private Optional<NestedCommand> getNested(String label, boolean startsWith) {
        return nestedCommands.stream().filter(cmd -> {
            if (startsWith) {
                return cmd.getLabel().startsWith(label);
            }
            return cmd.getLabel().equalsIgnoreCase(label);
        }).findAny();
    }
}
