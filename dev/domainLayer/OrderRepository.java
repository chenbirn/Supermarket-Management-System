package domainLayer;

import dataAccessLayer.*;
import enums.OrderStatus;
import enums.SupplierStatus;
import DTO.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class OrderRepository implements IOrderRepository {
    private final Map<Integer, Order> orderStorage = new HashMap<>();
    private final ISupplierRepository supplierRepository;
    private final IProductRepository productRepository;
    private final OrderDaoSQLite orderDAO;
    private final OrderProductDAOSQLite orderProductDAO;
    private final AgreementProductDaoSQLite agreementProductDAO;


    public OrderRepository(ISupplierRepository supplierRepository, IProductRepository productRepository, OrderDaoSQLite orderDAO, SupplierDaoSQLite supplierDAO, OrderProductDAOSQLite orderProductDAO, AgreementProductDaoSQLite agreementProductDAO) throws SQLException {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.orderDAO = orderDAO;
        this.orderProductDAO = orderProductDAO;
        this.agreementProductDAO = agreementProductDAO;
        loadAllFromDB();
    }

    public void loadAllFromDB() throws SQLException {
        List<OrderDTO> orderDTOs = orderDAO.findAll();
        for (OrderDTO orderDTO : orderDTOs) {
            Supplier supplier = supplierRepository.findSupplierById(orderDTO.supplier_id());
            List<OrderProductDTO> productDTOs = orderProductDAO.findByOrderId(orderDTO.order_id());
            List<OrderProduct> products = new ArrayList<>();
            for (OrderProductDTO orderProductDTO : productDTOs) {
                AgreementProductDTO agreementProductDto = agreementProductDAO.findBySupplyItem_id(orderProductDTO.supplier_ProductId());
                Product product = productRepository.findProduct(agreementProductDto.product_id());
                products.add(OrderProductMapper.toEntity(orderProductDTO, AgreementProductMapper.toEntity(agreementProductDto, product, supplier)));
            }
            Order order = OrderMapper.toEntity(orderDTO, supplier, products);
            orderStorage.put(order.getOrder_id(), order);

        }
        loadOrdersToSupplier();
    }

    private void loadOrdersToSupplier() throws SQLException {
        for (Supplier supplier : supplierRepository.findAllSuppliers())
            for (Order order : findAllOrdersBySupplier(supplier.getSupplier_id()))
                supplier.addOrder(order);
    }


    @Override
    public Order createOrder(String contactNum, Supplier supplier) throws SQLException {
        Order newOrder = new Order(contactNum, supplier, findMaxId());
        orderStorage.put(newOrder.getOrder_id(), newOrder);
        orderDAO.save(OrderMapper.toDto(newOrder));
        supplier.addOrder(newOrder);
        return newOrder;
    }

    @Override
    public PeriodicOrder createPeriodicOrder(Product product, Collection<Supplier> suppliers) throws SQLException {
        PeriodicOrder newOrder = new PeriodicOrder(product, suppliers, findMaxId());
        orderStorage.put(newOrder.getOrder_id(), newOrder);
        orderDAO.save(OrderMapper.toDto(newOrder));
        return newOrder;
    }

    //create Periodic order that is already exist (only need to be renewed)
    @Override
    public PeriodicOrder recreatePeriodicOrder(Product product, Supplier supplier) throws SQLException {
        PeriodicOrder newOrder = new PeriodicOrder(product, supplier, findMaxId());
        orderStorage.put(newOrder.getOrder_id(), newOrder);
        return newOrder;
    }

    @Override
    public ShortageOrder createShortageOrder(Product product, Collection<Supplier> suppliers) throws SQLException {
        ShortageOrder newOrder = new ShortageOrder(product, suppliers, findMaxId());
        orderStorage.put(newOrder.getOrder_id(), newOrder);
        orderDAO.save(OrderMapper.toDto(newOrder));
        return newOrder;
    }

    @Override
    public Order findOrderById(int id) throws SQLException {
        return orderStorage.get(id);
    }

    @Override
    public List<Order> findOrdersByDates(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderStorage.values())
            if (order.getOrderDate().isBefore(endDate) && order.getOrderDate().isAfter(startDate))
                orders.add(order);
        return orders;
    }

    @Override
    public List<Order> findOrdersByProductId(int id) throws SQLException {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderStorage.values())
            for (OrderProduct orderProduct : order.getProducts())
                if (orderProduct.getAgreementItem().getProduct().getId() == id)
                    orders.add(order);
        return orders;
    }

    @Override
    public List<Order> findOrdersByProductSupId(String id) throws SQLException {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderStorage.values())
            for (OrderProduct orderProduct : order.getProducts())
                if (Objects.equals(orderProduct.getAgreementItem().getSupplyItem_id(), id))
                    orders.add(order);
        return orders;
    }

    @Override
    public List<Order> findAllOrders() throws SQLException {
        return new ArrayList<>(orderStorage.values());
    }

    @Override
    public int findMaxId() throws SQLException {
        int max = 0;
        for (Order order : orderStorage.values())
            if (order.getOrder_id() > max)
                max = order.getOrder_id();
        return max;
    }

    @Override
    public List<PeriodicOrder> findAllPeriodicOrders() throws SQLException {
        List<PeriodicOrder> periodicOrders = new ArrayList<>();
        for (Order order : orderStorage.values())
            if (order instanceof PeriodicOrder)
                periodicOrders.add((PeriodicOrder) order);
        return periodicOrders;
    }

    @Override
    public List<Order> findAllOrdersBySupplier(int supplier_id) throws SQLException {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderStorage.values())
            if (order.getSupplier().getSupplier_id() == supplier_id)
                orders.add(order);
        return orders;
    }

    @Override
    public void changeOrderStatus(int id, OrderStatus status) throws SQLException {
        Order order = orderStorage.get(id);
        order.setStatus(status);
        orderDAO.update(OrderMapper.toDto(order));
    }

    @Override
    public List<PeriodicOrder> checkAllPeriodicOrders(Collection<Supplier> suppliers) throws SQLException {
        List<PeriodicOrder> orders = new ArrayList<>();
        for (PeriodicOrder periodicOrder : this.findAllPeriodicOrders())
            //check if it's time for another periodic order
            if (periodicOrder.getOrderDate().plusWeeks(periodicOrder.frequency).equals(LocalDate.now())) {
                //if original supplier_id is active, order from him
                if (periodicOrder.getSupplier().getSupplierStatus().equals(SupplierStatus.ACTIVE))
                    orders.add(this.recreatePeriodicOrder(periodicOrder.getProduct(), periodicOrder.getSupplier()));
                else
                    orders.add(this.createPeriodicOrder(periodicOrder.getProduct(), suppliers));
            }
        return orders;
    }

    @Override
    public void setOrderDeliveryDate(int order_id, LocalDate deliveryDate) throws SQLException {
        Order order = orderStorage.get(order_id);
        order.setDeliveryDate(deliveryDate);
        orderDAO.update(OrderMapper.toDto(order));
    }

    @Override
    public LocalDate getDeliveryDate(int order_id) throws SQLException {
        return findOrderById(order_id).getDeliveryDate();
    }
}
