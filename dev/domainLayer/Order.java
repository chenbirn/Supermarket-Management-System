package domainLayer;

import enums.DeliveryDays;
import enums.OrderStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Represents a purchase order made to a supplier_id.
 * Contains supplier_id details, order items, order status, and pricing information.
 */
public class Order {
    private final int order_id; //order's id
    private LocalDate orderDate; //order's creation date
    private LocalDate deliveryDate; //order's delivery date
    private String contactNum; //contact phone number
    private double totalPrice = 0; //total price
    private OrderStatus status; //status (in process/ready/done)
    private Collection<OrderProduct> products = new ArrayList<>(); //items in order
    private Supplier supplier; //supplier_id for items in order

    /**
     * Constructs a new Order.
     *
     * @param contactNum Contact phone number for the supplier_id.
     * @param supplier   Supplier associated with the order.
     * @throws IllegalArgumentException if supplier_id is null or contact number invalid.
     */
    protected Order(String contactNum, Supplier supplier, int max_id) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier cannot be null.");
        this.supplier = supplier;
        setContactNum(contactNum);
        //automatic fields
        this.order_id = max_id +1;
        this.orderDate = LocalDate.now();
        this.totalPrice = 0;
        this.status = OrderStatus.IN_PROCESS;
        setDeliveryDate();
    }

    //constructor for converting from DTO
    protected Order(int order_id, LocalDate orderDate, LocalDate deliveryDate, String contactNum, OrderStatus status, List<OrderProduct> products, Supplier supplier) {
        this.order_id = order_id;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.contactNum = contactNum;
        this.status = status;
        this.supplier = supplier;
        for (OrderProduct orderProduct : products)
            this.addProduct(orderProduct.getQuantity(), orderProduct.isUseDiscount(), orderProduct.getAgreementItem());
    }

    //getters

    /**
     * Returns the order ID.
     *
     * @return Order ID.
     */
    protected int getOrder_id() {
        return order_id;
    }

    /**
     * Returns the order date.
     *
     * @return Order date.
     */
    protected LocalDate getOrderDate() {
        return orderDate;
    }

    /**
     * Returns the contact number for the supplier_id.
     *
     * @return Contact number.
     */
    protected String getContactNum() {
        return contactNum;
    }

    /**
     * Returns the total price of the order.
     *
     * @return Total price.
     */
    protected double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Returns the current status of the order.
     *
     * @return OrderStatus.
     */
    protected OrderStatus getStatus() {
        return status;
    }

    /**
     * Returns the items included in the order.
     *
     * @return Collection of OrderItems.
     */
    protected Collection<OrderProduct> getProducts() {
        return products;
    }

    /**
     * Returns the supplier_id associated with the order.
     *
     * @return Supplier.
     */
    protected domainLayer.Supplier getSupplier() {
        return supplier;
    }

    protected LocalDate getDeliveryDate() {
        return this.deliveryDate;
    }

    //setters

    /**
     * Sets the order date.
     *
     * @param orderDate New order date.
     */
    protected void setOrderDate(LocalDate orderDate) {
        if (orderDate == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.orderDate = orderDate;
    }

    /**
     * Sets the contact phone number for the supplier_id.
     *
     * @param contactNum Contact number to set.
     * @throws IllegalArgumentException if contact number is not positive.
     */
    protected void setContactNum(String contactNum) {
        if (contactNum == null || contactNum.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        String cleaned = contactNum.replaceAll("[\\s-]", "");

        if (!cleaned.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number should have 10 digits only.");
        }

        this.contactNum = cleaned.substring(0, 3) + "-" + cleaned.substring(3);
    }

    /**
     * Sets the total price of the order.
     *
     * @param totalPrice Total price to set.
     * @throws IllegalArgumentException if total price is not positive.
     */
    protected void setTotalPrice(double totalPrice) {
        if (totalPrice <= 0)
            throw new IllegalArgumentException("Total price must be positive.");
        this.totalPrice = totalPrice;
    }

    /**
     * Sets the status of the order.
     *
     * @param status New order status.
     * @throws IllegalArgumentException if status is null.
     */
    protected void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    /**
     * Sets the list of items included in the order.
     *
     * @param products Collection of order items to set.
     * @throws IllegalArgumentException if the collection is null or empty.
     */
    protected void setProducts(Collection<OrderProduct> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Item's list can't be empty.");
        }
        this.products = products;
    }

    /**
     * Sets the supplier_id associated with the order.
     *
     * @param supplier Supplier to set.
     * @throws IllegalArgumentException if supplier_id is null.
     */
    protected void setSupplier(Supplier supplier) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier cannot be null.");
        this.supplier = supplier;
    }

    private void setDeliveryDate() {
        Set<DeliveryDays> supplierDays = this.supplier.getDeliveryDays();
        //if supplier_id only deliver by order, return
        if (supplierDays.size() == 1 && supplierDays.contains(DeliveryDays.BY_ORDER))
            return;
        LocalDate delivery = this.orderDate.plusDays(1);
        for (int i = 0; i < 7; i++) {
            DayOfWeek day = delivery.getDayOfWeek();
            if (day.equals(DayOfWeek.SATURDAY))
                continue;
            //casting DayOfWeek to DeliveryDay
            DeliveryDays deliveryDay = DeliveryDays.valueOf(day.name());
            //check if supplier_id can supply this day
            if (supplierDays.contains(deliveryDay)) {
                this.deliveryDate = delivery;
                break;
            }
            //if not, check the next day
            delivery = delivery.plusDays(1);
            i++;
        }
        if (this.deliveryDate == null) {
            if (!this.orderDate.getDayOfWeek().equals(DayOfWeek.THURSDAY))
                this.deliveryDate = this.orderDate.plusDays(2);
            else
                this.deliveryDate = this.orderDate.plusDays(3);
        }
    }

    protected void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Returns a string representation of the order, including supplier_id and item details.
     *
     * @return String description of the order.
     */
    @Override
    public String toString() {
        String part1 = "==Order number " + this.order_id + "==\nSupplier's name: " + this.supplier.getName() + "\nSupplier's id: "
                + this.supplier.getSupplier_id() + "\nAddress: " + this.supplier.getAddress() + "\nDate: " + this.orderDate + "\nDelivery date:" + this.deliveryDate +
                "\nContact number: " + this.contactNum + "\nITEMS:\n";
        StringBuilder part2 = new StringBuilder();
        for (OrderProduct item : this.products) {
            part2.append(item.toString()).append("\n");
        }
        return part1 + part2 + "Total price: " + this.totalPrice + "\n";
    }

    /**
     * Changes the supplier_id for the order and adjusts items accordingly.
     *
     * @param supplier New supplier_id.
     * @throws IllegalArgumentException if supplier_id is null.
     */
    protected void changeSupplier(Supplier supplier) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier cannot be null.");
        this.setSupplier(supplier);
        List<OrderProduct> supplied = this.supplied(supplier);
        this.setProducts(supplied);
    }

    /**
     * Returns a list of items that the new supplier_id can supply.
     *
     * @param supplier Supplier to check.
     * @return List of OrderItems that can be supplied.
     */
    private List<OrderProduct> supplied(Supplier supplier) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier cannot be null.");
        List<OrderProduct> orderSupplied = new ArrayList<>();
        Collection<Product> supplied = supplier.getSuppliedItems();
        for (OrderProduct orderedItem : this.getProducts()) {
            if (supplied.contains(orderedItem.getAgreementItem().getProduct()))
                orderSupplied.add(orderedItem);
        }
        return orderSupplied;
    }

    /**
     * Returns a collection of items that cannot be supplied by a given supplier_id.
     *
     * @param supplier Supplier to check.
     * @return Collection of OrderItems not supplied.
     */
    protected Collection<OrderProduct> notSupplied(Supplier supplier) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier cannot be null.");
        Collection<OrderProduct> notSupplied = this.getProducts();
        notSupplied.removeAll(this.supplied(supplier));
        return notSupplied;
    }

    /**
     * Changes the status of the order.
     *
     * @param status New OrderStatus.
     * @throws IllegalArgumentException if status is null.
     */
    protected void changeStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.setStatus(status);
    }

    /**
     * Removes an item from the order.
     *
     * @param item Item to remove.
     * @throws IllegalArgumentException if item is null.
     */
    protected void removeProduct(OrderProduct item) {
        if (item == null) {
            throw new IllegalArgumentException("Order's item cannot be null.");
        }
        this.getProducts().remove(item);
    }

    /**
     * Changes the quantity of an item in the order.
     *
     * @param item     Item to modify.
     * @param quantity New quantity.
     * @throws IllegalArgumentException if quantity is not positive or item is null.
     */
    protected void changeItemQuantity(OrderProduct item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Order's item cannot be null.");
        }
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive.");
        item.setQuantity(quantity);
    }

    /**
     * Adds a new item to the order.
     *
     * @param quantity    Quantity of the item.
     * @param useDiscount Whether to apply discount.
     * @param agItem      The agreement item to add.
     * @throws IllegalArgumentException if quantity is not positive or agItem is null.
     */
    protected void addProduct(int quantity, boolean useDiscount, AgreementProduct agItem) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive.");
        if (agItem == null) {
            throw new IllegalArgumentException("Agreement's item cannot be null.");
        }
        OrderProduct item = new OrderProduct(quantity, useDiscount, this.order_id, agItem);
        this.getProducts().add(item);
        this.totalPrice +=  item.getFinalPrice();
    }
}
