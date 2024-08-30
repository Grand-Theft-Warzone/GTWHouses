package com.grandtheftwarzone.gtwhouses.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HouseActions {
    //Unowned house
    Buy("buy", false, "Buy a house"),
    Rent("rent", false, "Rent a house"),
    Unrent("unrent", false, "Stop renting a house"),

    //Owned house
    Sell("sell", false, "Sell one of your houses"),
    CitySell("citysell", false, "Sell one of your houses to the city"),
    Unsell("unsell", false, "Stop selling one of your houses"),
    Rentable("rentable", false, "Make a house available for rent"),
    Unrentable("unrentable", false, "Stop making a house available for rent"),

    //Listings
    List("list", false, "List your houses"),
    Top("top", false, "List the top buyers"),


    //Admin
    //Register("register", true),
    Remove("remove", true, "Remove a house and reset its blocks"),
    Reset("reset", true, "Reset a house's blocks"),
    Unregister("unregister", true, "Unregister a house")
    ;

    private final String name;
    private final boolean requiresAdmin;
    public final String description;

    public static HouseActions get(String action) {
        for (HouseActions houseAction : values())
            if (houseAction.getName().equalsIgnoreCase(action))
                return houseAction;
        return null;
    }
}
