package com.grandtheftwarzone.gtwhouses.client.gtwhouses.ui.modals;

import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;

public class ConfirmModal extends GuiPanel {

    public ConfirmModal(GuiPanel parent, IMouseClickListener listener) {
        setCssClass("modal");
        setCssId("confirmModal");
        setLayout(GridLayout.columnLayout(50, 10));

        GuiLabel label = new GuiLabel("Are you sure?");

        GuiPanel buttonPanel = new GuiPanel();
        buttonPanel.setCssClass("buttonPanel");
        buttonPanel.setLayout(new GridLayout(75, 15, 15, GridLayout.GridDirection.HORIZONTAL, 3));

        GuiButton yesButton = new GuiButton("Yes");
        yesButton.setCssClass("yesButton");

        GuiButton noButton = new GuiButton("No");
        noButton.setCssClass("noButton");

        noButton.addClickListener((a, b, c) -> parent.remove(this));

        yesButton.addClickListener(listener);

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        add(label);
        add(buttonPanel);
    }
}
