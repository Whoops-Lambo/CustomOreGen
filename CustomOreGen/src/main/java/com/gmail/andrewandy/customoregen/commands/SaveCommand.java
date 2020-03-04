package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import org.bukkit.command.CommandSender;

public class SaveCommand extends NestedCommand {

    private static final SaveCommand instance = new SaveCommand();

    private SaveCommand() {
        super("&cSave", "com.gmail.andrewandy.customoregen.save");
        setDescription("&eSave all generator data to disk.");
    }

    public static SaveCommand getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        long millis = System.currentTimeMillis();
        AbstractGenerator.globalUpdateFile();
        Common.tell(commandSender, "&3&l[CustomOreGen] &aSaved data, took " + (System.currentTimeMillis() - millis) + "ms");
        return true;
    }
}
