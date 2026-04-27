package dataAccessLayer;

import DTO.ItemDTO;
import enums.DefectiveStatus;
import enums.location;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoSQLite implements ItemDao {
    private final Connection conn;

    public ItemDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    // find item by id
    @Override
    public ItemDTO findById(int id) {
        String query = "SELECT * FROM items WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return toDto(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // find all items
    @Override
    public List<ItemDTO> findAll() {
        List<ItemDTO> items = new ArrayList<>();
        String query = "SELECT * FROM items";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                items.add(toDto(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // save an item
    @Override
    public void save(ItemDTO dto) {
        String query = "INSERT INTO items (id, buy_price, expiration_date, status, location, product_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            fillStatement(stmt, dto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // update an item
    @Override
    public void update(ItemDTO dto) {
        String query = "UPDATE items SET buy_price=?, expiration_date=?, status=?, location=?, product_id=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            fillStatement(stmt, dto);
            stmt.setInt(6, dto.ItemId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete an item
    @Override
    public void delete(int id) {
        String query = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemDTO toDto(ResultSet rs) throws SQLException {
        return new ItemDTO(
                rs.getInt("id"),
                rs.getDouble("buy_price"),
                LocalDate.parse(rs.getString("expiration_date")),
                DefectiveStatus.valueOf(rs.getString("status")),
                location.valueOf(rs.getString("location")),
                rs.getInt("product_id")
        );
    }

    @Override
    public void fillStatement(PreparedStatement stmt, ItemDTO dto) throws SQLException {
        stmt.setInt(1, dto.ItemId());
        stmt.setDouble(2, dto.BuyPrice());
        stmt.setString(3, dto.ExpirationDate().toString());
        stmt.setString(4, dto.Status().name());
        stmt.setString(5, dto.location().name());
        stmt.setInt(6, dto.productId());
    }
}
