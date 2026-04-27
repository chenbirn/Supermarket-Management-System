package serviceLayer;

import DTO.SupplierDTO;
import domainLayer.*;
import enums.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * SupplierService connects the presentation layer to the domain logic.
 * It handles supplier_id-related actions such as creating suppliers,
 * updating supplier_id details, adding/removing contacts, and deleting suppliers.
 */
public class SupplierService {
    private final MainController controller;

    public SupplierService(MainController controller) {
        this.controller = controller;
    }


    public String createNewSupplier(SupplierDTO dto) {
        try {
            boolean success = controller.createNewSupplier(dto);
            if (success) {
                return "Supplier created successfully.";
            } else {
                return "Supplier already exists.";
            }
        } catch (Exception e) {
            return "A problem occurred during creating new supplier " + e.getMessage();
        }
    }

    /**
     * Finds a supplier_id by ID.
     *
     * @param id Supplier ID
     * @return Supplier object if found, otherwise null
     */
    public Supplier findSupplierById(int id) throws SQLException {
        return controller.findSupplierById(id);
    }

    public boolean supplierProvidesProduct(int supplier_id, int product_id) throws SQLException {
        return controller.supplierProvidesProduct(supplier_id, product_id);
    }

    /**
     * Returns a list of all suppliers.
     *
     * @return Collection of suppliers
     */
    public Collection<Supplier> getAllSuppliers() throws SQLException {
        return controller.getAllSuppliers();
    }

    /**
     * Changes the name of a supplier_id.
     *
     * @param supplier_id Supplier ID
     * @param newName     New name for the supplier_id
     * @return Message indicating success or failure
     */
    public String changeSupplierName(int supplier_id, String newName) {
        try {
            controller.changeSupplierName(supplier_id, newName);
            return "Supplier's name successfully changed to " + newName;
        } catch (Exception e) {
            return "A problem occurred during editing. " + e.getMessage();
        }
    }

    /**
     * Changes the address of a supplier_id.
     *
     * @param supplier_id Supplier ID
     * @param newAddress  New address for the supplier_id
     * @return Message indicating success or failure
     */
    public String changeSupplierAddress(int supplier_id, String newAddress) {
        try {
            controller.changeSupplierAddress(supplier_id, newAddress);
            return "Supplier's address successfully changed.";
        } catch (Exception e) {
            return "A problem occurred during editing. " + e.getMessage();
        }
    }

    /**
     * Changes the payment types of a supplier_id.
     *
     * @param supplier_id     Supplier ID
     * @param newPaymentTypes New set of payment types
     * @return Message indicating success or failure
     */
    public String changeSupplierPaymentTypes(int supplier_id, EnumSet<PaymentType> newPaymentTypes) {
        try {
            controller.changeSupplierPaymentTypes(supplier_id, newPaymentTypes);
            return "Supplier's payment types successfully changed.";
        } catch (Exception e) {
            return "A problem occurred during editing. " + e.getMessage();
        }
    }

    /**
     * Changes the bank account of a supplier_id.
     *
     * @param supplier_id    Supplier ID
     * @param newBankAccount New bank account number
     * @return Message indicating success or failure
     */
    public String changeSupplierBankAccount(int supplier_id, int newBankAccount) {
        try {
            controller.changeSupplierBankAccount(supplier_id, newBankAccount);
            return "Supplier's bank account successfully changed.";
        } catch (Exception e) {
            return "A problem occurred during updating bank account: " + e.getMessage();
        }
    }

    /**
     * Adds a contact to a supplier_id.
     *
     * @param supplier_id Supplier ID
     * @param contact     Contact to add
     * @return Message indicating success or failure
     */
    public String addContactToSupplier(int supplier_id, Contact contact) {
        try {
            controller.addContactToSupplier(supplier_id, contact);
            return "Contact added successfully.";
        } catch (Exception e) {
            return "A problem occurred during adding contact: " + e.getMessage();
        }
    }

    /**
     * Removes a contact from a supplier_id.
     *
     * @param supplier_id Supplier ID
     * @param contact     Contact to remove
     * @return Message indicating success or failure
     */
    public String removeContactFromSupplier(int supplier_id, Contact contact) {
        try {
            controller.removeContactFromSupplier(supplier_id, contact);
            return "Contact removed successfully.";
        } catch (Exception e) {
            return "A problem occurred while removing contact: " + e.getMessage();
        }
    }


    /**
     * Removes (or deactivates) a supplier_id.
     *
     * @param supplier_id Supplier ID
     * @return true if successful, false if supplier_id was not found
     */
    public boolean removeSupplier(int supplier_id) throws SQLException {
        return controller.removeOrDeactivateSupplier(supplier_id);
    }

    /**
     * Checks if a supplier_id exists by ID.
     *
     * @param supplier_id Supplier ID
     * @return true if supplier_id exists, false otherwise
     */
    public boolean supplierExists(int supplier_id) throws SQLException {
        return controller.findSupplierById(supplier_id) != null;
    }

    /**
     * Creates a contact using the given name and phone.
     *
     * @param name  Contact name
     * @param phone Contact phone number
     * @return A Contact object
     */
    public Contact createContact(String name, String phone) {
        return controller.createContact(name, phone);
    }

    /**
     * Creates a list with one contact inside it.
     *
     * @param name  Contact name
     * @param phone Contact phone number
     * @return List containing the single contact
     */
    public List<Contact> createContactList(String name, String phone) {
        return controller.createContactList(name, phone);
    }

    //returns amount of items supplied by supplier_id
    public int suppliedItemsAmount(int supplier_id) throws SQLException {
        return controller.supplierProducts(supplier_id).size();
    }

    //check if supplier_id has agreements
    public boolean hasActiveAgreements(int supplier_id) throws SQLException {
        return controller.hasActiveAgreements(supplier_id);
    }

    //Get supplier_id status
    public SupplierStatus getSupplierStatus(int supplier_id) {
        try {
            return controller.getSupplierStatus(supplier_id);
        } catch (Exception e) {
            System.out.println("A problem occurred while getting supplier_id status: " + e.getMessage());
            return null;
        }
    }

    //get all active suppliers
    public List<Supplier> activeSuppliers() throws SQLException {
        return controller.getActiveSuppliers();
    }

    /**
     * Returns the number of contacts for a supplier_id by supplier_id ID.
     *
     * @param supplier_id the supplier_id's ID
     * @return the number of contacts associated with the supplier_id
     * @throws IllegalArgumentException if the supplier_id does not exist
     */
    public int getNumOfContacts(int supplier_id) throws SQLException {
        return controller.getNumOfContacts(supplier_id);
    }

    /**
     * Prints the contacts of a supplier_id by supplier_id ID.
     *
     * @param supplier_id the ID of the supplier_id
     */
    public void printSupplierContacts(int supplier_id) throws SQLException {
        controller.printSupplierContacts(supplier_id);
    }

    /**
     * Returns the contact of a supplier_id by the supplier_id's ID and the contact's position.
     *
     * @param supplier_id the ID of the supplier_id
     * @param position    the position of the contact in the supplier_id's contact list (starting from 1)
     * @return the Contact at the given position
     */
    public Contact getContactByPosition(int supplier_id, int position) throws SQLException {
        return controller.getContactByPosition(supplier_id, position);
    }

    //get all supplier_id's delivery days
    public Set<DeliveryDays> getDeliveryDays(int supplier_id) throws SQLException {
        return controller.getDeliveryDays(supplier_id);
    }

    //set order's delivery date
    public void setOrderDeliveryDate(int order_id, LocalDate deliveryDate) throws SQLException {
        controller.setOrderDeliveryDate(order_id, deliveryDate);
    }


}

