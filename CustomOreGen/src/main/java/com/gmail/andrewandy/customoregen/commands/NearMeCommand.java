package com.gmail.andrewandy.customoregen.commands;

import com.gmail.andrewandy.corelib.api.command.NestedCommand;
import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class NearMeCommand extends NestedCommand {

    private static final NearMeCommand instance = new NearMeCommand();

    private NearMeCommand() {
        super("&bNearMe");
        setDescription("&aCounts how many generators are active at your current location.");
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
        Collection<BlockGenerator> generators = CustomOreGen.getGeneratorManager().getGeneratorsAt(location);
        int total = generators.size();
        int global = Math.toIntExact(generators.stream().filter(BlockGenerator::isGlobal).count());
        int nonGlobal = total - global;
        int amt = nonGlobal + global > 1 ? 1 : global;
        Common.tell(entity, "&e&l[Generators] &aThere are " + amt + " active block generators near you!");
        return true;
    }
}
