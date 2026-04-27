package dataAccessLayer;

import DTO.AgreementDTO;
import enums.DeliveryDays;
import enums.DeliveryMethod;
import enums.AgreementStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgreementDaoSQLite implements AgreementDao {
    private final Connection conn;

    public AgreementDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public AgreementDTO findAgreementById(int agreement_id) throws SQLException {
        String sql = "SELECT * FROM Agreements WHERE agreement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agreement_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                int supplier_id = rs.getInt("supplier_id");
                DeliveryMethod deliveryMethod = DeliveryMethod.valueOf(rs.getString("delivery_method"));

                // Nested try-with-resources for the delivery days query
                String daySql = "SELECT day FROM Agreement_DeliveryDays WHERE agreement_id = ?";
                try (PreparedStatement dayStmt = conn.prepareStatement(daySql)) {
                    dayStmt.setInt(1, agreement_id);
                    try (ResultSet dayRes = dayStmt.executeQuery()) {
                        List<DeliveryDays> deliveryDays = new ArrayList<>();
                        while (dayRes.next()) {
                            deliveryDays.add(DeliveryDays.valueOf(dayRes.getString("day")));
                        }

                        return new AgreementDTO(
                                agreement_id,
                                supplier_id,
                                deliveryDays,
                                deliveryMethod,
                                AgreementStatus.ACTIVE
                        );
                    }
                }
            }
        }
    }


    @Override
    public List<AgreementDTO> findAll() throws SQLException {
        // Get a list of all agreements in the table
        List<AgreementDTO> list = new ArrayList<>();
        String sql = "SELECT agreement_id FROM Agreements";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AgreementDTO dto = findAgreementById(rs.getInt("agreement_id"));
                if (dto != null) list.add(dto);
            }
        }
        return list;
    }

    @Override
    public void update(AgreementDTO dto) throws SQLException {
        // Update main info in Agreements table
        String sql = "UPDATE Agreements SET supplier_id = ?, delivery_method = ? " +
                "WHERE agreement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.supplier_id());
            stmt.setString(2, dto.deliveryMethod().name());
            stmt.setInt(3, dto.agreement_id());
            stmt.executeUpdate();
        }

        // Remove old delivery days for this agreement
        String deleteSql = "DELETE FROM Agreement_DeliveryDays WHERE agreement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, dto.agreement_id());
            stmt.executeUpdate();
        }

        // Add new delivery days from the DTO
        for (DeliveryDays day : dto.deliveryDays()) {
            String insertSql = "INSERT INTO Agreement_DeliveryDays (agreement_id, day) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, dto.agreement_id());
                stmt.setString(2, day.name());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // First delete the delivery days of the agreement
        try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM Agreement_DeliveryDays WHERE agreement_id = ?");
             PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM Agreements WHERE agreement_id = ?")) {
            stmt1.setInt(1, id);
            stmt1.executeUpdate();

            // Then delete the agreement itself
            stmt2.setInt(1, id);
            stmt2.executeUpdate();
        }
    }


    @Override
    public void save(AgreementDTO dto) throws SQLException {
        // Insert a new agreement into the Agreements table
        String sql = "INSERT INTO Agreements (agreement_id, supplier_id, delivery_method) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.agreement_id());
            stmt.setInt(2, dto.supplier_id());
            stmt.setString(3, dto.deliveryMethod().name());
            stmt.executeUpdate();

            // Insert delivery days for this new agreement
            for (DeliveryDays day : dto.deliveryDays()) {
                String daySql = "INSERT INTO Agreement_DeliveryDays (agreement_id, day) VALUES (?, ?)";
                try (PreparedStatement dayStmt = conn.prepareStatement(daySql)) {
                    dayStmt.setInt(1, dto.agreement_id());
                    dayStmt.setString(2, day.name());
                    dayStmt.executeUpdate();
                }
            }
        }
    }

    @Override
    public List<Integer> productsInAgreement(int agreement_id) throws SQLException {
        // Return all product IDs connected to a given agreement
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT product_id FROM AgreementProducts WHERE agreement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agreement_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("product_id"));
            }
        }
        return result;
    }

    @Override
    public List<DeliveryDays> getDeliveryDays(int agreementId) throws SQLException {
        List<DeliveryDays> deliveryDays = new ArrayList<>();
        String sql = "SELECT day FROM Agreement_DeliveryDays WHERE agreement_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agreementId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dayStr = rs.getString("day");
                    deliveryDays.add(DeliveryDays.valueOf(dayStr));
                }
            }
        }

        return deliveryDays;
    }
}