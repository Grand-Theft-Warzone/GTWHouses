package com.grandtheftwarzone.gtwhouses.server.gtwhouses.phone;

import com.grandtheftwarzone.phone.api.app.App;
import com.grandtheftwarzone.phone.api.app.AppData;
import com.grandtheftwarzone.phone.api.app.AppIcon;
import com.grandtheftwarzone.phone.api.app.AppSide;
import com.grandtheftwarzone.phone.api.intermediate.I10EResourceLocation;
import com.grandtheftwarzone.phone.api.ui.ServerUIComponent;
import com.grandtheftwarzone.phone.api.ui.UserInterface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GTWHousesApp implements App {
    @Nullable
    @Override
    public String group() {
        return "gtwhouses";
    }

    @Nullable
    @Override
    public AppIcon icon() {
        return new AppIcon(new I10EResourceLocation("gtwhouses", "textures/gui/houses.png"));
    }

    @Nonnull
    @Override
    public String id() {
        return "gtwhouses-app";
    }

    @Nonnull
    @Override
    public String name() {
        return "GTW Houses";
    }

    @Override
    public boolean clientTranslatedName() {
        return false;
    }

    @Nonnull
    @Override
    public String version() {
        return "1.0";
    }

    @Nonnull
    @Override
    public AppSide side() {
        return AppSide.SERVER;
    }

    @Override
    public void setAppData(@Nonnull AppData<?> appData) {

    }

    @Nonnull
    @Override
    public UserInterface userInterface() {
        ServerUIComponent component = new ServerUIComponent(ServerUIComponent.ComponentType.PANEL);
       /* component.addListener(ServerUIComponent.ListenerType.OPEN, (player) -> {
            GTWHouses.getPacketManager().sendPacket(player, new HousesGUIS2CPacket(OpenGUIC2SPacket.OpenGUIType.HOUSES, GTWHouses.getHousesManager().getHouses()));
            return null;
        });
*/

        return component;
    }
}
