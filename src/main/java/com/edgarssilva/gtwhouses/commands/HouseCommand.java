package com.edgarssilva.gtwhouses.commands;

import com.edgarssilva.gtwhouses.GTWHouses;
import me.phoenixra.atum.core.command.AtumCommand;
import me.phoenixra.atum.core.exceptions.NotificationException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class HouseCommand extends AtumCommand {
    GTWHouses plugin;

    public HouseCommand(@Nonnull GTWHouses plugin) {
        super(plugin, "house", "house.houseadmin");
        this.plugin = plugin;
        addSubcommand(new RegisterHouseCommand(plugin, this));

    }


    @Override
    protected void onCommandExecute(@NotNull CommandSender sender, @NotNull List<String> args) throws NotificationException {
        getSubcommands().get("help").handleCommand(sender, args.toArray(new String[0]));
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull List<String> args) {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "House commands";
    }

    @Override
    public @NotNull String getUsage() {
        return "/house";
    }
}
