package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseCommand implements com.gmail.andrewandy.corelib.api.command.BaseCommand {

    private static final BaseCommand instance = new BaseCommand();
    public static final String INSUFFICIENT_PERMS = "&cInsufficient Permission!";
    private Collection<NestedCommand> nestedCommands;

    private BaseCommand() {
        nestedCommands = new HashSet<>(Arrays.asList(
                HelpCommand.getInstance(),
                NearMeCommand.getInstance(),
                ReloadCommand.getInstance(),
                SaveCommand.getInstance()));
    }

    public void registerNestedCommand(NestedCommand nestedCommand) {
        if (nestedCommand == null) {
            return;
        }
        nestedCommands.removeIf(target -> target.getLabel().equalsIgnoreCase(nestedCommand.getLabel()));
        nestedCommands.add(nestedCommand);
    }

    public Collection<NestedCommand> getNestedCommands() {
        return new HashSet<>(nestedCommands);
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
        int index = 0;
        String label = ChatColor.stripColor(strings[index++]);
        Optional<NestedCommand> nested = sortNestedByTargetLabel(label)
                .filter(cmd -> cmd.getLabel(true).equalsIgnoreCase(label))
                .findAny();
        if (nested.isPresent()) {
            NestedCommand nestedCommand = nested.get();
            if (nestedCommand.hasDescription() && !commandSender.hasPermission(nestedCommand.getPermission())) {
                Common.tell(commandSender, INSUFFICIENT_PERMS);
                return true;
            }
            return nested.get().onCommand(commandSender, Arrays.copyOfRange(strings, index, strings.length));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        String targetLabel = strings[0];
        return sortNestedByTargetLabel(targetLabel)
                .filter(permissionFilter(commandSender))
                .map(NestedCommand::getLabel)
                .collect(Collectors.toList());
    }

    /**
     * Sorts all the labels which are closest to the target label.
     *
     * @param label The target label.
     * @return Returns a stream of commands which have been sorted by
     * their relative closeness to the target label. This stream may be empty if
     * no commands were found which start with the target label.
     */
    private Stream<NestedCommand> sortNestedByTargetLabel(String label) {
        String stripped = ChatColor.stripColor(label);
        return nestedCommands.stream().filter(cmd ->
                cmd.getLabel(true).equalsIgnoreCase(stripped) || cmd.getLabel(true).startsWith(stripped)
        ).sorted((first, second) -> {
            int firstMatch, secondMatch;
            firstMatch = first.getLabel(true).compareTo(stripped);
            secondMatch = second.getLabel(true).compareTo(stripped);
            return Integer.compare(secondMatch, firstMatch);
        });
    }


    private static Predicate<NestedCommand> permissionFilter(Permissible permissible) {
        Objects.requireNonNull(permissible);
        return (NestedCommand command) -> permissible.hasPermission(command.getPermission());
    }

    /**
     * @return Returns a list of nested commands which have been sorted by their labels alphabetically.
     */
    public List<NestedCommand> getSortedNestedCommands() {
        return nestedCommands.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    public List<NestedCommand> getSortedNestedCommands(Permissible permissible) {
        return nestedCommands.stream().filter(permissionFilter(permissible))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
