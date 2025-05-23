package com.grandtheftwarzone.gtwhouses.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HouseActions {
    GUI("gui", false, false, "Open the house GUI"),
    //Unowned house
    Buy("buy", true, false, "Buy a house"),
    Rent("rent", true, false, "Rent a house"),
    Unrent("unrent", true, false, "Stop renting a house"),

    //Owned house
    Sell("sell", true, false, "Sell one of your houses"),
    CitySell("citysell", true, false, "Sell one of your houses to the city"),
    Unsell("unsell", true, false, "Stop selling one of your houses"),
    Rentable("rentable", true, false, "Make a house available for rent"),
    Unrentable("unrentable", true, false, "Stop making a house available for rent"),

    //Listings
    List("list", false, false, "List your houses"),
    Top("top", false, false, "List the top buyers"),


    //Admin
    Admin("admin", false, true, "Open admin GUI"),
    Register("register", false, true, "Open admin GUI to register a house"),
    Remove("remove", true, true, "Remove a house and reset its blocks"),
    Reset("reset", true, true, "Reset a house's blocks"),
    Unregister("unregister", true, true, "Unregister a house"),
    Edit("edit", true, true, "Edit a house"),
    EditBlocks("editblocks", true, true, "Edit a house's blocks"),
    Finish("finish", false, true, "Finish editing a house's blocks"),
    ;

    private final String name;
    private final boolean requiresHouse;
    private final boolean requiresAdmin;
    public final String description;

    public static HouseActions get(String action) {
        for (HouseActions houseAction : values())
            if (houseAction.getName().equalsIgnoreCase(action))
                return houseAction;
        return null;
    }
}
