package domainLayer;

import DTO.OrderDTO;
import enums.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface IOrderRepository {

    Order createOrder(String contactNum, Supplier supplier) throws SQLException;

    PeriodicOrder createPeriodicOrder(Product product, Collection<Supplier> suppliers) throws SQLException;

    PeriodicOrder recreatePeriodicOrder(Product product, Supplier supplier) throws SQLException;

    ShortageOrder createShortageOrder(Product product, Collection<Supplier> suppliers) throws SQLException;

    Order findOrderById(int id) throws SQLException;

    List<Order> findOrdersByDates(LocalDate startDate, LocalDate endDate) throws SQLException;

    List<Order> findOrdersByProductId(int id) throws SQLException;

    List<Order> findOrdersByProductSupId(String id) throws SQLException;

    List<Order> findAllOrders() throws SQLException;

    int findMaxId() throws SQLException;

    List<PeriodicOrder> findAllPeriodicOrders() throws SQLException;

    List<Order> findAllOrdersBySupplier(int supplier_id) throws SQLException;

    void changeOrderStatus(int id, OrderStatus status) throws SQLException;

    List<PeriodicOrder> checkAllPeriodicOrders(Collection<Supplier> suppliers) throws SQLException;

    public void setOrderDeliveryDate(int order_id, LocalDate deliveryDate) throws SQLException;

    public LocalDate getDeliveryDate(int order_id) throws SQLException;
}
