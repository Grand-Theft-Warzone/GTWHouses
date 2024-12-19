package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class ContainerSell extends Container {
    private final InventoryBasic inventorySell;
    private final InventoryPlayer playerCopyInventory;
    private final Map<String, ShopItem> sellableItems;

    @Getter
    private int profit;

    public ContainerSell(InventoryPlayer playerInventory, Map<String, ShopItem> sellableItems) {
        this.sellableItems = sellableItems;
        this.inventorySell = new InventoryBasic("Sell", false, 27);
        this.playerCopyInventory = new InventoryPlayer(null);
        for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
            this.playerCopyInventory.setInventorySlotContents(i, playerInventory.getStackInSlot(i).copy());
        }

        // Add slots for the sell inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventorySell, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        // Add slots for the player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerCopyInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Add slots for the player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerCopyInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.inventorySell.isUsableByPlayer(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
    }

    public void mySlotClicked(Slot slot, int slotId, int mouseButton, ClickType clickTypeIn) {
        if (clickTypeIn == ClickType.QUICK_CRAFT ||
                clickTypeIn == ClickType.THROW ||
                slotId < 0 ||
                slotId >= this.inventorySlots.size()
        ) return;

        if (slot == null) return;
        if (!slot.getStack().isEmpty() && !isSellable(slot.getStack())) return;

        ItemStack cursorStack = Minecraft.getMinecraft().player.inventory.getItemStack();
        if (!cursorStack.isEmpty()) {
            if (slot.getStack().getItem() == cursorStack.getItem()) {
                int i = cursorStack.getCount();
                if (slot.getStack().getCount() + cursorStack.getCount() > slot.getStack().getMaxStackSize()) {
                    i = slot.getStack().getMaxStackSize() - slot.getStack().getCount();
                }
                slot.getStack().grow(i);
                cursorStack.shrink(i);
                return;
            }

            ItemStack oldCursor = cursorStack.copy();

            if (!slot.getStack().isEmpty()) {
                if (slot.getStack().getItem() != cursorStack.getItem()) {
                    Minecraft.getMinecraft().player.inventory.setItemStack(slot.getStack().copy());
                    slot.putStack(oldCursor);
                    return;
                }

                if (slot.getStack().getCount() >= slot.getStack().getMaxStackSize()) return;

                int count = cursorStack.getCount();
                if (mouseButton == 1) count = 1;

                Minecraft.getMinecraft().player.inventory.getItemStack().shrink(count);
                slot.getStack().grow(count);
            }

            int count = cursorStack.getCount();
            if (mouseButton == 1) count = 1;

            slot.putStack(new ItemStack(cursorStack.getItem(), count));
            Minecraft.getMinecraft().player.inventory.getItemStack().shrink(count);
            return;
        }

        ItemStack stack = slot.getStack();
        if (stack.isEmpty()) return;

        ItemStack copy = stack.copy();

        if (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.PICKUP_ALL) {
            int count = stack.getCount();
            if (mouseButton == 1) count = count / 2;

            copy.setCount(count);
            Minecraft.getMinecraft().player.inventory.setItemStack(copy);
            slot.getStack().shrink(count);
            return;
        }

        if (clickTypeIn == ClickType.QUICK_MOVE) {
            IInventory i = slot.inventory == playerCopyInventory ? inventorySell : playerCopyInventory;
            moveStackToInventory(slot.getStack(), i);
        }

    }

    public boolean isSellable(ItemStack stack) {
        return sellableItems.containsKey(stack.getItem().getRegistryName().toString());
    }

    private void moveStackToInventory(ItemStack stack, IInventory targetInventory) {
        for (int i = 0; i < targetInventory.getSizeInventory(); i++) {
            ItemStack targetStack = targetInventory.getStackInSlot(i);

            if (targetStack.isEmpty()) {
                targetInventory.setInventorySlotContents(i, stack.copy());
                stack.setCount(0);
                break;
            } else if (ItemStack.areItemsEqual(stack, targetStack) && ItemStack.areItemStackTagsEqual(stack, targetStack)) {
                int space = targetStack.getMaxStackSize() - targetStack.getCount();
                if (space > 0) {
                    int transfer = Math.min(space, stack.getCount());
                    targetStack.grow(transfer);
                    stack.shrink(transfer);
                    if (stack.getCount() <= 0) break;
                }
            }
        }
    }

    public void calculateProfit() {
        profit = 0;
        for (int i = 0; i < inventorySell.getSizeInventory(); i++) {
            ItemStack stack = inventorySell.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            profit += sellableItems.get(stack.getItem().getRegistryName().toString()).getSellPrice() * stack.getCount();
        }
    }

    public Map<String, Integer> getItemsToSell() {
        Map<String, Integer> items = new java.util.HashMap<>();
        for (int i = 0; i < inventorySell.getSizeInventory(); i++) {
            ItemStack stack = inventorySell.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            items.put(stack.getItem().getRegistryName().toString(), stack.getCount());
        }
        return items;
    }
}