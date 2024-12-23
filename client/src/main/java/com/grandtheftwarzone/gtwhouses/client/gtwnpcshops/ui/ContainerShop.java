package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ItemUtils;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItemAmount;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContainerShop extends Container {
    private final IInventory shopInventory;
    @Getter
    private final IInventory buyListInventory;

    private final Shop shop;
    private final Collection<ShopItem> items;


    @Getter
    private int totalCost;

    @Getter
    private int minLevel;

    public ContainerShop(Shop shop, Collection<ShopItem> items) {
        this.shop = shop;
        this.items = items;
        this.shopInventory = new InventoryShop(shop, items);
        this.buyListInventory = new InventoryBasic("Buy List", false, 27);


        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 13; ++col) {
                int index = row * 9 + col;
                int x = 8 + col * 18;
                int y = 18 + row * 18;

                this.addSlotToContainer(new Slot(shopInventory, index, x, y) {
                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn) {
                        return false; // Prevent removing items from the shop
                    }
                });
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int index = row * 9 + col;
                int x = 8 + col * 18;
                int y = 140 + row * 18; // Adjust Y position below shop
                this.addSlotToContainer(new Slot(buyListInventory, index, x, y));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY; // Prevent shift-clicking behavior
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return ItemStack.EMPTY;
    }

    public void customSlotClip(int slotId, int mouseButton, ClickType clickTypeIn, EntityPlayer player) {
        mySlotClick(slotId, mouseButton, clickTypeIn, player);
        calculateTotalCost();
    }

    private void mySlotClick(int slotId, int mouseButton, ClickType clickTypeIn, EntityPlayer player) {
        if (clickTypeIn == ClickType.QUICK_CRAFT ||
                clickTypeIn == ClickType.THROW ||
                slotId < 0 ||
                slotId >= this.inventorySlots.size()
        ) return;


        Slot slot = this.inventorySlots.get(slotId);
        if (slot.inventory == player.inventory) return;

        ItemStack cursorStack = player.inventory.getItemStack();
        if (!cursorStack.isEmpty()) {
            if (slot.inventory == shopInventory) {
                if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.PICKUP_ALL) && slot.getStack().getItem() == cursorStack.getItem())
                    player.inventory.getItemStack().setCount(cursorStack.getCount() + 1);
                else
                    player.inventory.setItemStack(ItemStack.EMPTY);
            } else {

                if (slot.inventory == buyListInventory) {
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
                    if (!slot.getStack().isEmpty())
                        player.inventory.setItemStack(slot.getStack().copy());
                    else
                        player.inventory.setItemStack(ItemStack.EMPTY);
                    buyListInventory.setInventorySlotContents(slot.getSlotIndex(), oldCursor);
                }
                return;
            }
        }

        ItemStack stack = slot.getStack();
        if (stack.isEmpty()) return;

        ItemStack copy = stack.copy();

        if (slot.inventory == shopInventory) {
            switch (clickTypeIn) {
                case PICKUP:
                case PICKUP_ALL:
                    copy.setCount(1);
                    player.inventory.setItemStack(copy);
                    break;
                case CLONE:
                    copy.setCount(stack.getMaxStackSize());
                    player.inventory.setItemStack(copy);
                    break;
                case QUICK_MOVE:
                    copy.setCount(stack.getMaxStackSize());
                    moveStackToInventory(copy, buyListInventory);
                    break;
            }
        } else if (slot.inventory == buyListInventory) {
            switch (clickTypeIn) {
                case PICKUP:
                case PICKUP_ALL:
                    if (mouseButton == 1) {
                        copy.setCount(copy.getCount() / 2);
                        player.inventory.setItemStack(copy);
                        buyListInventory.decrStackSize(slot.getSlotIndex(), copy.getCount());
                        break;
                    }
                    player.inventory.setItemStack(copy);
                    buyListInventory.decrStackSize(slot.getSlotIndex(), copy.getCount());
                case CLONE:
                    player.inventory.setItemStack(copy);
                    buyListInventory.decrStackSize(slot.getSlotIndex(), copy.getCount());
                    break;
                case QUICK_MOVE:
                    buyListInventory.setInventorySlotContents(slot.getSlotIndex(), ItemStack.EMPTY);
                    break;
            }
        }
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

    private void calculateTotalCost() {
        totalCost = 0;
        minLevel = 0;

        for (int i = 0; i < buyListInventory.getSizeInventory(); i++) {
            ItemStack stack = buyListInventory.getStackInSlot(i);

            if (!stack.isEmpty()) {
                System.out.println(stack.getDisplayName());
                ShopItem item = getShopitem(stack);
                if (item == null) continue;

                totalCost += item.getBuyPrice() * stack.getCount();
                minLevel = Math.max(minLevel, item.getBuyLevel());
            }
        }
    }

    private ShopItem getShopitem(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setTagCompound(null); //Remove tooltip information for comparison
        copy.setCount(1);

        for (ShopItem item : items)
            if (item.getSerializedItemStack().equals(ItemUtils.serializeItemStack(copy)))
                return item;

        return null;
    }

    public List<ShopItemAmount> getBuyList() {
        List<ShopItemAmount> buyList = new ArrayList<>();

        for (int i = 0; i < buyListInventory.getSizeInventory(); i++) {
            ItemStack stack = buyListInventory.getStackInSlot(i);
            ItemStack copy = stack.copy();
            copy.setTagCompound(null); //Remove tooltip information for comparison
            copy.setCount(1);
            if (!stack.isEmpty()) {
                ShopItemAmount itemAmount = new ShopItemAmount(ItemUtils.serializeItemStack(copy), stack.getCount());
                buyList.add(itemAmount);
            }
        }

        return buyList;
    }
}