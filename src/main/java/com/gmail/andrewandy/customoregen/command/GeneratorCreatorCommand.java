package com.gmail.andrewandy.customoregen.command;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GeneratorCreatorCommand implements CommandExecutor {

    private Map<String, BlockGenerator> generatorMap = new HashMap<>();

    private Collection<NestedCommand> nestedCommands = Arrays.asList();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (strings[0].toLowerCase()) {
            case "add":
                try {
                    String name = strings[1];
                    String blockName = strings[2];
                    String rawChance = strings[3];
                    double chance = Double.parseDouble(rawChance);
                    Material block = Material.valueOf(blockName);
                } catch (IllegalArgumentException ex) {
                    Common.tell(commandSender, "&bInvalid BlockName or chance provided.");
                    return true;
                }
        }
        return false;
    }
}
