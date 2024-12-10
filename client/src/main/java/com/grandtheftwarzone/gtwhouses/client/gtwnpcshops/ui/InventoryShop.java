package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public class InventoryShop extends InventoryBasic {
    public InventoryShop(Shop shop, Collection<ShopItem> items) {
        super(shop.getName(), false, 27);

        int i = 0;
        for (String item : shop.getItems()) {
            for (ShopItem shopItem : items) {
                if (shopItem.getItem().equals(item)) {
                    Item nmsItem = Item.getByNameOrId(shopItem.getItem());
                    if (nmsItem == null)
                        nmsItem = Item.getByNameOrId("minecraft:barrier");
                    if (nmsItem == null) continue;

                    ItemStack stack = new ItemStack(nmsItem, 1);

                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag == null) tag = new NBTTagCompound();

                    //Rendered using the ShopToolTipHandler
                    tag.setInteger("cost", shopItem.getBuyPrice());
                    tag.setInteger("level", shopItem.getBuyLevel());
                    stack.setTagCompound(tag);

                    this.setInventorySlotContents(i++, stack);
                    break;
                }
            }
        }
    }
}