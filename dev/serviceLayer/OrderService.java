package serviceLayer;

import domainLayer.*;
import enums.OrderStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Service layer for managing supplier_id agreements.
 * Handles creation, modification, cancellation, and querying of agreements.
 */
public class OrderService {
    private final MainController controller;

    public OrderService(MainController controller) {
        this.controller = controller;
    }


    /**
     * Creates a new order and returns its ID.
     *
     * @param contactNum  Supplier's contact phone number.
     * @param supplier_id Supplier ID.
     * @return Newly created order ID.
     */
    public int createNewOrder(String contactNum, int supplier_id) throws SQLException {
        return controller.createNewOrder(contactNum, supplier_id);
    }

    public List<Order> getAllOrders() throws SQLException {
        return controller.getAllOrders();
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id Order ID.
     * @return Order object if found, otherwise null.
     */
    public Order showOrderById(int id) throws SQLException {
        return controller.findOrderById(id);
    }

    /**
     * Retrieves all orders made by a specific supplier_id.
     *
     * @param s_id Supplier ID.
     * @return Collection of orders made by the supplier_id.
     */
    public Collection<Order> findOrdersBySupplier(int s_id) throws SQLException {
        return controller.findOrdersBySupplier(s_id);
    }

    /**
     * Retrieves all orders that include a specific system item.
     *
     * @param sys_id System item ID.
     * @return Collection of orders containing the item.
     */
    public Collection<Order> findOrdersByItemSysId(int sys_id) throws SQLException {
        return controller.findOrdersByProductSysId(sys_id);
    }

    /**
     * Retrieves all orders containing items supplied by a specific supplier_id.
     *
     * @param sup_id Supplier ID.
     * @return Collection of orders containing items from the supplier_id.
     */
    public Collection<Order> findOrdersByItemSupId(String sup_id) throws SQLException {
        return controller.findOrdersByProductSupId(sup_id);
    }

    /**
     * Retrieves all orders placed within a given date range.
     *
     * @param startDate Start date.
     * @param endDate   End date.
     * @return Collection of orders within the specified date range.
     */
    public Collection<Order> findOrdersByDates(LocalDate startDate, LocalDate endDate) throws SQLException {
        return controller.findOrdersByDates(startDate, endDate);
    }

    /**
     * Retrieves items from an order that cannot be supplied by the specified supplier_id.
     *
     * @param order_id    Order ID.
     * @param supplier_id Supplier ID.
     * @return Collection of OrderProduct objects that cannot be supplied.
     */
    public Collection<OrderProduct> canNotSupply(int order_id, int supplier_id) throws SQLException {
        return controller.canNotSupply(order_id, supplier_id);
    }

    /**
     * Changes the supplier_id associated with an order.
     *
     * @param order_id    Order ID.
     * @param supplier_id New supplier_id ID.
     * @return Success or error message.
     */
    public String changeSupplier(int order_id, int supplier_id) {
        try {
            controller.changeSupplier(order_id, supplier_id);
            return "Supplier successfully changed";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Retrieves the current status of an order.
     *
     * @param order_id Order ID.
     * @return Current OrderStatus.
     */
    public OrderStatus currentStatus(int order_id) throws SQLException {
        return controller.currentStatus(order_id);
    }

    /**
     * Changes the status of an order.
     *
     * @param order_id Order ID.
     * @param status   New OrderStatus to apply.
     * @return Success or error message.
     */
    public String changeStatus(int order_id, OrderStatus status) {
        try {
            controller.changeStatus(order_id, status);
            return "Status successfully changed to: " + status.toString() + ".";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    public boolean checkItem(int order_id, int productSystemId) throws SQLException {
        return controller.checkOrderProduct(order_id, productSystemId);
    }

    /**
     * Removes an item from an order.
     *
     * @param order_id        Order ID.
     * @param productSystemId System ID of the item to remove.
     * @return Success or error message.
     */
    public String removeItem(int order_id, int productSystemId) {
        try {
            controller.removeProductFromOrder(order_id, productSystemId);
            return "Item successfully removed from order";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Changes the quantity of a specific item in an order.
     *
     * @param order_id        Order ID.
     * @param productSystemId System ID of the item to update.
     * @param quantity        New quantity to set.
     * @return Success or error message.
     */
    public String changeItemQuantity(int order_id, int productSystemId, int quantity) {
        try {
            controller.changeProductQuantity(order_id, productSystemId, quantity);
            return "Item's quantity successfully changed to " + quantity + ".";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Adds a new item to an order.
     *
     * @param order_id        Order ID.
     * @param productSystemId System ID of the item to add.
     * @param quantity        Quantity of the item.
     * @param useDiscount     Whether to apply discount to the item.
     * @return Success or error message.
     */
    public String addItem(int order_id, int productSystemId, int quantity, boolean useDiscount) {
        try {
            controller.addProductToOrder(order_id, productSystemId, quantity, useDiscount);
            return "Item successfully added to order";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    //check if product had quantity agreement
    public boolean checkQAgreement(int supplier_id, int product_id) throws SQLException {
        return controller.checkQAgreement(supplier_id, product_id);
    }

    //amount of products in order
    public int productsInOrderAmount(int order_id) throws SQLException {
        return controller.productsInOrder(order_id).size();
    }

    //returns supplier_id's id in order
    public int supplierIdByOrder(int order_id) throws SQLException {
        return controller.supplierIdByOrder(order_id);
    }

    //set order's delivery date
    public void setOrderDeliveryDate(int order_id, LocalDate deliveryDate) throws SQLException {
        controller.setOrderDeliveryDate(order_id, deliveryDate);
    }

    public LocalDate getDeliveryDate(int order_id) throws SQLException {
        return controller.getDeliveryDate(order_id);
    }
}
