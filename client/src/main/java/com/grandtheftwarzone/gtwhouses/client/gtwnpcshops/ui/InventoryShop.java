package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ui;

import com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.ItemUtils;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.Shop;
import com.grandtheftwarzone.gtwhouses.common.gtwnpcshops.data.ShopItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public class InventoryShop extends InventoryBasic {
    public InventoryShop(Shop shop, Collection<ShopItem> items) {
        super(shop.getName(), false, 6 * 13);

        int i = 0;
        for (String item : shop.getItems()) {
            for (ShopItem shopItem : items) {
                if (!shopItem.getSerializedItemStack().equals(item))
                    continue;

                ItemStack stack = ItemUtils.deserializeItemStack(item);
                if (stack == null)
                    stack = new ItemStack(Item.getByNameOrId("minecraft:barrier"), 1);

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