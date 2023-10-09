package com.grandtheftwarzone.gtwhouses.util;

import com.grandtheftwarzone.gtwhouses.database.HouseDatabase;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

@Getter
public class HouseRent implements Serializable {

    private final double costPerDay;
    private int daysRented;
    private Date rentedAt;
    private RentStatus status;
    private UUID renter;
    private boolean renewable;

    public HouseRent(ResultSet rs) throws Exception {
        this.costPerDay = rs.getDouble("rent_cost");
        this.daysRented = rs.getInt("days_rented");
        String rentedAtString = rs.getString("rented_at");
        this.rentedAt = rentedAtString != null ? HouseDatabase.sqliteDateFormat.parse(rentedAtString) : null;
        String renterString = rs.getString("renter_uuid");
        this.renter = renterString != null ? UUID.fromString(renterString): null;
        this.renewable = rs.getBoolean("renewable");
        this.status = RentStatus.fromDays(HouseUtils.getRemainingRentDays(this));
    }

    public HouseRent(double costPerDay) {
        this.costPerDay = costPerDay;
        this.daysRented = -1;
        this.rentedAt = null;
        this.renter = null;
        this.status = RentStatus.NOT_RENTED;
        this.renewable = true;
    }

    public void startRent(UUID renter, int daysDuration) {
        this.renter = renter;
        this.daysRented = daysDuration;
        this.rentedAt = new Date();
        this.status = RentStatus.RENTED;
        this.renewable = true;
    }

    public void renewRent(int daysDuration) {
        this.daysRented += daysDuration;
        this.rentedAt = new Date();
        this.status = RentStatus.RENTED;
        this.renewable = true;
    }

    public void stopRent() {
        this.renter = null;
        this.daysRented = -1;
        this.rentedAt = null;
        this.status = RentStatus.NOT_RENTED;
        this.renewable = true;
    }

    public boolean isRented() {
        return daysRented != -1 || rentedAt != null || renter != null;
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
