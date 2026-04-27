package serviceLayer;

import domainLayer.*;
import enums.AgreementStatus;
import enums.DeliveryDays;
import enums.DeliveryMethod;
import enums.DiscountMethod;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Service layer for managing supplier_id agreements.
 * Handles creation, modification, cancellation, and querying of agreements.
 */
public class AgreementService {
    private final MainController controller;

    public AgreementService(MainController controller) {
        this.controller = controller;
    }

    /**
     * Creates a new agreement.
     *
     * @param deliveryDays   List of delivery days.
     * @param deliveryMethod Delivery method.
     * @param supplier_id    Supplier ID.
     * @return New agreement ID.
     * @throws IllegalArgumentException if input is invalid.
     */
    public int CreateAgreement(List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, int supplier_id) throws SQLException {
        return controller.createAgreement(deliveryDays, deliveryMethod, supplier_id);
    }

    /**
     * Finds an agreement by its ID.
     *
     * @param agreementId The ID of the agreement to find.
     * @return The Agreement if found, otherwise null.
     */
    public Agreement findAgreement(int agreementId) throws SQLException {
        return controller.findAgreementById(agreementId);
    }

    /**
     * Returns the list of items in the agreement.
     *
     * @param agreementId Agreement ID.
     * @return List of AgreementProduct.
     */
    public Collection<AgreementProduct> ItemsInAgreement(int agreementId) throws SQLException {
        return controller.productsInAgreement(agreementId);
    }

    /**
     * Edits delivery method of an agreement.
     *
     * @param agreement_id   Agreement ID.
     * @param deliveryMethod New delivery method.
     */
    public String editDeliveryMethod(int agreement_id, DeliveryMethod deliveryMethod) {
        try {
            controller.editDeliveryMethod(agreement_id, deliveryMethod);
            return "Delivery method changed successfully.\n";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Edits delivery days of an agreement.
     *
     * @param agreement_id Agreement ID.
     * @param deliveryDays New delivery days.
     */
    public String editDeliveryDays(int agreement_id, List<DeliveryDays> deliveryDays) {
        try {
            controller.editDeliveryDays(agreement_id, deliveryDays);
            return "Delivery days changed successfully.\n";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Adds a new item to an agreement.
     *
     * @param agreement_id Agreement ID.
     * @param price        Price of the item.
     * @param systemItemId System item ID.
     * @throws IllegalArgumentException if item or price is invalid.
     */
    public String addItem(int agreement_id, double price, int systemItemId) {
        try {
            controller.addProductToAgreement(agreement_id, price, systemItemId);
            return "Item successfully added to agreement";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Adds a quantity-based discount agreement to an item.
     *
     * @param agreement_id Agreement ID.
     * @param discount     Discount value.
     * @param quantity     Minimum quantity for discount.
     * @param Dtype        Discount method (percentage or amount).
     * @param systemItemId ID of the system item.
     * @return Success or error message.
     */
    public String addQuantityAgreement(int agreement_id, double discount, int quantity, DiscountMethod Dtype, int systemItemId) {
        try {
            controller.addQuantityAgreement(agreement_id, systemItemId, discount, quantity, Dtype);
            return "Quantity agreement successfully added to item";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Removes an item from an agreement.
     *
     * @param agreement_id  Agreement ID.
     * @param systemItem_id System item ID.
     * @throws IllegalArgumentException if item not found.
     */
    public String removeProduct(int agreement_id, int systemItem_id) {
        try {
            controller.removeProductFromAgreement(agreement_id, systemItem_id);
            return "Item successfully removed from agreement";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Edits the quantity threshold in a quantity-based agreement.
     *
     * @param agreement_id Agreement ID.
     * @param systemItemId ID of the item in the agreement.
     * @param newQuantity  New quantity threshold.
     * @return Success or error message.
     */
    public String editQuantityInQA(int agreement_id, int systemItemId, int newQuantity) {
        try {
            controller.changeProductQuantity(agreement_id, systemItemId, newQuantity);
            return "Quantity successfully changed.";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Edits the discount value in a quantity-based agreement.
     *
     * @param agreement_id Agreement ID.
     * @param systemItemId ID of the item in the agreement.
     * @param newDiscount  New discount value.
     * @return Success or error message.
     */
    public String editDiscountInQA(int agreement_id, int systemItemId, double newDiscount) {
        try {
            controller.editDiscountInQa(agreement_id, systemItemId, newDiscount);
            return "Discount successfully changed.";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Edits the discount method (percentage or amount) in a quantity-based agreement.
     *
     * @param agreement_id Agreement ID.
     * @param systemItemId ID of the item in the agreement.
     * @param newMethod    New DiscountMethod to apply.
     * @return Success or error message.
     */
    public String editDiscountMethodInQA(int agreement_id, int systemItemId, DiscountMethod newMethod) {
        try {
            controller.editDMethodInQA(agreement_id, systemItemId, newMethod);
            return "Discount method successfully changed.";
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Changes the status of an agreement (ACTIVE/INACTIVE).
     *
     * @param agreement_id Agreement ID.
     * @param status       New AgreementStatus.
     * @return Success or error message.
     */
    public String changeStatus(int agreement_id, AgreementStatus status) {
        try {
            controller.changeAgreementStatus(agreement_id, status);
            return "Status successfully changed to " + status.toString();
        } catch (Exception e) {
            return "A problem occurred. " + e.getMessage();
        }
    }

    /**
     * Retrieves the current status of an agreement.
     *
     * @param agreement_id Agreement ID.
     * @return Current AgreementStatus.
     */
    public AgreementStatus getCurrentStatus(int agreement_id) throws SQLException {
        return controller.currAgreementStatus(agreement_id);
    }

    /**
     * Checks whether a specific item is included in an agreement.
     *
     * @param agreement_id Agreement ID.
     * @param systemItemId System item ID to check.
     * @return true if item exists in agreement, false otherwise.
     */
    public boolean checkItem(int agreement_id, int systemItemId) throws SQLException {
        return controller.checkAgreementProduct(agreement_id, systemItemId);
    }

    public List<Agreement> activeAgreements() throws SQLException {
        return controller.getActiveAgreements();
    }

    public List<Agreement> getAllAgreements() throws SQLException {
        return controller.getAllAgreements();
    }

    public int findSupplierIdByAgreementId(int agreement_id) throws SQLException {
        return controller.findSupplierIdByAgreementId(agreement_id);
    }

}
