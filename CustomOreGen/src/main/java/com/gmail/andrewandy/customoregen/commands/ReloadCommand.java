package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.hooks.bentobox.BentoBoxHookManager;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class ReloadCommand extends NestedCommand {

    private static final ReloadCommand instance = new ReloadCommand();

    private ReloadCommand() {
        super("reload", "com.gmail.andrewandy.customoregen.reload");
    }

    public static ReloadCommand getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission(super.getPermission())) {
            Common.tell(commandSender, "&cInsufficient Permission!");
            return true;
        }
        try {
            CustomOreGen.loadConfig();
            BentoBoxHookManager.reload();
            Common.tell(commandSender, "&3&l[CustomOreGen] &eSettings Reloaded!");
        } catch (IOException e) {
            Common.tell(commandSender, "&c&l[CustomOreGen] &cError occurred, please check console.");
            e.printStackTrace();
        }
        return true;
    }
}
