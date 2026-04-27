package dataAccessLayer;

import DTO.SupplierDTO;
import enums.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class SupplierDaoSQLite implements SupplierDao {
    private final Connection conn;

    public SupplierDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public SupplierDTO findById(int id) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs);
            } else {
                throw new SQLException("Supplier not found with ID: " + id);
            }
        }
    }

    @Override
    public List<SupplierDTO> findAll() throws SQLException {
        List<SupplierDTO> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(mapResultSetToDTO(rs));
            }
        }

        return suppliers;
    }

    @Override
    public void save(SupplierDTO dto) throws SQLException {
        String sql = """
                    INSERT INTO suppliers (
                        supplier_id, name, address, payment_type, bank_account, status,
                        contact_name1, contact_phone1, contact_name2, contact_phone2
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.supplier_id());
            stmt.setString(2, dto.name());
            stmt.setString(3, dto.address());
            stmt.setString(4, paymentTypesToString(dto.paymentType()));
            stmt.setInt(5, dto.bankAccount());
            stmt.setString(6, dto.supplierStatus().name());
            stmt.setString(7, dto.contact1());
            stmt.setString(8, dto.phone_number1());
            stmt.setString(9, dto.contact2());
            stmt.setString(10, dto.phone_number2());

            stmt.executeUpdate();
        }
    }

    @Override
    public void update(SupplierDTO dto) throws SQLException {
        String sql = """
                    UPDATE suppliers
                    SET name = ?, address = ?, payment_type = ?, bank_account = ?, status = ?,
                        contact_name1 = ?, contact_phone1 = ?, contact_name2 = ?, contact_phone2 = ?
                    WHERE supplier_id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.name());
            stmt.setString(2, dto.address());
            stmt.setString(3, paymentTypesToString(dto.paymentType()));
            stmt.setInt(4, dto.bankAccount());
            stmt.setString(5, dto.supplierStatus().name());

            stmt.setString(6, dto.contact1());
            stmt.setString(7, dto.phone_number1());
            stmt.setString(8, dto.contact2());
            stmt.setString(9, dto.phone_number2());

            stmt.setInt(10, dto.supplier_id());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.executeUpdate();
        }
    }

    @Override
    public SupplierDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int supplierId = rs.getInt("supplier_id");
        String name = rs.getString("name");
        String address = rs.getString("address");
        EnumSet<PaymentType> paymentType = parsePaymentTypes(rs.getString("payment_type"));
        int bankAccount = rs.getInt("bank_account");
        SupplierStatus status = SupplierStatus.valueOf(rs.getString("status"));
        String contact1 = rs.getString("contact_name1");
        String phone1 = rs.getString("contact_phone1");
        String contact2 = rs.getString("contact_name2");
        String phone2 = rs.getString("contact_phone2");

        return new SupplierDTO(supplierId, name, address, paymentType, bankAccount, status, contact1, phone1, contact2, phone2);
    }

    //convert text to set of payment types
    private EnumSet<PaymentType> parsePaymentTypes(String paymentStr) {

        EnumSet<PaymentType> set = EnumSet.noneOf(PaymentType.class);
        String[] parts = paymentStr.split(",");

        for (String part : parts) {
            set.add(PaymentType.valueOf(part.trim()));
        }

        return set;
    }

    //convert set of payment types to String
    public String paymentTypesToString(EnumSet<PaymentType> types) {
        return types.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
