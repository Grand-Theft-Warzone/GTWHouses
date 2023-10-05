package com.edgarssilva.gtwhouses.util;

import org.bukkit.ChatColor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class HouseRent implements Serializable {

    private final double costPerDay;
    private int daysDuration;
    private Date startDate;
    private RentStatus status;
    private UUID renter;

    private boolean renewable;


    public HouseRent(double costPerDay) {
        this.costPerDay = costPerDay;
        this.daysDuration = -1;
        this.startDate = null;
        this.renter = null;
        this.status = RentStatus.NOT_RENTED;
        this.renewable = true;
    }

    public void startRent(UUID renter, int daysDuration) {
        this.renter = renter;
        this.daysDuration = daysDuration;
        this.startDate = new Date();
        this.status = RentStatus.RENTED;
        this.renewable = true;
    }

    public void renewRent(int daysDuration) {
        this.daysDuration += daysDuration;
        this.startDate = new Date();
        this.status = RentStatus.RENTED;
        this.renewable = true;
    }

    public void stopRent() {
        this.renter = null;
        this.daysDuration = -1;
        this.startDate = null;
        this.status = RentStatus.NOT_RENTED;
        this.renewable = true;
    }

    public boolean isRented() {
        return this.status != RentStatus.NOT_RENTED;
    }


    public double getCostPerDay() {
        return costPerDay;
    }

    public int getDaysDuration() {
        return daysDuration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStatus(RentStatus status) {
        this.status = status;
    }

    public RentStatus getStatus() {
        return status;
    }

    public UUID getRenter() {
        return renter;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    public enum RentStatus {
        OVERDUE, // More than 1 day overdue
        EXPIRED, // Expired (still has 1 day to pay)
        CLOSE_TO_EXPIRE, // 1 day left
        EXPIRING, // Today
        RENTED, // More than 1 day left
        NOT_RENTED // Not rented
        ;

        public static RentStatus fromDays(int daysLeft) {
            switch (daysLeft) {
                case 0:
                    return EXPIRING;
                case 1:
                    return CLOSE_TO_EXPIRE;
                case -1:
                    return EXPIRED;
                default:
                    return daysLeft < -1 ? OVERDUE : RENTED;
            }
        }

        public String getWarning(House house) {
            String houseName = ChatColor.GOLD + house.getName() + ChatColor.RED;

            int daysLeft = HouseUtils.getRemainingRentDays(house.getRent());

            switch (house.getRent().getStatus()) {
                case EXPIRED:
                    return "§cYour house " + houseName + " rent has expired." + (house.getRent().isRenewable() ? " You have until today to pay it." : " Resetting house... ");
                case CLOSE_TO_EXPIRE:
                    return "§cYour house " + houseName + " rent will expire in 1 day.";
                case EXPIRING:
                    return "§cYour house " + houseName + " rent will expire today.";
                case OVERDUE:
                    return "§cYour house " + houseName + " rent is overdue. Resetting house...";
                default:
                    return "You house " + houseName + ChatColor.RESET + " rent will expire in " + ChatColor.YELLOW + daysLeft + ChatColor.RESET + " day" + (daysLeft > 1 ? "s" : "") + ".";
            }
        }

        public boolean shouldReset(House house) {
            switch (house.getRent().getStatus()) {
                case EXPIRED:
                    return !house.getRent().isRenewable();
                case OVERDUE:
                    return true;
                default:
                    return false;
            }
        }
    }
}
