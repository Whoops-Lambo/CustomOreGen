package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;

public class ReloadCommand extends NestedCommand {

    private static final ReloadCommand instance = new ReloadCommand();
    private Collection<Callable<?>> reloadTasks = new ArrayList<>();

    public void registerReloadTask(Callable<?> reloadTask) {
        unregisterReloadTask(reloadTask);
        reloadTasks.add(Objects.requireNonNull(reloadTask));
    }

    public void unregisterReloadTask(Callable<?> task) {
        reloadTasks.remove(task);
    }

    private ReloadCommand() {
        super("reload", "com.gmail.andrewandy.customoregen.reload");
        setDescription("&eReload the block generator settings.");
    }

    public static ReloadCommand getInstance() {
        return instance;
    }

    public boolean executeReload() {
        boolean error = false;
        try {
            CustomOreGen.loadConfig();
            for (Callable<?> callable : reloadTasks) {
                try {
                    callable.call();
                } catch (Exception ex) {
                    error = true;
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            error = true;
            ex.printStackTrace();
        }
        return error;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        boolean error = executeReload();
        if (error) {
            Common.tell(commandSender, "&c&l[CustomOreGen] &cError occurred, please check console.");
        } else {
            Common.tell(commandSender, "&3&l[CustomOreGen] &eSettings Reloaded!");
        }
        return true;
    }
}
