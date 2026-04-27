package domainLayer;

import enums.*;

import java.util.*;

/**
 * Represents a Supplier in the system.
 * A Supplier can have agreements, supply items, handle orders, and has contacts.
 */
public class Supplier {
    // Supplier fields
    private final int supplier_id;
    private String name;
    private String address;
    private EnumSet<PaymentType> paymentType;
    private int bankAccount;
    private SupplierStatus supplierStatus;

    // Supplier connections
    private final List<Contact> contacts;  // 1-2 contacts per supplier_id
    private final Map<Integer, Agreement> signedAgreements;  // Agreements signed with this supplier_id
    private final Map<Integer, Product> suppliedItems;  // Items the supplier_id can provide
    private final Map<Integer, Order> orders;  // Order history for this supplier_id

    /**
     * Constructor a new Supplier with the given information.
     *
     * @param name        Supplier's name
     * @param address     address Supplier's address
     * @param supplier_id supplier_id Unique ID for the supplier_id
     * @param paymentType paymentType Accepted payment types
     * @param bankAccount bankAccount Supplier's bank account number
     * @param contacts    contacts List of contacts (must be 1-2)
     */
    public Supplier(String name, String address, int supplier_id, EnumSet<PaymentType> paymentType,
                    int bankAccount, List<Contact> contacts) {
        this.supplier_id = supplier_id;
        setName(name);
        setAddress(address);
        setPaymentType(paymentType);
        setBankAccount(bankAccount);
        setSupplierStatus(SupplierStatus.ACTIVE);

        // Contacts must be 1-2 and not null
        if (contacts == null || contacts.isEmpty() || contacts.size() > 2) {
            throw new IllegalArgumentException("Supplier must have 1-2 contacts.");
        }
        this.contacts = new ArrayList<>(contacts);

        // Optional fields initialized as empty
        this.signedAgreements = new HashMap<>();
        this.suppliedItems = new HashMap<>();
        this.orders = new HashMap<>();
    }

    //constructor for converting from DTO
    public Supplier(String name, String address, int supplier_id, EnumSet<PaymentType> paymentType,
                    int bankAccount, List<Contact> contacts, SupplierStatus status) {
        this.supplier_id = supplier_id;
        setName(name);
        setAddress(address);
        setPaymentType(paymentType);
        setBankAccount(bankAccount);
        setSupplierStatus(status);
        this.contacts = new ArrayList<>(contacts);

        // Optional fields initialized as empty
        this.signedAgreements = new HashMap<>();
        this.suppliedItems = new HashMap<>();
        this.orders = new HashMap<>();
    }

    //Getters//

    /**
     * @return Supplier's name
     */
    protected String getName() {
        return name;
    }

    /**
     * @return Supplier's address
     */
    protected String getAddress() {
        return address;
    }

    /**
     * @return Supplier's ID
     */
    protected int getSupplier_id() {
        return supplier_id;
    }

    /**
     * @return Supplier's bank account number
     */
    protected int getBankAccount() {
        return bankAccount;
    }

    /**
     * @return Payment types accepted by the supplier_id
     */
    protected EnumSet<PaymentType> getPaymentType() {
        return paymentType;
    }

    /**
     * @return List of the supplier_id's contacts
     */
    protected List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    /**
     * @return Collection of item supplied by the supplier_id
     */
    protected Collection<Product> getSuppliedItems() {
        return suppliedItems.values();
    }

    /**
     * @return Collection of agreements signed with the supplier_id
     */
    protected Collection<Agreement> getSignedAgreements() {
        return signedAgreements.values();
    }

    /**
     * @return Collection of orders made with the supplier_id
     */
    protected Collection<Order> getOrders() {
        return orders.values();
    }

    /**
     * @return The current status of the supplier_id
     */
    public SupplierStatus getSupplierStatus() {
        return supplierStatus;
    }

    //Supplier status functions//

    /**
     * @return True if the supplier_id supplies at least one item
     */
    protected boolean hasItems() {
        return !suppliedItems.isEmpty();
    }

    /**
     * @return True if the supplier_id has any unfinished orders
     */
    protected boolean hasActiveOrders() {
        for (Order order : orders.values()) {
            if (order.getStatus() != OrderStatus.DONE) {
                return true;  // there is at least one unfinished order
            }
        }
        return false; // All orders are finished or none at all
    }

    /**
     * @return True if the supplier_id has any active agreements
     */
    protected boolean hasActiveAgreements() {
        for (Agreement agreement : signedAgreements.values()) {
            if (agreement.getStatus() == AgreementStatus.ACTIVE) {
                return true;  // there at least one active agreement
            }
        }
        return false;  // no active agreements
    }

    //Contact management//

    /**
     * Adds a new contact to the supplier_id.
     *
     * @param contact The contact to add
     * @throws IllegalArgumentException if the contact in null, already exists, or
     *                                  if there are already 2 contacts.
     */
    protected void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null.");
        }
        if (contacts.contains(contact)) {
            throw new IllegalArgumentException("Contact already exist.");
        }
        if (contacts.size() >= 2) {
            throw new IllegalArgumentException("Supplier cannot have more than 2 contacts.");
        }
        contacts.add(contact);
    }

    /**
     * Removes a contact from the supplier_id.
     *
     * @param contact The contact to remove
     * @return true if contact was removed successfully
     * @throws IllegalArgumentException if contact is null, not found, or removing would
     *                                  leave zero contacts.
     */
    protected boolean removeContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null.");
        }
        if (!contacts.contains(contact)) {
            throw new IllegalArgumentException("Contact does not exist in this supplier_id.");
        }
        if (contacts.size() == 1) {
            throw new IllegalArgumentException("Supplier must have at least one contact.");
        }
        return contacts.remove(contact);
    }

    //Agreement management//

    /**
     * Adds an agreement to the supplier_id.
     *
     * @param agreement The agreement to add
     * @throws IllegalArgumentException if the agreement is null
     */
    protected void addAgreement(Agreement agreement) {
        if (agreement == null) {
            throw new IllegalArgumentException("Agreement cannot be null.");
        }
        signedAgreements.put(agreement.getAgreement_id(), agreement);
    }

    /**
     * Removes an agreement from the supplier_id.
     *
     * @param agreement The agreement to remove
     * @throws IllegalArgumentException if the agreement is null or not found
     */
    protected void removeAgreement(Agreement agreement) {
        if (agreement == null) {
            throw new IllegalArgumentException("Agreement must not be null.");
        }
        if (!signedAgreements.containsKey(agreement.getAgreement_id())) {
            throw new IllegalArgumentException("Agreement not found.");
        }
        signedAgreements.remove(agreement.getAgreement_id());
    }

    //Supplied items management//

    /**
     * Adds a supplied item to the supplier_id.
     *
     * @param product The product to add
     * @throws IllegalArgumentException if the item is null
     */
    protected void addSuppliedItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        suppliedItems.put(product.getId(), product);
    }

    /**
     * Removes a supplied item from the supplier_id.
     *
     * @param product The item to remove
     * @throws IllegalArgumentException if the item is null or not found
     */
    protected void removeSuppliedItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }
        if (!suppliedItems.containsKey(product.getId())) {
            throw new IllegalArgumentException("Item not found in supplier_id's items.");
        }
        suppliedItems.remove(product.getId());
    }

    //Orders management//

    /**
     * Adds an order to the supplier_id's history.
     *
     * @param order The order to add
     * @throws IllegalArgumentException if the order is null or already exists
     */
    protected void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        if (orders.containsKey(order.getOrder_id())) {
            throw new IllegalArgumentException("Order with this ID already exists.");
        }
        orders.put(order.getOrder_id(), order);
    }

    //Setters//

    /**
     * Sets the supplier_id's name. Must not be null or empty.
     */
    protected void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty.");
        }
        this.name = name;
    }

    /**
     * Sets the supplier_id's address. Must not be null or empty.
     */
    protected void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier address cannot be null or empty.");
        }
        this.address = address;
    }

    /**
     * Sets the supplier_id's bank account number. Must be positive.
     */
    protected void setBankAccount(int bankAccount) {
        if (bankAccount <= 0) {
            throw new IllegalArgumentException("Bank account must be a positive number.");
        }
        this.bankAccount = bankAccount;
    }

    /**
     * Sets the supplier_id's accepted payment types. Must not be null or empty.
     */
    protected void setPaymentType(EnumSet<PaymentType> paymentType) {
        if (paymentType == null || paymentType.isEmpty()) {
            throw new IllegalArgumentException("At least one payment type must be provided.");
        }
        this.paymentType = paymentType;
    }

    /**
     * Sets the supplier_id's current status.
     */
    public void setSupplierStatus(SupplierStatus supplierStatus) {
        this.supplierStatus = supplierStatus;
    }

    //Equality and display//

    /**
     * @return True if two suppliers have the same supplier_id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Supplier supplier = (Supplier) obj;
        return getSupplier_id() == supplier.supplier_id;
    }

    /**
     * @return Hash code based on supplier_id
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(supplier_id);
    }

    /**
     * @return string representation of the supplier_id
     */
    @Override
    public String toString() {
        return " == Supplier: " + name + " ==\n" +
                "Supplier ID: " + getSupplier_id() + "\n" +
                "Address: " + getAddress() + "\n" +
                "Bank Account: " + getBankAccount() + "\n" +
                "Payment Method: " + getPaymentType() + "\n" +
                "Contacts:\n" + formatContacts() + "\n" +
                "Status: " + getSupplierStatus();
    }

    /**
     * @return string representation of the supplier_id's contacts
     */
    private String formatContacts() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < contacts.size(); i++) {
            res.append(" - ").append(contacts.get(i).toString());
            if (i < contacts.size() - 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }

    /**
     * Returns the number of contacts associated with this supplier_id.
     *
     * @return the number of contacts
     */
    protected int getNumOfContacts() {
        return contacts.size();
    }

    /**
     * Prints all the contacts associated with this supplier_id, numbered starting from 1.
     */
    protected void printContacts() {
        int idx = 1;
        for (Contact contact : contacts) {
            System.out.println(idx + ". " + contact);
            idx++;
        }
    }

    /**
     * Returns the contact at the specified position (1-based index).
     *
     * @param position the position in the contacts list (starting from 1)
     * @return the Contact at the given position
     * @throws IllegalArgumentException if the position is invalid
     */
    protected Contact getContactByPosition(int position) {
        if (position < 1 || position > contacts.size()) {
            throw new IllegalArgumentException("Invalid contact position.");
        }

        int idx = 1;
        for (Contact cont : contacts) {
            if (idx == position) {
                return cont;
            }
            idx++;
        }
        throw new IllegalArgumentException("Contact not found.");
    }

    //return supplier_id's delivery days
    protected Set<DeliveryDays> getDeliveryDays() {
        List<DeliveryDays> deliveryDays = new ArrayList<>();
        for (Agreement agreement : this.getSignedAgreements())
            deliveryDays.addAll(agreement.getDeliveryDays());
        return new HashSet<>(deliveryDays);
    }
}