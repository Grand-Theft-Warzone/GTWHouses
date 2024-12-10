package com.grandtheftwarzone.gtwhouses.client.gtwnpcshops.npc;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

public class ShopNPC extends EntityLiving {
    public static final int ENTITY_ID = 7777;

    public ShopNPC(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 1.8f);
    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false; //TODO: No one can attack the NPC
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return null;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return null;
    }
}
