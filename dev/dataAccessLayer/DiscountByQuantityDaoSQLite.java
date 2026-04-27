package dataAccessLayer;

import DTO.DiscountByQuantityDTO;
import enums.DiscountMethod;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountByQuantityDaoSQLite implements DiscountByQuantityDao {
    private final Connection conn;

    // Constructor - gets the database connection
    public DiscountByQuantityDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(DiscountByQuantityDTO dto) throws SQLException {
        // Insert a new discount rule into the database
        String sql = "INSERT INTO DiscountByQuantity (supplyItem_id, discount, quantity, Dtype) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.supplyItem_id());
            stmt.setDouble(2, dto.discount());
            stmt.setInt(3, dto.quantity());
            stmt.setString(4, dto.dtype().name());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<DiscountByQuantityDTO> findBySupplyItem_id(String supplyItem_id) throws SQLException {
        List<DiscountByQuantityDTO> discounts = new ArrayList<>();

        // Get the discount rule for a specific item
        String sql = "SELECT * FROM DiscountByQuantity WHERE supplyItem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplyItem_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                discounts.add(new DiscountByQuantityDTO(
                        rs.getString("supplyItem_id"),
                        rs.getDouble("discount"),
                        rs.getInt("quantity"),
                        DiscountMethod.valueOf(rs.getString("Dtype"))
                ));
            }
        }
        return discounts.isEmpty() ? null : discounts;
    }

    @Override
    public void update(DiscountByQuantityDTO dto) throws SQLException {
        // Update an existing discount rule in the database
        String sql = "UPDATE DiscountByQuantity SET discount = ?, quantity = ?, Dtype = ? WHERE supplyItem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, dto.discount());
            stmt.setInt(2, dto.quantity());
            stmt.setString(3, dto.dtype().name());
            stmt.setString(4, dto.supplyItem_id());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(String supplyItem_id) throws SQLException {
        // Delete a discount rule by item ID
        String sql = "DELETE FROM DiscountByQuantity WHERE supplyItem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplyItem_id);
            stmt.executeUpdate();
        }
    }
}