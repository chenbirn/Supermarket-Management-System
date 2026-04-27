package dataAccessLayer;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import enums.*;
import DTO.OrderDTO;

public class OrderDaoSQLite implements OrderDao {
    private final Connection conn;

    public OrderDaoSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public OrderDTO findById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<OrderDTO> findAll() throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapResultSetToDTO(rs));
            }
        }
        return orders;
    }

    @Override
    public List<OrderDTO> findAllPeriodicOrders() throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE order_type = 'periodic'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapResultSetToDTO(rs));
            }
        }
        return orders;
    }

    @Override
    public void save(OrderDTO dto) throws SQLException {
        String sql = """
                    INSERT INTO orders (
                        order_id, order_date, delivery_date, contact_num,
                        total_price, status, supplier_id, order_type, frequency
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.order_id());
            stmt.setString(2, dto.orderDate().toString());
            stmt.setString(3, dto.deliveryDate() != null ? dto.deliveryDate().toString() : null);
            stmt.setString(4, dto.contactNum());
            stmt.setDouble(5, dto.totalPrice());
            stmt.setString(6, dto.status().name());
            stmt.setInt(7, dto.supplier_id());
            stmt.setString(8, dto.order_type().name());

            if (dto.order_type() == OrderType.PERIODIC) {
                stmt.setInt(9, dto.frequency());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }


    @Override
    public void update(OrderDTO dto) throws SQLException {
        String sql = "UPDATE orders SET order_date = ?, delivery_date = ?, contact_num = ?, total_price = ?, status = ?, supplier_id = ?, order_type = ?, frequency = ? WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.orderDate().toString());
            stmt.setString(2, dto.deliveryDate() != null ? dto.deliveryDate().toString() : null);
            stmt.setString(3, dto.contactNum());
            stmt.setDouble(4, dto.totalPrice());
            stmt.setString(5, dto.status().name());
            stmt.setInt(6, dto.supplier_id());
            stmt.setString(7, dto.order_type().name());
            if (dto.order_type() == OrderType.PERIODIC) {
                stmt.setInt(8, dto.frequency());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.setInt(9, dto.order_id());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    @Override
    public OrderDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int id = rs.getInt("order_id");
        LocalDate orderDate = LocalDate.parse(rs.getString("order_date"));
        String deliveryStr = rs.getString("delivery_date");
        LocalDate deliveryDate = deliveryStr != null ? LocalDate.parse(deliveryStr) : null;
        String contactNum = rs.getString("contact_num");
        double totalPrice = rs.getDouble("total_price");
        OrderStatus status = OrderStatus.valueOf(rs.getString("status"));
        int supplierId = rs.getInt("supplier_id");
        OrderType type = OrderType.valueOf(rs.getString("order_type"));
        Integer frequency = rs.getInt("frequency");

        return new OrderDTO(id, orderDate, contactNum, totalPrice, status, supplierId, deliveryDate, frequency, type);
    }
}
