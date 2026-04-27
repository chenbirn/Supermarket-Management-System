package dataAccessLayer;

import DTO.ProductDto;
import enums.PackagingOption;
import enums.UnitType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoSQLite implements IProductDao {
    private final Connection conn;

    public ProductDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    // finding product by id
    @Override
    public ProductDto findById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";
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

    // find all products
    @Override
    public List<ProductDto> findAll() {
        List<ProductDto> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                products.add(toDto(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // saving a product
    @Override
    public void save(ProductDto dto) {
        String query = "INSERT INTO products (id, manufacturer, name, real_price, sale_price, weight, size_ml, " +
                "min_quantity, curr_quantity, packaging_option, unit_type, box_units, product_order_status, frequency, " +
                "main_category_name, sub_category_name, size_category_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            fillStatement(stmt, dto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // update a product
    @Override
    public void update(ProductDto dto) {
        String query = "UPDATE products SET manufacturer=?, name=?, real_price=?, sale_price=?, weight=?, size_ml=?," +
                "min_quantity=?, curr_quantity=?, packaging_option=?, unit_type=?, box_units=?, product_order_status=?," +
                "frequency=?, main_category_name=?, sub_category_name=?, size_category_name=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dto.manufacturer());
            stmt.setString(2, dto.name());
            stmt.setDouble(3, dto.realPrice());
            stmt.setDouble(4, dto.salePrice());
            stmt.setDouble(5, dto.weight());
            stmt.setDouble(6, dto.sizeML());
            stmt.setInt(7, dto.minQuantity());
            stmt.setInt(8, dto.currQuantity());
            stmt.setString(9, dto.packagingOption().name());
            stmt.setString(10, dto.unitType().name());
            stmt.setInt(11, dto.boxUnits());
            stmt.setString(12, "NOT_ORDERED"); // או dto.status().name() בעתיד
            stmt.setInt(13, dto.frequency());
            stmt.setString(14, dto.mainCategoryName());
            stmt.setString(15, dto.subCategoryName());
            stmt.setString(16, dto.sizeCategoryName());
            stmt.setInt(17, dto.id()); // WHERE id=?
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete a product
    @Override
    public void delete(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProductDto toDto(ResultSet rs) throws SQLException {
        return new ProductDto(
                rs.getInt("id"),
                rs.getString("manufacturer"),
                rs.getString("name"),
                rs.getDouble("real_price"),
                rs.getDouble("sale_price"),
                rs.getDouble("weight"),
                rs.getDouble("size_ml"),
                rs.getInt("min_quantity"),
                rs.getInt("curr_quantity"),
                rs.getInt("frequency"),
                new ArrayList<>(), // items
                new ArrayList<>(), // storeDiscounts
                PackagingOption.valueOf(rs.getString("packaging_option")),
                UnitType.valueOf(rs.getString("unit_type")),
                rs.getInt("box_units"),
                rs.getString("main_category_name"),
                rs.getString("sub_category_name"),
                rs.getString("size_category_name")
        );
    }

    @Override
    public void fillStatement(PreparedStatement stmt, ProductDto dto) throws SQLException {
        stmt.setInt(1, dto.id());
        stmt.setString(2, dto.manufacturer());
        stmt.setString(3, dto.name());
        stmt.setDouble(4, dto.realPrice());
        stmt.setDouble(5, dto.salePrice());
        stmt.setDouble(6, dto.weight());
        stmt.setDouble(7, dto.sizeML());
        stmt.setInt(8, dto.minQuantity());
        stmt.setInt(9, dto.currQuantity());
        stmt.setString(10, dto.packagingOption().name());
        stmt.setString(11, dto.unitType().name());
        stmt.setInt(12, dto.boxUnits());
        stmt.setString(13, "NOT_ORDERED"); // אפשר להחליף אם תוסיפי ל־DTO
        stmt.setInt(14, dto.frequency());
        stmt.setString(15, dto.mainCategoryName());
        stmt.setString(16, dto.subCategoryName());
        stmt.setString(17, dto.sizeCategoryName());
    }
}
