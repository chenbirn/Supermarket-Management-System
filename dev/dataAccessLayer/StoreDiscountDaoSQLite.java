package dataAccessLayer;

import DTO.StoreDiscountDTO;
import enums.DiscountType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StoreDiscountDaoSQLite implements StoreDiscountDao {
    private final Connection conn;

    public StoreDiscountDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public StoreDiscountDTO findById(int id) {
        String query = "SELECT * FROM store_discounts WHERE id = ?";
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

    @Override
    public List<StoreDiscountDTO> findAll() {
        List<StoreDiscountDTO> discounts = new ArrayList<>();
        String query = "SELECT * FROM store_discounts";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                discounts.add(toDto(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discounts;
    }

    @Override
    public void save(StoreDiscountDTO dto) {
        String query = "INSERT INTO store_discounts " +
                "(id, product_id, main_category_name, sub_category_name, size_category_name, percent_discount, category_or_product, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            fillStatement(stmt, dto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(StoreDiscountDTO dto) {
        String query = "UPDATE store_discounts SET " +
                "product_id=?, main_category_name=?, sub_category_name=?, size_category_name=?, " +
                "percent_discount=?, category_or_product=?, start_date=?, end_date=? " +
                "WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, dto.productId());
            stmt.setString(2, dto.mainCategoryName());
            stmt.setString(3, dto.subCategoryName());
            stmt.setString(4, dto.sizeCategoryName());
            stmt.setDouble(5, dto.percentDiscount());
            stmt.setString(6, dto.categoryOrProduct().name());
            stmt.setString(7, dto.startDate().toString());
            stmt.setString(8, dto.endDate().toString());
            stmt.setInt(9, dto.discountId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM store_discounts WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public StoreDiscountDTO toDto(ResultSet rs) throws SQLException {
        return new StoreDiscountDTO(
                rs.getInt("id"),
                rs.getDouble("percent_discount"),
                DiscountType.valueOf(rs.getString("category_or_product")),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("end_date")),
                rs.getObject("product_id") != null ? rs.getInt("product_id") : null,
                rs.getString("main_category_name"),
                rs.getString("sub_category_name"),
                rs.getString("size_category_name")
        );
    }

    @Override
    public void fillStatement(PreparedStatement stmt, StoreDiscountDTO dto) throws SQLException {
        stmt.setInt(1, dto.discountId());

        if (dto.productId() != null) {
            stmt.setInt(2, dto.productId());
        } else {
            stmt.setNull(2, Types.INTEGER);
        }

        stmt.setString(3, dto.mainCategoryName());
        stmt.setString(4, dto.subCategoryName());
        stmt.setString(5, dto.sizeCategoryName());
        stmt.setDouble(6, dto.percentDiscount());
        stmt.setString(7, dto.categoryOrProduct().name());
        stmt.setString(8, dto.startDate().toString());
        stmt.setString(9, dto.endDate().toString());
    }

    @Override
    public List<StoreDiscountDTO> findByProductId(int productId) {
        List<StoreDiscountDTO> discounts = new ArrayList<>();
        String query = "SELECT * FROM store_discounts WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                discounts.add(toDto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discounts;
    }
}
