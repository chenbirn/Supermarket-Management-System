package dataAccessLayer;

import DTO.AgreementProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgreementProductDaoSQLite implements AgreementProductDao {
    private final Connection conn;

    // Constructor - gets the database connection to use for queries
    public AgreementProductDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(AgreementProductDTO dto) throws SQLException {
        // Insert a new row into the AgreementProducts table
        String sql = "INSERT INTO AgreementProducts (supplyItem_id, agreement_id, product_id, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.supplyItem_id());
            stmt.setInt(2, dto.agreement_id());
            stmt.setInt(3, dto.product_id());
            stmt.setDouble(4, dto.price());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(String supplyItem_id) throws SQLException {
        // Delete a product from the agreement using its supplyItem_id
        String sql = "DELETE FROM AgreementProducts WHERE supplyItem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplyItem_id);
            stmt.executeUpdate();
        }
    }

    @Override
    public AgreementProductDTO findBySupplyItem_id(String supplyItem_id) throws SQLException {
        // Find and return a product from the agreement by supplyItem_id
        String sql = "SELECT * FROM AgreementProducts WHERE supplyItem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplyItem_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Create and return a DTO object from the result
                return new AgreementProductDTO(
                        rs.getString("supplyItem_id"),
                        rs.getInt("product_id"),
                        rs.getInt("agreement_id"),
                        rs.getDouble("price")
                );
            }
            return null;  // If not found, return null
        }
    }

    @Override
    public List<AgreementProductDTO> findAllByAgreement(int agreement_id) throws SQLException {
        // Get all products that are part of a specific agreement
        List<AgreementProductDTO> result = new ArrayList<>();
        String sql = "SELECT * FROM AgreementProducts WHERE agreement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agreement_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new AgreementProductDTO(
                        rs.getString("supplyItem_id"),
                        rs.getInt("product_id"),
                        rs.getInt("agreement_id"),
                        rs.getDouble("price")
                ));
            }
        }
        return result;
    }

    @Override
    public void update(AgreementProductDTO dto) throws SQLException {
        // Update the data of an existing agreement product
        String sql = "UPDATE AgreementProducts SET agreement_id = ?, product_id = ?, price = ? WHERE supplyItem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.agreement_id());
            stmt.setInt(2, dto.product_id());
            stmt.setDouble(3, dto.price());
            stmt.setString(4, dto.supplyItem_id());
            stmt.executeUpdate();
        }
    }


}
