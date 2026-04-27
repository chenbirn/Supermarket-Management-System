package domainLayer;

import enums.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a supply agreement with a supplier_id, including delivery details and associated items.
 * Manages adding/removing items, setting delivery conditions, and tracking agreement status.
 */
public class Agreement {
    private final int agreement_id;
    private List<DeliveryDays> deliveryDays = new ArrayList<>();
    private DeliveryMethod deliveryMethod;
    private Collection<AgreementProduct> products = new ArrayList<>();
    private final Supplier supplier;
    private AgreementStatus status;

    /**
     * Constructs a new Agreement.
     *
     * @param deliveryDays   List of delivery days for the agreement.
     * @param deliveryMethod Delivery method (direct delivery or pickup).
     * @param supplier       Associated supplier_id for this agreement.
     * @throws IllegalArgumentException if any parameter is null.
     */
    protected Agreement(List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, Supplier supplier, int max_id) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier must not be null.");
        if (deliveryDays == null)
            throw new IllegalArgumentException("Delivery days must not be null.");
        if (deliveryMethod == null)
            throw new IllegalArgumentException("Delivery method must not be null.");
        this.agreement_id = max_id + 1;
        this.deliveryDays = deliveryDays;
        this.deliveryMethod = deliveryMethod;
        this.supplier = supplier;
        this.status = AgreementStatus.ACTIVE;
    }

    //recreation constructor
    protected Agreement(int id, List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, Supplier supplier) {
        if (supplier == null)
            throw new IllegalArgumentException("Supplier must not be null.");
        if (deliveryDays == null)
            throw new IllegalArgumentException("Delivery days must not be null.");
        if (deliveryMethod == null)
            throw new IllegalArgumentException("Delivery method must not be null.");

        this.agreement_id = id;
        this.deliveryDays = deliveryDays;
        this.deliveryMethod = deliveryMethod;
        this.supplier = supplier;
        this.status = AgreementStatus.ACTIVE;
    }

    //getters

    /**
     * Returns the unique ID of the agreement.
     *
     * @return agreement ID.
     */
    protected int getAgreement_id() {
        return agreement_id;
    }

    /**
     * Returns the list of delivery days for the agreement.
     *
     * @return List of DeliveryDays.
     */
    protected List<DeliveryDays> getDeliveryDays() {
        return deliveryDays;
    }

    /**
     * Returns the delivery method for the agreement.
     *
     * @return DeliveryMethod.
     */
    protected DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    /**
     * Returns the collection of items included in the agreement.
     *
     * @return Collection of AgreementProduct.
     */
    protected Collection<AgreementProduct> getProducts() {
        return products;
    }

    /**
     * Returns the supplier_id associated with the agreement.
     *
     * @return Supplier.
     */
    protected Supplier getSupplier() {
        return supplier;
    }

    /**
     * Returns the current status of the agreement.
     *
     * @return AgreementStatus (ACTIVE or INACTIVE).
     */
    protected AgreementStatus getStatus() {
        return this.status;
    }

    //setters

    /**
     * Updates the delivery days for the agreement.
     *
     * @param deliveryD New list of delivery days.
     * @throws IllegalArgumentException if the list is null or empty.
     */
    protected void setDeliveryDays(List<DeliveryDays> deliveryD) {
        if (deliveryD == null || deliveryD.isEmpty()) {
            throw new IllegalArgumentException("Delivery days can't be empty.");
        }
        deliveryDays = deliveryD;
    }

    /**
     * Updates the delivery method for the agreement.
     *
     * @param deliveryM New delivery method.
     * @throws IllegalArgumentException if the method is null.
     */
    protected void setDeliveryMethod(DeliveryMethod deliveryM) {
        if (deliveryM == null) {
            throw new IllegalArgumentException("Delivery method cannot be null.");
        }
        deliveryMethod = deliveryM;
    }

    /**
     * Sets the collection of items for the agreement.
     *
     * @param newItems Collection of AgreementProduct.
     * @throws IllegalArgumentException if the collection is null or empty.
     */
    protected void setProducts(Collection<AgreementProduct> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            throw new IllegalArgumentException("Item's list can't be empty.");
        }
        products = newItems;
    }

    /**
     * Updates the status of the agreement.
     *
     * @param Astatus New AgreementStatus.
     * @throws IllegalArgumentException if the status is null.
     */
    protected void setStatus(AgreementStatus Astatus) {
        if (Astatus == null) {
            throw new IllegalArgumentException("Agreement's status cannot be null.");
        }
        status = Astatus;
    }

    // Item management

    /**
     * Adds a new item to the agreement and updates the supplier_id's supplied items.
     *
     * @param price Price of the item.
     * @param product  Item to add.
     * @throws IllegalArgumentException if the item is null or price is non-positive.
     */
    protected void addItem(double price, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        AgreementProduct agreementProduct = new AgreementProduct(price, product, this.getSupplier());
        if (product.getCurrQuantity() == -1)
            product.setCurrQuantity(0);
        this.products.add(agreementProduct);
        //update supplier_id's supplied items
        supplier.addSuppliedItem(product);
    }

    /**
     * Removes an item from the agreement and updates the supplier_id's supplied items.
     *
     * @param product Item to remove.
     * @throws IllegalArgumentException if the item is null.
     */
    protected void removeItem(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        AgreementProduct agreementProduct = this.AgreementItemByProduct(product);
        this.getProducts().remove(agreementProduct);
        //update supplier_id's supplied items
        supplier.removeSuppliedItem(product);
    }

    /**
     * Assigns a quantity-based discount to an item in the agreement.
     *
     * @param item     Item to assign the discount to.
     * @param discount Discount value.
     * @param quantity Minimum quantity for discount to apply.
     * @param dType    Type of discount (percentage or amount).
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    protected void setQuantityAgreementToItem(Product item, double discount, int quantity, DiscountMethod dType) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        if (dType == null) {
            throw new IllegalArgumentException("Discount method cannot be null.");
        }
        if (discount <= 0) {
            throw new IllegalArgumentException("Discount must be positive.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        AgreementProduct agreementProduct = AgreementItemByProduct(item);
        if (agreementProduct != null)
            agreementProduct.setQAgreement(discount, quantity, dType);
    }

    /**
     * Retrieves the AgreementProduct associated with the given system item.
     *
     * @param product System item to find in the agreement.
     * @return AgreementProduct if found, otherwise null.
     * @throws IllegalArgumentException if the item is null.
     */
    protected AgreementProduct AgreementItemByProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        for (AgreementProduct AgItem : this.products)
            if (AgItem.getProduct().equals(product))
                return AgItem;
        return null;
    }

    /**
     * Checks whether an item exists in the agreement.
     *
     * @param item Item to check.
     * @return true if the item is in the agreement, false otherwise.
     * @throws IllegalArgumentException if the item is null.
     */
    protected boolean checkProduct(Product item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        for (AgreementProduct agreementProduct : this.getProducts())
            if (agreementProduct.getProduct().equals(item))
                return true;
        return false;
    }

    @Override
    public String toString() {
        String part1 = "==Agreement number " + this.agreement_id + "==\nAgreement's status: " + this.status + "\nSupplier's name: " + this.supplier.getName()
                + "\nSupplier's id: " + this.supplier.getSupplier_id() + "\nDelivery days: " + this.getDeliveryDays() + "\nDelivery method: "
                + this.deliveryMethod + "\nITEMS:\n";
        StringBuilder part2 = new StringBuilder();
        for (AgreementProduct item : this.products) {
            part2.append(item.toString()).append("\n");
        }
        return part1 + part2;
    }
}
