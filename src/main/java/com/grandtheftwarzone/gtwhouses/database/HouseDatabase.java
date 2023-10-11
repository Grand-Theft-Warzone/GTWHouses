package com.grandtheftwarzone.gtwhouses.database;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.dao.HouseBlock;
import com.grandtheftwarzone.gtwhouses.dao.HouseRent;
import me.phoenixra.atum.core.database.Database;
import me.phoenixra.atum.core.database.SQLiteDatabase;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class HouseDatabase {
    private final Database database;

    public static final SimpleDateFormat sqliteDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HouseDatabase(GTWHouses plugin) {
        database = new SQLiteDatabase(plugin, "houses", "plugins/GTWHouses");

        if (!database.checkConnection()) {
            plugin.getLogger().severe("Could not connect to the database! Disabling plugin...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        plugin.getLogger().info("Connected to the database!");
        if (!database.existsTable("houses")) {
            String sql;
            try {
                sql = getSQL("houses.sql", plugin);

                plugin.getLogger().info("Creating tables ...");

                //Unfortunately JDBC can't run multiple statements at once
                for (String statement : sql.split(";")) database.execute(statement);
                database.execute("PRAGMA case_sensitive_like = 0;\n");

                plugin.getLogger().info("Tables created!");
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().severe("Could not create table 'houses'! Disabling plugin...");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }

        }
    }

    private String getSQL(String name, GTWHouses plugin) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(plugin.getResource(name));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        for (int result = stream.read(); result != -1; result = stream.read()) {
            buffer.write((byte) result);
        }

        return buffer.toString("UTF-8");
    }


    public boolean hasName(String houseName) {
        try {
            ResultSet rs = database.select("SELECT COUNT(id) as houseCount FROM houses WHERE name = '" + houseName + "';");
            if (rs == null || !rs.next()) return false;
            return rs.getInt("houseCount") > 0;
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }

        return true;
    }

    public boolean registerHouse(House house) {
        try {
            database.getConnection().setAutoCommit(false);

            PreparedStatement ps = database.getConnection().prepareStatement("INSERT INTO houses " +
                    "(name, world_uuid, minX, minY, minZ, maxX, maxY, maxZ, owner_uuid, base_buy_cost, base_rent_cost, sell_cost) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            ps.setString(1, house.getName());
            ps.setString(2, house.getWorld().toString());
            ps.setInt(3, house.getMinPos().getBlockX());
            ps.setInt(4, house.getMinPos().getBlockY());
            ps.setInt(5, house.getMinPos().getBlockZ());
            ps.setInt(6, house.getMaxPos().getBlockX());
            ps.setInt(7, house.getMaxPos().getBlockY());
            ps.setInt(8, house.getMaxPos().getBlockZ());
            ps.setString(9, house.getOwner() == null ? null : house.getOwner().toString());
            ps.setDouble(10, house.getDefaultCost());
            ps.setDouble(11, house.getDefaultRentConst());
            ps.setDouble(12, house.getSellCost());

            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int houseId = -1;
            if (generatedKeys.next()) houseId = generatedKeys.getInt(1);

            PreparedStatement rentPs = database.getConnection().prepareStatement("INSERT INTO rent (house_id, renter_uuid, rent_cost, rented_at, days_rented, renewable) VALUES (?, ?, ?, ?, ?, ?);");

            rentPs.setInt(1, houseId);
            rentPs.setString(2, null);
            rentPs.setDouble(3, house.getRent().getCostPerDay());
            rentPs.setString(4, null/*sqliteDateFormat.format(house.getRent().getRentedAt())*/);
            rentPs.setInt(5, house.getRent().getDaysRented());
            rentPs.setInt(6, 1);

            rentPs.executeUpdate();

            PreparedStatement blockPs = database.getConnection().prepareStatement("INSERT INTO house_blocks (house_id, x, y, z) VALUES (?, ?, ?, ?);");

            for (HouseBlock block : house.getBlocks()) {
                blockPs.setInt(1, houseId);
                blockPs.setInt(2, block.getX());
                blockPs.setInt(3, block.getY());
                blockPs.setInt(4, block.getZ());
                blockPs.addBatch();
            }

            blockPs.executeBatch();

            database.getConnection().commit();

            rentPs.close();
            blockPs.close();
            ps.close();

            house.setId(houseId);
            GTWHouses.getHouseCache().updateHouse(house);

            return true;
        } catch (SQLException ignored) {
            ignored.printStackTrace();
            try {
                database.getConnection().rollback();
            } catch (SQLException ignored2) {
            }
        } finally {
            try {
                database.getConnection().setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    public List<House> getHouses() {
        return getHouses("SELECT * FROM houses;");
    }

    public List<House> getPlayerOwnedHouses(UUID player) {
        return getHouses("SELECT * FROM houses WHERE owner_uuid = '" + player.toString() + "';");
    }

    public List<House> getPlayerRenterHouses(UUID player) {
        return getHouses("SELECT * FROM houses, rent WHERE id = rent.house_id AND rent.renter_uuid = '" + player.toString() + "';");
    }

    public House getHouseByName(String name) {
        List<House> houses = getHouses("SELECT * FROM houses WHERE name = '" + name + "';");
        return houses.isEmpty() ? null : houses.get(0);
    }

    public House getHouseById(int id) {
        return getHouses("SELECT * FROM houses WHERE id = " + id + ";").get(0);
    }

    private List<House> getHouses(String query) {
        ArrayList<House> houses = new ArrayList<>();
        try {
            ResultSet rs = database.select(query);
            if (rs == null) return houses;

            while (rs.next()) {
                int id = rs.getInt("id");
                houses.add(new House(rs, getRent(id), getBlocks(id)));
            }

            rs.close();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }

        return houses;
    }

    public HouseRent getRent(int houseId) {
        HouseRent rent = null;
        try {
            ResultSet rs = database.select("SELECT * FROM rent WHERE house_id = " + houseId + ";");
            if (rs == null) return null;
            if (rs.next()) rent = new HouseRent(rs);
            rs.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return rent;
    }

    public List<HouseBlock> getBlocks(int houseId) {
        ArrayList<HouseBlock> blocks = new ArrayList<>();

        try {
            ResultSet rs = database.select("SELECT * FROM house_blocks WHERE house_id = " + houseId + ";");
            if (rs == null) return blocks;
            while (rs.next()) blocks.add(new HouseBlock(rs));
            rs.close();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }

        return blocks;
    }

    public boolean updateHouseOwner(House house, UUID uniqueId) {
        house.setOwner(uniqueId);
        GTWHouses.getInstance().getLogger().info("Updating house owner");
        try {
            database.getConnection().setAutoCommit(false);

            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE houses SET owner_uuid = ?, sell_cost = -1 WHERE id = ?;");
            ps.setString(1, uniqueId.toString());
            ps.setInt(2, house.getId());

            ps.executeUpdate();

            PreparedStatement rentPs = database.getConnection().prepareStatement(
                    (house.isRentable() && house.getRent().isRented()
                            ? "UPDATE rent SET renewable = 0 WHERE house_id = ?;"
                            : "DELETE FROM rent WHERE house_id = ?;"));

            rentPs.setInt(1, house.getId());

            rentPs.executeUpdate();

            database.getConnection().commit();
            database.getConnection().setAutoCommit(true);

            ps.close();
            rentPs.close();

            GTWHouses.getHouseCache().updateHouse(house);

            return true;
        } catch (SQLException e) {
            GTWHouses.getInstance().getLogger().severe("Could not update house owner!");
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeHouse(int houseId) {
        try {
            database.getConnection().setAutoCommit(false);

            PreparedStatement ps = database.getConnection().prepareStatement("DELETE FROM houses WHERE id = ?;");
            ps.setInt(1, houseId);
            ps.executeUpdate();

            PreparedStatement rentPs = database.getConnection().prepareStatement("DELETE FROM rent WHERE house_id = ?;");
            rentPs.setInt(1, houseId);
            rentPs.executeUpdate();


            PreparedStatement blockPs = database.getConnection().prepareStatement("DELETE FROM house_blocks WHERE house_id = ?;");
            blockPs.setInt(1, houseId);
            blockPs.executeUpdate();

            database.getConnection().commit();
            database.getConnection().setAutoCommit(true);

            GTWHouses.getHouseCache().removeHouse(houseId);

            ps.close();
            rentPs.close();
            blockPs.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setRentable(House house, double costPerDay) {
        house.setRentable(costPerDay);
        try {
            database.getConnection().setAutoCommit(false);

            PreparedStatement ps = database.getConnection().prepareStatement("DELETE FROM rent WHERE house_id = ?;");
            ps.setInt(1, house.getId());
            ps.executeUpdate();

            PreparedStatement rentPs = database.getConnection().prepareStatement("INSERT INTO rent (house_id, renter_uuid, rent_cost, rented_at, days_rented, renewable) VALUES (?, NULL, ?, NULL, -1, 1);");
            rentPs.setInt(1, house.getId());
            rentPs.setDouble(2, costPerDay);
            rentPs.executeUpdate();

            database.getConnection().commit();
            database.getConnection().setAutoCommit(true);

            GTWHouses.getHouseCache().updateHouse(house);

            ps.close();
            rentPs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean startRent(House house, UUID uniqueId, int rentDays) {
        house.getRent().startRent(uniqueId, rentDays);
        boolean res = database.execute("UPDATE rent SET renter_uuid = '" + uniqueId.toString() + "', rented_at = '" + sqliteDateFormat.format(house.getRent().getRentedAt()) + "', days_rented = " + house.getRent().getDaysRented() + ", renewable = 1  WHERE house_id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean resetHouse(House house, boolean rentable) {
        house.setOwner(null);
        if (rentable) house.resetRent();

        try {
            database.getConnection().setAutoCommit(false);

            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE houses SET owner_uuid = NULL, sell_cost = -1 WHERE id = ?;");
            ps.setInt(1, house.getId());
            ps.executeUpdate();

            PreparedStatement rentPs = database.getConnection().prepareStatement("DELETE FROM rent WHERE house_id = ?;");
            rentPs.setInt(1, house.getId());
            rentPs.executeUpdate();

            if (rentable) {
                PreparedStatement newRentPs = database.getConnection().prepareStatement("INSERT INTO rent (house_id, renter_uuid, rent_cost, rented_at, days_rented, renewable) VALUES (?, NULL, ?, NULL, -1, 1);");
                newRentPs.setInt(1, house.getId());
                newRentPs.setDouble(2, house.getDefaultRentConst());
                newRentPs.executeUpdate();
                newRentPs.close();
            }

            database.getConnection().commit();
            database.getConnection().setAutoCommit(true);

            GTWHouses.getHouseCache().updateHouse(house);

            ps.close();
            rentPs.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSellable(House house, double cost) {
        house.setSellable(cost);
        boolean res = database.execute("UPDATE houses SET sell_cost = " + cost + " WHERE id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean setUnsellable(House house) {
        house.setSellable(-1);
        boolean res = database.execute("UPDATE houses SET sell_cost = -1 WHERE id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean setUnrentable(House house) {
        // Check if it's currently rented if so make it not renewable
        //otherwise delete the rent
        boolean res = house.getRent() != null && house.getRent().isRented()
                ? database.execute("UPDATE rent SET renewable = 0 WHERE house_id = " + house.getId() + ";")
                : database.execute("DELETE FROM rent WHERE house_id = " + house.getId() + ";");
        house.setUnrentable();
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean setPayedRent(House house, int rentDays) {
        house.getRent().renewRent(rentDays);
        boolean res = database.execute("UPDATE rent SET rented_at = DATE('now'), days_rented = " + house.getRent().getDaysRented() + " WHERE house_id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean stopRent(House house) {
        house.getRent().stopRent();
        boolean res = database.execute("DELETE FROM rent WHERE house_id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }
}
