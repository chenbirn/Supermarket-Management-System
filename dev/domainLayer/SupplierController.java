package domainLayer;

import DTO.SupplierDTO;
import enums.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class SupplierController {
    private final ISupplierRepository supplierRepository;
    private final IOrderRepository orderRepository;
    private final IAgreementRepository agreementRepository;

    public SupplierController(ISupplierRepository supplierRepository, IOrderRepository orderRepository, IAgreementRepository agreementRepository) {
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
        this.agreementRepository = agreementRepository;
    }
    //order functions//

    //create a new order and returns its id
    public int createNewOrder(String contactNum, int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        Order order = orderRepository.createOrder(contactNum, supplier);
        return order.getOrder_id();
    }

    public PeriodicOrder createPeriodicOrder(Product product) throws SQLException {
        Collection<Supplier> suppliers = supplierRepository.findAllActiveSuppliers();
        return orderRepository.createPeriodicOrder(product, suppliers);
    }

    public ShortageOrder createShortageOrder(Product product) throws SQLException {
        Collection<Supplier> suppliers = supplierRepository.findAllActiveSuppliers();
        return orderRepository.createShortageOrder(product, suppliers);
    }

    public void AddOrderToSupplier(Order order) {
        order.getSupplier().addOrder(order);
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderRepository.findAllOrders();
    }

    //find an order according to its id
    public Order findOrderById(int id) throws SQLException {
        return orderRepository.findOrderById(id);
    }

    //find all orders with given supplier_id
    public Collection<Order> findOrdersBySupplier(int supplier_id) throws SQLException {
        return orderRepository.findAllOrdersBySupplier(supplier_id);
    }

    //find all orders with given product (according to item's system id)
    public Collection<Order> findOrdersByProductSysId(int system_id) throws SQLException {
        return orderRepository.findOrdersByProductId(system_id);
    }

    //find all orders with given product (according to product's supplier_id id)
    public Collection<Order> findOrdersByProductSupId(String sup_id) throws SQLException {
        return orderRepository.findOrdersByProductSupId(sup_id);
    }

    //find all orders in given date range
    public Collection<Order> findOrdersByDates(LocalDate startDate, LocalDate endDate) throws SQLException {
        return orderRepository.findOrdersByDates(startDate, endDate);
    }

    //returns all items in order that can not be supplied by given supplier_id
    public Collection<OrderProduct> canNotSupply(int order_id, int supplier_id) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        return order.notSupplied(supplier);
    }

    //change order's supplier_id
    public void changeSupplier(int order_id, int supplier_id) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        order.changeSupplier(supplier);
    }

    //returns order's current status
    public OrderStatus currentStatus(int order_id) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        return order.getStatus();
    }

    //change order's status
    public void changeStatus(int order_id, OrderStatus status) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        order.changeStatus(status);
    }

    //check if product is in order
    public boolean checkOrderProduct(int order_id, Product product) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        return this.orderProductFromProduct(order, product) != null;
    }

    //remove product from order
    public void removeProductFromOrder(int order_id, Product product) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        order.removeProduct(this.orderProductFromProduct(order, product));
    }

    //help function - returns order's OrderProduct by given product (if item not in order returns null)
    private OrderProduct orderProductFromProduct(Order order, Product product) {
        for (OrderProduct orderProduct : order.getProducts())
            if (orderProduct.getAgreementItem().getProduct().equals(product))
                return orderProduct;
        return null;
    }

    //change quantity of product in order
    public void changeProductQuantity(int order_id, Product product, int quantity) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        OrderProduct orderProduct = this.orderProductFromProduct(order, product);
        if (orderProduct != null)
            order.changeItemQuantity(orderProduct, quantity);
    }

    //add product to order
    public void addProductToOrder(int order_id, Product product, int quantity, boolean useDiscount) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        AgreementProduct AgProduct = this.FindAgreementProduct(order.getSupplier(), product);
        order.addProduct(quantity, useDiscount, AgProduct);
    }

    //returns all products in order
    public Collection<OrderProduct> productsInOrder(int order_id) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        return order.getProducts();
    }

    //returns supplier_id's id in order
    public int supplierIdByOrder(int order_id) throws SQLException {
        Order order = orderRepository.findOrderById(order_id);
        return order.getSupplier().getSupplier_id();
    }

    //supplier_id functions//

    public Contact getContactByPosition(int supplier_id, int position) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        return supplier.getContactByPosition(position);
    }


    public void printSupplierContacts(int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        supplier.printContacts();
    }

    public int getNumOfContacts(int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        return supplier.getNumOfContacts();
    }

    public SupplierStatus getSupplierStatus(int supplier_id) throws SQLException {
        return supplierRepository.getSupplierStatus(supplier_id);
    }


    // Change supplier_id's name
    public void changeSupplierName(int supplier_id, String newName) throws SQLException {
        supplierRepository.changeSupplierName(supplier_id, newName);
    }

    // Change supplier_id's address
    public void changeSupplierAddress(int supplier_id, String newAddress) throws SQLException {
        supplierRepository.changeSupplierAddress(supplier_id, newAddress);
    }

    // Change supplier_id's payment types
    public void changeSupplierPaymentTypes(int supplier_id, EnumSet<PaymentType> newPaymentT) throws SQLException {
        supplierRepository.changeSupplierPaymentTypes(supplier_id, newPaymentT);
    }

    // Change supplier_id's bank account
    public void changeSupplierBankAccount(int supplier_id, int newBankA) throws SQLException {
        supplierRepository.changeSupplierBankAccount(supplier_id, newBankA);
    }

    // Add a contact to supplier_id
    public void addContactToSupplier(int supplier_id, Contact contact) throws SQLException {
        supplierRepository.addContactToSupplier(supplier_id, contact);
    }

    public void removeContactFromSupplier(int supplier_id, Contact contact) throws SQLException {
        supplierRepository.removeContactFromSupplier(supplier_id, contact);
    }

    // Supplier creation function
    public boolean createNewSupplier(SupplierDTO dto) throws SQLException {
        if (supplierRepository.findSupplierById(dto.supplier_id()) != null) {
            return false; // Supplier already exist
        }
        Supplier newSupplier = supplierRepository.createSupplier(dto);
        return true;
    }

    // Find supplier_id by ID
    public Supplier findSupplierById(int id) throws SQLException {
        return supplierRepository.findSupplierById(id);
    }

    // Get all suppliers
    public List<Supplier> getAllSuppliers() throws SQLException {
        return supplierRepository.findAllSuppliers();
    }

    //get all active suppliers
    public List<Supplier> getActiveSuppliers() throws SQLException {
        return supplierRepository.findAllActiveSuppliers();
    }

    private boolean canRemoveSupplier(Supplier supplier) {
        return supplier.getSignedAgreements().isEmpty() && supplier.getOrders().isEmpty();
    }

    // Remove or deactivate supplier_id
    public boolean removeOrDeactivateSupplier(int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        if (supplier == null) {
            return false;  // Supplier not found
        }
        if (canRemoveSupplier(supplier)) {
            // No agreement and no orders connected - safe to delete
            supplierRepository.deleteSupplier(supplier_id);
            return true;
        } else {
            // Supplier had agreements or orders
            supplierRepository.changeStatus(supplier_id, SupplierStatus.INACTIVE);
            return true;
        }
    }

    public boolean supplierProvidesItem(int supplier_id, Product product) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        if (supplier == null) {
            return false;
        }
        for (Product productToCheck : supplier.getSuppliedItems()) {
            if (productToCheck.equals(product)) {
                return true;
            }
        }
        return false;
    }

    //return all products supplier_id supply
    public Collection<Product> supplierItems(int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        return supplier.getSuppliedItems();
    }

    // Get all orders of a supplier_id
    public Collection<Order> getAllOrdersOfSupplier(int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        if (supplier == null) {
            return new ArrayList<>();
        }
        return supplier.getOrders();
    }

    // Create a new contact
    public Contact createContact(String name, String phone) {
        return new Contact(name, phone);
    }

    // Create a list with one contact
    public List<Contact> createContactList(String name, String phone) {
        Contact contact = new Contact(name, phone);
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        return contacts;
    }

    //check if supplier_id has agreements
    public boolean hasActiveAgreements(int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        return !agreementRepository.findAllActiveAgreementsBySupplier(supplier).isEmpty();
    }

    //get all supplier_id's delivery days
    public Set<DeliveryDays> getDeliveryDays(int supplier_id) throws SQLException {
        return supplierRepository.getDeliveryDays(supplier_id);
    }

    //set order's delivery date
    public void setOrderDeliveryDate(int order_id, LocalDate deliveryDate) throws SQLException {
        orderRepository.setOrderDeliveryDate(order_id, deliveryDate);
    }

    public LocalDate getDeliveryDate(int order_id) throws SQLException {
        return orderRepository.getDeliveryDate(order_id);
    }

    public List<PeriodicOrder> checkAllPeriodicOrders() throws SQLException {
        Collection<Supplier> suppliers = supplierRepository.findAllActiveSuppliers();
        return orderRepository.checkAllPeriodicOrders(suppliers);
    }

    //agreement functions//

    //create agreement and return its id
    public int createAgreement(List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, int supplier_id) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        Agreement agreement = agreementRepository.createAgreement(deliveryDays, deliveryMethod, supplier);
        //add to supplier_id's agreements
        supplier.addAgreement(agreement);
        return agreement.getAgreement_id();
    }

    public int findSupplierIdByAgreementId(int agreement_id) throws SQLException {
        return agreementRepository.findSupplierIdByAgreementId(agreement_id);
    }
    public Agreement findAgreementById(int agreement_id) throws SQLException {
        return agreementRepository.findAgreementById(agreement_id);
    }

    public Collection<AgreementProduct> productsInAgreement(int agreement_id) throws SQLException {
        return agreementRepository.productsInAgreement(agreement_id);
    }

    //edit delivery method in agreement
    public void editDeliveryMethod(int agreement_id, DeliveryMethod deliveryMethod) throws SQLException {
        agreementRepository.editDeliveryMethod(agreement_id, deliveryMethod);
    }

    //edit delivery days in agreement
    public void editDeliveryDays(int agreement_id, List<DeliveryDays> deliveryDays) throws SQLException {
        agreementRepository.editDeliveryDays(agreement_id, deliveryDays);
    }

    //add product to agreement
    public void addProductToAgreement(int agreement_id, double price, Product product) throws SQLException {
        agreementRepository.addProductToAgreement(agreement_id, price, product);
    }

    //add quantity agreement to product
    public void addQuantityAgreement(int agreement_id, Product product, double discount, int quantity, DiscountMethod Dtype) throws SQLException {
        agreementRepository.addQuantityAgreement(agreement_id, product, discount, quantity, Dtype);
    }

    //remove product from agreement
    public void removeProductFromAgreement(int agreement_id, Product product) throws SQLException {
        agreementRepository.removeProductFromAgreement(agreement_id, product);
        //remove item from supplied items of supplier_id
//        agreement.getSupplier().getSuppliedItems().remove(item);
    }

    //change quantity in quantity agreement
    public void editQuantityInQA(int agreement_id, Product product, int quantity) throws SQLException {
        agreementRepository.editQuantityInQA(agreement_id, product, quantity);
    }

    //edit discount in quantity agreement
    public void editDiscountInQa(int agreement_id, Product product, double discount) throws SQLException {
        agreementRepository.editDiscountInQa(agreement_id, product, discount);
    }

    //edit discount method in quantity agreement
    public void editDMethodInQA(int agreement_id, Product product, DiscountMethod discountMethod) throws SQLException {
        agreementRepository.editDMethodInQA(agreement_id, product, discountMethod);
    }

    //change agreement's status
    public void changeAgreementStatus(int agreement_id, AgreementStatus status) throws SQLException {
        agreementRepository.changeAgreementStatus(agreement_id, status);

    }

    //help function - get AgreementProduct by supplier_id and product
    private AgreementProduct FindAgreementProduct(Supplier supplier, Product product) {
        for (Agreement agreement : supplier.getSignedAgreements())
            for (AgreementProduct agreementProduct : agreement.getProducts())
                if (agreementProduct.getProduct().equals(product))
                    return agreementProduct;
        return null;
    }

    //check if product is in agreement
    public boolean checkAgreementProduct(int agreement_id, Product product) throws SQLException {
        return agreementRepository.checkAgreementProduct(agreement_id, product);
    }

    //returns agreement's current status
    public AgreementStatus currAgreementStatus(int agreement_id) throws SQLException {
        return agreementRepository.currAgreementStatus(agreement_id);
    }

    //check if product has quantity agreement
    public boolean checkQAgreement(int supplier_id, Product product) throws SQLException {
        Supplier supplier = supplierRepository.findSupplierById(supplier_id);
        return agreementRepository.checkQAgreement(supplier, product);
    }

    //return all active agreements
    public List<Agreement> getActiveAgreements() throws SQLException {
        return agreementRepository.findAllActiveAgreements();
    }

    public List<Agreement> getAllAgreements() throws SQLException {
        return agreementRepository.findAllAgreements();
    }

}
