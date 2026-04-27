package domainLayer;

import DTO.ProductDto;
import DTO.SupplierDTO;
import dataAccessLayer.*;
import enums.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static enums.productOrder.ORDERED;

public class MainController {
    private final Controller inventory_con;
    private final SupplierController supplier_con;


    public MainController(Controller inventory_con, SupplierController supplier_con) {
            this.inventory_con = inventory_con;
            this.supplier_con = supplier_con;
    }

    public void CreatePeriodicOrder(int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        PeriodicOrder periodicOrder = supplier_con.createPeriodicOrder(product);
        inventory_con.UpdateStatusProduct(product_id);
        supplier_con.AddOrderToSupplier(periodicOrder);
    }

    public ShortageOrder CreateShortageOrder(int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        productOrder status = inventory_con.getStatus(product_id);
        if (status != ORDERED) {
            ShortageOrder shortageOrder = supplier_con.createShortageOrder(product);
            inventory_con.UpdateStatusProduct(product_id);
            supplier_con.AddOrderToSupplier(shortageOrder);
            return shortageOrder;
        }
        return null;
    }

    //get all supplier_id's delivery days
    public Set<DeliveryDays> getDeliveryDays(int supplier_id) throws SQLException {
        return supplier_con.getDeliveryDays(supplier_id);
    }

    //set order's delivery date
    public void setOrderDeliveryDate(int order_id, LocalDate deliveryDate) throws SQLException {
        supplier_con.setOrderDeliveryDate(order_id, deliveryDate);
    }

    public LocalDate getDeliveryDate(int order_id) throws SQLException {
        return supplier_con.getDeliveryDate(order_id);
    }

    public List<PeriodicOrder> checkAllPeriodicOrders() throws SQLException {
        return supplier_con.checkAllPeriodicOrders();
    }

    public int createNewOrder(String contactNum, int supplier_id) throws SQLException {
        return supplier_con.createNewOrder(contactNum, supplier_id);
    }

    public List<Order> getAllOrders() throws SQLException {
        return supplier_con.getAllOrders();
    }

    public Order findOrderById(int id) throws SQLException {
        return supplier_con.findOrderById(id);
    }

    public Collection<Order> findOrdersBySupplier(int supplier_id) throws SQLException {
        return supplier_con.findOrdersBySupplier(supplier_id);
    }

    public Collection<Order> findOrdersByProductSysId(int system_id) throws SQLException {
        return supplier_con.findOrdersByProductSysId(system_id);
    }

    //find all orders with given product (according to product's supplier_id id)
    public Collection<Order> findOrdersByProductSupId(String sup_id) throws SQLException {
        return supplier_con.findOrdersByProductSupId(sup_id);
    }

    //find all orders in given date range
    public Collection<Order> findOrdersByDates(LocalDate startDate, LocalDate endDate) throws SQLException {
        return supplier_con.findOrdersByDates(startDate, endDate);
    }

    //returns all items in order that can not be supplied by given supplier_id
    public Collection<OrderProduct> canNotSupply(int order_id, int supplier_id) throws SQLException {
        return supplier_con.canNotSupply(order_id, supplier_id);
    }

    //change order's supplier_id
    public void changeSupplier(int order_id, int supplier_id) throws SQLException {
        supplier_con.changeSupplier(order_id, supplier_id);
    }

    //returns order's current status
    public OrderStatus currentStatus(int order_id) throws SQLException {
        return supplier_con.currentStatus(order_id);
    }

    //change order's status
    public void changeStatus(int order_id, OrderStatus status) throws SQLException {
        supplier_con.changeStatus(order_id, status);
    }

    //check if item is in order
    public boolean checkOrderProduct(int order_id, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        return supplier_con.checkOrderProduct(order_id, product);
    }

    //remove product from order
    public void removeProductFromOrder(int order_id, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.removeProductFromOrder(order_id, product);
    }

    //change quantity of product in order
    public void changeProductQuantity(int order_id, int product_id, int quantity) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.changeProductQuantity(order_id, product, quantity);
    }

    //add product to order
    public void addProductToOrder(int order_id, int product_id, int quantity, boolean useDiscount) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.addProductToOrder(order_id, product, quantity, useDiscount);
    }

    //returns all products in order
    public Collection<OrderProduct> productsInOrder(int order_id) throws SQLException {
        return supplier_con.productsInOrder(order_id);
    }

    //returns supplier_id's id in order
    public int supplierIdByOrder(int order_id) throws SQLException {
        return supplier_con.supplierIdByOrder(order_id);
    }

    //print supplier_id's contacts
    public void printSupplierContacts(int supplier_id) throws SQLException {
        supplier_con.printSupplierContacts(supplier_id);
    }

    //get number of contacts supplier_id has
    public int getNumOfContacts(int supplier_id) throws SQLException {
        return supplier_con.getNumOfContacts(supplier_id);
    }

    //get supplier_id's status
    public SupplierStatus getSupplierStatus(int supplier_id) throws SQLException {
        return supplier_con.getSupplierStatus(supplier_id);
    }


    // Change supplier_id's name
    public void changeSupplierName(int supplier_id, String newName) throws SQLException {
        supplier_con.changeSupplierName(supplier_id, newName);
    }

    // Change supplier_id's address
    public void changeSupplierAddress(int supplier_id, String newAddress) throws SQLException {
        supplier_con.changeSupplierAddress(supplier_id, newAddress);
    }

    // Change supplier_id's payment types
    public void changeSupplierPaymentTypes(int supplier_id, EnumSet<PaymentType> newPaymentT) throws SQLException {
        supplier_con.changeSupplierPaymentTypes(supplier_id, newPaymentT);
    }

    // Change supplier_id's bank account
    public void changeSupplierBankAccount(int supplier_id, int newBankA) throws SQLException {
        supplier_con.changeSupplierBankAccount(supplier_id, newBankA);
    }

    // Add a contact to supplier_id
    public void addContactToSupplier(int supplier_id, Contact contact) throws SQLException {
        supplier_con.addContactToSupplier(supplier_id, contact);
    }

    public void removeContactFromSupplier(int supplier_id, Contact contact) throws SQLException {
        supplier_con.removeContactFromSupplier(supplier_id, contact);
    }

    // Supplier creation function
    public boolean createNewSupplier(SupplierDTO dto) throws SQLException {
        return supplier_con.createNewSupplier(dto);
    }

    // Find supplier_id by ID
    public Supplier findSupplierById(int id) throws SQLException {
        return supplier_con.findSupplierById(id);
    }

    // Get all suppliers
    public List<Supplier> getAllSuppliers() throws SQLException {
        return supplier_con.getAllSuppliers();
    }

    //get all active suppliers
    public List<Supplier> getActiveSuppliers() throws SQLException {
        return supplier_con.getActiveSuppliers();
    }

    // Remove or deactivate supplier_id
    public boolean removeOrDeactivateSupplier(int supplier_id) throws SQLException {
        return supplier_con.removeOrDeactivateSupplier(supplier_id);
    }

    //check if supplier_id provides this product
    public boolean supplierProvidesProduct(int supplier_id, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        return supplier_con.supplierProvidesItem(supplier_id, product);
    }

    //return all products supplier_id supply
    public Collection<Product> supplierProducts(int supplier_id) throws SQLException {
        return supplier_con.supplierItems(supplier_id);
    }

    // Get all orders of a supplier_id
    public Collection<Order> getAllOrdersOfSupplier(int supplier_id) throws SQLException {
        return supplier_con.getAllOrdersOfSupplier(supplier_id);
    }

    // Create a new contact
    public Contact createContact(String name, String phone) {
        return supplier_con.createContact(name, phone);
    }

    //check if supplier_id has agreements
    public boolean hasActiveAgreements(int supplier_id) throws SQLException {
        return supplier_con.hasActiveAgreements(supplier_id);
    }

    //agreement functions//

    //create agreement and return its id
    public int createAgreement(List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, int supplier_id) throws SQLException {
        return supplier_con.createAgreement(deliveryDays, deliveryMethod, supplier_id);
    }

    public Agreement findAgreementById(int agreement_id) throws SQLException {
        return supplier_con.findAgreementById(agreement_id);
    }

    public Collection<AgreementProduct> productsInAgreement(int agreement_id) throws SQLException {
        return supplier_con.productsInAgreement(agreement_id);
    }

    //edit delivery method in agreement
    public void editDeliveryMethod(int agreement_id, DeliveryMethod deliveryMethod) throws SQLException {
        supplier_con.editDeliveryMethod(agreement_id, deliveryMethod);
    }

    //edit delivery days in agreement
    public void editDeliveryDays(int agreement_id, List<DeliveryDays> deliveryDays) throws SQLException {
        supplier_con.editDeliveryDays(agreement_id, deliveryDays);
    }

    //add product to agreement
    public void addProductToAgreement(int agreement_id, double price, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.addProductToAgreement(agreement_id, price, product);
    }

    //add quantity agreement to item
    public void addQuantityAgreement(int agreement_id, int product_id, double discount, int quantity, DiscountMethod Dtype) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.addQuantityAgreement(agreement_id, product, discount, quantity, Dtype);
    }

    //remove product from agreement
    public void removeProductFromAgreement(int agreement_id, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.removeProductFromAgreement(agreement_id, product);
    }

    //change quantity in quantity agreement
    public void editQuantityInQA(int agreement_id, int product_id, int quantity) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.editQuantityInQA(agreement_id, product, quantity);
    }

    //edit discount in quantity agreement
    public void editDiscountInQa(int agreement_id, int product_id, double discount) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.editDiscountInQa(agreement_id, product, discount);
    }

    //edit discount method in quantity agreement
    public void editDMethodInQA(int agreement_id, int product_id, DiscountMethod discountMethod) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        supplier_con.editDMethodInQA(agreement_id, product, discountMethod);
    }

    //change agreement's status
    public void changeAgreementStatus(int agreement_id, AgreementStatus status) throws SQLException {
        supplier_con.changeAgreementStatus(agreement_id, status);
    }

    //check if item is in agreement
    public boolean checkAgreementProduct(int agreement_id, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        return supplier_con.checkAgreementProduct(agreement_id, product);
    }

    //returns agreement's current status
    public AgreementStatus currAgreementStatus(int agreement_id) throws SQLException {
        return supplier_con.currAgreementStatus(agreement_id);
    }

    //check if item has quantity agreement
    public boolean checkQAgreement(int supplier_id, int product_id) throws SQLException {
        Product product = inventory_con.findProductById(product_id);
        return supplier_con.checkQAgreement(supplier_id, product);
    }

    //return all active agreements
    public List<Agreement> getActiveAgreements() throws SQLException {
        return supplier_con.getActiveAgreements();
    }

    //get all agreements
    public List<Agreement> getAllAgreements() throws SQLException {
        return supplier_con.getAllAgreements();
    }

    public int findSupplierIdByAgreementId(int agreement_id) throws SQLException {
        return supplier_con.findSupplierIdByAgreementId(agreement_id);
    }

    public void checkLowStockAlert() throws SQLException {
        List<Product> lowStockProducts = inventory_con.findAllBelowMinQuantity();
        List<ShortageOrder> shortageOrders = new ArrayList<>();

        if (lowStockProducts.isEmpty()) {
            System.out.println("All products are above their minimum stock.\n");
        } else {
            System.out.println("Some products have reached their minimum stock:\n");
            for (Product product : lowStockProducts) {
                if (product.getCurrQuantity() > -1) {
                    System.out.println("- Product name: " + product.getName() + " " + product.getWeight());
                    System.out.println("  Current quantity: " + product.getCurrQuantity());
                    System.out.println("  Minimum required quantity: " + product.getMinQuantity());
                    System.out.println();
                    ShortageOrder shortageOrder = CreateShortageOrder(product.getId());
                    if (shortageOrder != null)
                        shortageOrders.add(shortageOrder);
                }
            }
            if (!shortageOrders.isEmpty()) {
                System.out.println("The following orders has been made:\n");
                for (ShortageOrder shortageOrder : shortageOrders)
                    System.out.println(shortageOrder + "\n");
            }
        }
    }

    //check all periodic orders and create orders that reached relevant date
    public List<PeriodicOrder> checkPeriodicOrders() throws SQLException {
        return supplier_con.checkAllPeriodicOrders();
    }

    public List<Contact> createContactList(String name, String phone) {
        return supplier_con.createContactList(name, phone);
    }

    public Contact getContactByPosition(int supplierId, int position) throws SQLException {
        return supplier_con.getContactByPosition(supplierId, position);
    }

    public void InventoryReport(String input) {
        inventory_con.InventoryReport(input);
    }

    public void orderReport() throws SQLException {
        inventory_con.orderReport();
    }

    public void DefectiveReport() throws SQLException {
        inventory_con.DefectiveReport();
    }

    public void printProductDetails(int productId) throws SQLException {
        inventory_con.printProductDetails(productId);
    }

    public Product addProduct(ProductDto dto) throws SQLException {
        return inventory_con.addProduct(dto);

    }

    public boolean updateProductPrice(int productId, double newPrice) throws SQLException {
        return inventory_con.updateProductPrice(productId, newPrice);
    }

    public boolean addItemsToProduct(int productId, int count, double buyPrice, LocalDate expDate) throws SQLException {
        List<Supplier> suppliers = supplier_con.getAllSuppliers();
        Product product = inventory_con.findProductById(productId);
        for (Supplier supplier: suppliers)
            if (supplier.getSuppliedItems().contains(product)) {
                inventory_con.addItemsToProduct(productId, count, buyPrice, expDate);
                return true;
            }
        return false;
    }

    public boolean reportItemAsDefective(int productId, int itemId) throws SQLException {
        return inventory_con.reportItemAsDefective(productId, itemId);
    }

    public boolean removeOldestItemsFromProduct(int productId, int count) throws SQLException {
        return inventory_con.removeOldestItemsFromProduct(productId, count);
    }

    public boolean moveItemsFromWarehouseToShelf(int productId, int count) throws SQLException {
        return inventory_con.moveItemsFromWarehouseToShelf(productId, count);
    }

    public boolean updateProductWeight(int id, double weight) throws SQLException {
        return inventory_con.updateProductWeight(id, weight);
    }

    public boolean updateProductBoxUnits(int productId, int newBoxUnits) throws SQLException {
        return inventory_con.updateProductBoxUnits(productId, newBoxUnits);
    }

    public boolean applyDiscountToProduct(int DiscountID, int productId, double percent, LocalDate start, LocalDate end) throws SQLException {
        return inventory_con.applyDiscountToProduct(DiscountID, productId, percent, start, end);
    }

    public boolean applyDiscountToCategory(int DiscountID, String mainCategory, String subCategory, String sizeCategory, double discountPercent, LocalDate start, LocalDate end) {
        return inventory_con.applyDiscountToCategory(DiscountID, mainCategory, subCategory, sizeCategory, discountPercent, start, end);
    }

    public void updateProductPricesBasedOnCategoryDiscounts() {
        inventory_con.updateProductPricesBasedOnCategoryDiscounts();
    }

    public Product findProductById(int id) throws SQLException {
        return inventory_con.findProductById(id);
    }
    public void updatePackageType(int id,String type)throws SQLException{
        inventory_con.updatePackageType( id, type);
    }
}

