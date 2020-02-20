package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class NearMeCommand extends NestedCommand {

    private static final NearMeCommand instance = new NearMeCommand();

    private NearMeCommand() {
        super("nearme");
    }

    public static NearMeCommand getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Entity)) {
            Common.tell(commandSender, "&bOnly living entities can execute this command.");
            return true;
        }
        Entity entity = (Entity) commandSender;
        Location location = entity.getLocation();
        int amt = CustomOreGen.getGeneratorManager().getGeneratorsAt(location).size();
        Common.tell(entity, "&e[Generators] &aThere are " + amt + " block generators near you!");
        return true;
    }
}
