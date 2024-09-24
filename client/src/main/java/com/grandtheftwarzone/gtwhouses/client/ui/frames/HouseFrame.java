package com.grandtheftwarzone.gtwhouses.client.ui.frames;

import com.grandtheftwarzone.gtwhouses.client.GTWHousesUI;
import com.grandtheftwarzone.gtwhouses.client.ui.panels.HousePanel;
import com.grandtheftwarzone.gtwhouses.client.ui.panels.MyHousesPanel;
import com.grandtheftwarzone.gtwhouses.common.data.House;
import com.grandtheftwarzone.gtwhouses.common.data.HouseType;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class HouseFrame extends GuiFrame {
    public static final ResourceLocation CSS_LOCATION = new ResourceLocation(GTWHousesUI.MODID, "css/house_frame.css");
    public static final ResourceLocation MAP_LOCATION = new ResourceLocation(GTWHousesUI.MODID, "textures/map.png");

    Collection<House> houses;

    List<House> forSale = new ArrayList<>();
    List<House> forRenting = new ArrayList<>();
    List<House> unavailable = new ArrayList<>();
    List<House> myHouses = new ArrayList<>();

    Map<String, List<House>> houseData = new LinkedHashMap<>();

    public static boolean sortHighToLow = false;

    int selectedTab = 0;
    public static HouseType filterType = null;

    int tabIndex = 0;

    GuiTabbedPane tabbedPane;


    public HouseFrame(Collection<House> houses) {
        super(new GuiScaler.Identity());

        this.houses = houses = houses.stream().sorted(Comparator.comparingDouble(House::getBuyCost)).collect(Collectors.toList());

        for (House house : houses) {
            if (house.isOwned() && house.getOwner().equals(Minecraft.getMinecraft().player.getUniqueID())) {
                myHouses.add(house);
                continue;
            }

            if (house.isForSale()) forSale.add(house);
            else if (house.isRentable()) forRenting.add(house);
            else unavailable.add(house);
        }

        houseData.put("ALL", new ArrayList<>(houses));
        houseData.put("FOR SALE", forSale);
        houseData.put("FOR RENTING", forRenting);
        houseData.put("UNAVAILABLE", unavailable);
        //houseData.put("MY HOUSES", myHouses);

        setCssClass("frame");
        refresh();
    }

    public void refresh() {
        removeAllChilds();

        add(new GuiLabel("Grand Theft Warzone").setCssId("titleId"));

        tabbedPane = new GuiTabbedPane();
        tabbedPane.setCssClass("tabbedPane");

        tabIndex = 0;
        for (Map.Entry<String, List<House>> entry : houseData.entrySet()) {
            GuiPanel panel = new GuiPanel();
            panel.setCssClass("tabContent");

            GuiPanel contentPanel = new GuiPanel();
            contentPanel.setCssClass("tabCenterContent");

            GuiScrollPane scrollPane = new GuiScrollPane();
            scrollPane.setCssId("houseScrollPane");
            scrollPane.setLayout(GridLayout.columnLayout(200, 10));

            List<House> filter = entry.getValue();

            for (int i = sortHighToLow ? filter.size() - 1 : 0; sortHighToLow ? i >= 0 : i < filter.size(); i += sortHighToLow ? -1 : 1) {
                House house = filter.get(i);
                if (house == null) continue;

                if (filterType != null && house.getType() != filterType) continue;

                scrollPane.add(new HousePanel(house).setCssClass("housePanel"));
            }

            contentPanel.add(scrollPane);

            GuiPanel mapPanel = new GuiPanel();
            mapPanel.setCssId("map");
            mapPanel.getStyle().setTexture(new GuiTextureSprite(MAP_LOCATION));
            contentPanel.add(mapPanel);


            panel.add(createBarPanel());
            panel.add(contentPanel);

            tabbedPane.addTab(entry.getKey(), panel);

            final int thisTabIndex = tabIndex++;
            tabbedPane.getTabButton(thisTabIndex).addClickListener((mouseX, mouseY, mouseButton) -> selectedTab = thisTabIndex);
        }

        tabbedPane.addTab("MY HOUSES", new MyHousesPanel(myHouses, createBarPanel()));
        tabbedPane.getTabButton(tabIndex).addClickListener((mouseX, mouseY, mouseButton) -> selectedTab = tabIndex);

        tabbedPane.selectTab(selectedTab);
        add(tabbedPane);
    }

    public GuiPanel createBarPanel() {
        GuiPanel barPanel = new GuiPanel();
        barPanel.setCssId("bar");

        GuiPanel barContentPanel = new GuiPanel();
        barContentPanel.setCssClass("barContent");
        barContentPanel.setLayout(new GridLayout(-1, 20, 0, GridLayout.GridDirection.HORIZONTAL, 2));

        GuiPanel orderPanel = new GuiPanel();
        orderPanel.setCssClass("barGroup");
        orderPanel.setLayout(new GridLayout(65, 20, 5, GridLayout.GridDirection.HORIZONTAL, 4));


        GuiLabel lowToHigh = new GuiLabel("LOW TO HIGH");
        GuiLabel highToLow = new GuiLabel("HIGH TO LOW");
        if (!sortHighToLow) lowToHigh.setCssClass("selectedFilter");
        else highToLow.setCssClass("selectedFilter");

        lowToHigh.addClickListener((mouseX, mouseY, mouseButton) -> {
            sortHighToLow = false;
            refresh();
        });

        highToLow.addClickListener((mouseX, mouseY, mouseButton) -> {
            sortHighToLow = true;
            refresh();
        });

        orderPanel.add(new GuiLabel("      PRICE:"));
        orderPanel.add(lowToHigh);
        orderPanel.add(highToLow);

        barContentPanel.add(orderPanel);

        GuiPanel filterPanel = new GuiPanel();
        filterPanel.setCssId("filterGroup");
        filterPanel.setCssClass("barGroup");
        filterPanel.setLayout(new GridLayout(55, 20, 5, GridLayout.GridDirection.HORIZONTAL, 10));

        GuiLabel filterLabel = new GuiLabel("FILTER BY:");
        GuiLabel highEnd = new GuiLabel("HIGH-END");
        GuiLabel middleEnd = new GuiLabel("MID-END");
        GuiLabel lowEnd = new GuiLabel("LOW-END");

        if (filterType == HouseType.HIGH_END) highEnd.setCssClass("selectedFilter");
        else if (filterType == HouseType.MIDDLE_END) middleEnd.setCssClass("selectedFilter");
        else if (filterType == HouseType.LOW_END) lowEnd.setCssClass("selectedFilter");

        highEnd.addClickListener((mouseX, mouseY, mouseButton) -> {
            if (filterType == HouseType.HIGH_END) filterType = null;
            else filterType = HouseType.HIGH_END;
            refresh();
        });

        middleEnd.addClickListener((mouseX, mouseY, mouseButton) -> {
            if (filterType == HouseType.MIDDLE_END) filterType = null;
            else filterType = HouseType.MIDDLE_END;
            refresh();
        });

        lowEnd.addClickListener((mouseX, mouseY, mouseButton) -> {
            if (filterType == HouseType.LOW_END) filterType = null;
            else filterType = HouseType.LOW_END;
            refresh();
        });


        filterPanel.add(filterLabel);
        filterPanel.add(highEnd);
        filterPanel.add(middleEnd);
        filterPanel.add(lowEnd);

        barContentPanel.add(filterPanel);

        barPanel.add(barContentPanel);

        return barPanel;
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Collections.singletonList(CSS_LOCATION);
    }

    @Override
    public boolean needsCssReload() {
        return true;
    }
}
