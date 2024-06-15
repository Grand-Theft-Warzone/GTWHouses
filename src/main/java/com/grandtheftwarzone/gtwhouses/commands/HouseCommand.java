package com.grandtheftwarzone.gtwhouses.commands;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
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
        super(plugin, "house", "house.houseplayer");
        this.plugin = plugin;

        addSubcommand(new UnregisterHouseCommand(plugin, this));
        addSubcommand(new RegisterHouseCommand(plugin, this));
        addSubcommand(new HouseRemoveCommand(plugin, this));
        addSubcommand(new ResetHouseCommand(plugin, this));
        addSubcommand(new HouseListCommand(plugin, this));

        addSubcommand(new TopBuyersCommand(plugin, this));
        addSubcommand(new OwnedHousesCommand(plugin, this));
        addSubcommand(new AdminPlayerHousesCommand(plugin, this));

        addSubcommand(new BuyHouseCommand(plugin, this));
        addSubcommand(new SellHouseCommand(plugin, this));
        addSubcommand(new RentHouseCommand(plugin, this));
        addSubcommand(new UnrentHouseCommand(plugin, this));
        addSubcommand(new UnsellHouseCommand(plugin, this));
        addSubcommand(new CitySellHouseCommand(plugin, this));
        addSubcommand(new RentableHouseCommand(plugin, this));
        addSubcommand(new UnrentableHouseCommand(plugin, this));
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
