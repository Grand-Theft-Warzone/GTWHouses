package com.grandtheftwarzone.gtwhouses.database;

import com.grandtheftwarzone.gtwhouses.GTWHouses;
import com.grandtheftwarzone.gtwhouses.dao.House;
import com.grandtheftwarzone.gtwhouses.dao.HouseBlock;
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
                    "(name, world_uuid, minX, minY, minZ, maxX, maxY, maxZ, owner_uuid, buy_cost, rent_cost, rented_at, rent_due, sell_cost) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            ps.setString(1, house.getName());
            ps.setString(2, house.getWorld().toString());
            ps.setInt(3, house.getMinPos().getBlockX());
            ps.setInt(4, house.getMinPos().getBlockY());
            ps.setInt(5, house.getMinPos().getBlockZ());
            ps.setInt(6, house.getMaxPos().getBlockX());
            ps.setInt(7, house.getMaxPos().getBlockY());
            ps.setInt(8, house.getMaxPos().getBlockZ());
            ps.setString(9, house.getOwner() == null ? null : house.getOwner().toString());
            ps.setDouble(10, house.getBuyCost());
            ps.setDouble(11, house.getRentCost());
            ps.setDate(12, /*house.getRentedAt()*/ null);
            ps.setDate(13, /*house.getRentDueDate()*/ null);
            ps.setDouble(14, house.getSellCost());

            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int houseId = -1;
            if (generatedKeys.next()) houseId = generatedKeys.getInt(1);

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
                houses.add(new House(rs, getBlocks(id)));
            }

            rs.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return houses;
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

            PreparedStatement ps = database.getConnection()
                    .prepareStatement("UPDATE houses SET owner_uuid = ?, rented_at = ?, rent_due = ?, sell_cost = ? WHERE id = ?;");
            ps.setString(1, uniqueId.toString());
            ps.setDate(2, null);
            ps.setDate(3, null);
            ps.setDouble(4, -1);
            ps.setInt(5, house.getId());

            ps.executeUpdate();
            ps.close();

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

            PreparedStatement blockPs = database.getConnection().prepareStatement("DELETE FROM house_blocks WHERE house_id = ?;");
            blockPs.setInt(1, houseId);
            blockPs.executeUpdate();

            database.getConnection().commit();
            database.getConnection().setAutoCommit(true);

            GTWHouses.getHouseCache().removeHouse(houseId);

            ps.close();
            blockPs.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean startRent(House house) {
        house.startRent();
        try {
            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE house SET rented_at = ? , rent_due = ?  WHERE house_id = " + house.getId() + ";");
            ps.setString(1, sqliteDateFormat.format(house.getRentedAt()));
            ps.setString(2, sqliteDateFormat.format(house.getRentDueDate()));
            ps.executeUpdate();
            ps.close();
            GTWHouses.getHouseCache().updateHouse(house);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetHouse(House house) {
        house.setOwner(null);
        house.resetRent();

        try {
            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE houses SET owner_uuid = NULL, rented_at = NULL, rent_due = NULL, sell_cost = -1 WHERE id = ?;");
            ps.setInt(1, house.getId());
            ps.executeUpdate();

            GTWHouses.getHouseCache().updateHouse(house);

            ps.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSellable(House house, double cost) {
        house.setSellCost(cost);
        boolean res = database.execute("UPDATE houses SET sell_cost = " + cost + " WHERE id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean setUnsellable(House house) {
        house.setSellCost(-1);
        boolean res = database.execute("UPDATE houses SET sell_cost = -1 WHERE id = " + house.getId() + ";");
        if (res) GTWHouses.getHouseCache().updateHouse(house);
        return res;
    }

    public boolean payRent(House house) {
        house.renewRent();
        try {
            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE houses SET rent_due = ? WHERE house_id = " + house.getId() + ";");
            ps.setString(1, sqliteDateFormat.format(house.getRentDueDate()));
            ps.executeUpdate();
            ps.close();
            GTWHouses.getHouseCache().updateHouse(house);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopRent(House house) {
        house.resetRent();

        try {
            PreparedStatement ps = database.getConnection().prepareStatement("UPDATE houses SET rented_at = ?, rent_due = ? WHERE house_id = " + house.getId() + ";");
            ps.setString(1, null);
            ps.setString(2, null);
            ps.executeUpdate();
            ps.close();
            GTWHouses.getHouseCache().updateHouse(house);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
