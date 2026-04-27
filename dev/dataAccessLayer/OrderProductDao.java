package dataAccessLayer;

import DTO.OrderProductDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface OrderProductDao {
    void save(OrderProductDTO dto) throws SQLException;

    //return all OrderProduct related to order with given id
    List<OrderProductDTO> findByOrderId(int orderId) throws SQLException;

    // If order has only one product (usually for shortage/periodic orders), return this product as DTO
    OrderProductDTO findSingleByOrderId(int orderId) throws SQLException;

    //delete all orderProducts from order
    void deleteAllByOrderId(int orderId) throws SQLException;

    //delete an orderProduct from order
    void deleteOrderProductFromOrder(int orderId, String agreementProductId) throws SQLException;

    OrderProductDTO mapResultSetToDTO(ResultSet rs) throws SQLException;
}
