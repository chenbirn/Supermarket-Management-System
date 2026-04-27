package dataAccessLayer;

import DTO.OrderDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
    OrderDTO findById(int orderId) throws SQLException;

    List<OrderDTO> findAll() throws SQLException;

    List<OrderDTO> findAllPeriodicOrders() throws SQLException;

    void save(OrderDTO dto) throws SQLException;

    void update(OrderDTO dto) throws SQLException;

    void delete(int orderId) throws SQLException;

    OrderDTO mapResultSetToDTO(ResultSet rs) throws SQLException;
}
