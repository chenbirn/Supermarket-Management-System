package dataAccessLayer;

import DTO.OrderProductDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProductDAOSQLite implements OrderProductDao {

    private final Connection conn;

    public OrderProductDAOSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(OrderProductDTO dto) throws SQLException {
        String sql = """
                    INSERT INTO order_products (
                        quantity, price, use_discount, discount, final_price,
                        order_id, supplier_product_id
                    ) VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.quantity());
            stmt.setDouble(2, dto.price());
            stmt.setBoolean(3, dto.useDiscount());
            stmt.setDouble(4, dto.discount());
            stmt.setDouble(5, dto.finalPrice());
            stmt.setInt(6, dto.order_id());
            stmt.setString(7, dto.supplier_ProductId());
            stmt.executeUpdate();
        }
    }


    //return all OrderProduct related to order with given id
    @Override
    public List<OrderProductDTO> findByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM order_products WHERE order_id = ?";
        List<OrderProductDTO> products = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToDTO(rs));
            }
        }

        return products;
    }


    // If order has only one product (usually for shortage/periodic orders), return this product as DTO
    @Override
    public OrderProductDTO findSingleByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM order_products WHERE order_id = ? LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs);
            } else {
                throw new SQLException("No order product found for order_id " + orderId);
            }
        }
    }

    //delete all orderProducts from order
    @Override
    public void deleteAllByOrderId(int orderId) throws SQLException {
        String sql = "DELETE FROM order_products WHERE order_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    //delete an orderProduct from order
    @Override
    public void deleteOrderProductFromOrder(int orderId, String agreementProductId) throws SQLException {
        String sql = "DELETE FROM order_products WHERE order_id = ? AND agreement_product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setString(2, agreementProductId);
            stmt.executeUpdate();
        }
    }

    @Override
    public OrderProductDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int quantity = rs.getInt("quantity");
        double price = rs.getDouble("price");
        boolean useDiscount = rs.getBoolean("use_discount");
        double discount = rs.getDouble("discount");
        double finalPrice = rs.getDouble("final_price");
        int orderId = rs.getInt("order_id");
        String agreementProductId = rs.getString("supplier_product_id");

        return new OrderProductDTO(
                quantity,
                price,
                useDiscount,
                discount,
                finalPrice,
                orderId,
                agreementProductId
        );
    }
}
