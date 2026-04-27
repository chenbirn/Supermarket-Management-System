package domainLayer;

import enums.DiscountMethod;

/**
 * Represents an item in a supplier_id agreement.
 * Stores pricing information, catalog ID, and optional quantity-based discounts.
 * Allows updating price, discount details, and item information.
 */
public class AgreementProduct {
    // Fields
    private final String supplyItem_id;  // Catalog number at supplier_id
    private double price;  // Price per unit
    private DiscountByQuantity qAgreement;  // Optional quantity agreement for item
    private final Product product;  // The item this agreement refers to

    /**
     * Constructs an AgreementProduct with a price, system item, and supplier_id.
     *
     * @param price    Price per unit for the item.
     * @param product  Item associated with this agreement entry.
     * @param supplier Supplier providing the item.
     * @throws IllegalArgumentException if price is not positive.
     */
    protected AgreementProduct(double price, Product product, Supplier supplier) {
        setPrice(price);
        this.product = product;
        this.qAgreement = null;

        // Create a catalog number ID by combining supplier_id ID and item system ID
        this.supplyItem_id = supplier.getSupplier_id() + "-" + product.getId();
    }

    //getters

    /**
     * Returns the supplier_id's catalog ID for this item.
     *
     * @return Supplier catalog ID.
     */
    protected String getSupplyItem_id() {
        return supplyItem_id;
    }

    /**
     * Returns the Item associated with this agreement entry.
     *
     * @return Item object.
     */
    protected Product getProduct() {
        return product;
    }

    /**
     * Returns the price per unit for the item.
     *
     * @return Item price.
     */
    protected double getPrice() {
        return price;
    }

    /**
     * Returns the quantity-based discount agreement if available.
     *
     * @return DiscountByQuantity or null if none exists.
     */
    protected DiscountByQuantity getQAgreement() {
        return qAgreement;
    }

    //setters

    /**
     * Sets the price of the item.
     *
     * @param price New price value.
     * @throws IllegalArgumentException if price is not positive.
     */
    protected void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        this.price = price;
    }

    /**
     * Assigns a new quantity-based discount agreement to this item.
     *
     * @param discount Discount amount.
     * @param quantity Minimum quantity for discount eligibility.
     * @param Dtype    Type of discount (percentage or fixed amount).
     */
    protected void setQAgreement(double discount, int quantity, DiscountMethod Dtype) {
        if (Dtype.equals(DiscountMethod.AMOUNT)) {
            if (this.getPrice() < discount)
                throw new IllegalArgumentException("Discount can't be larger then price.");
        }
        this.qAgreement = new DiscountByQuantity(discount, quantity, Dtype);
    }

    /**
     * Updates the minimum quantity required for the quantity discount.
     *
     * @param quantity New minimum quantity.
     */
    protected void changeQuantityInQA(int quantity) {
        this.qAgreement.setQuantity(quantity);
    }

    /**
     * Updates the discount amount in the quantity agreement.
     *
     * @param discount New discount value.
     */
    protected void changeDiscountInQA(double discount) {
        this.qAgreement.setDiscount(discount);
    }

    /**
     * Updates the discount method (percentage or amount) in the quantity agreement.
     *
     * @param discountMethod New discount method.
     */
    protected void changeDMethodInQA(DiscountMethod discountMethod) {
        this.qAgreement.setDiscountMethod(discountMethod);
    }

    // Overrides

    /**
     * Returns a string representation of the AgreementProduct, including catalog ID, item details, and price.
     *
     * @return String representation of the AgreementProduct.
     */
    @Override
    public String toString() {
        if (this.qAgreement == null)
            return "Catalog ID: " + supplyItem_id + ", " + product.getName() +
                    ", Price: " + price + "₪";
        else
            return "Catalog ID: " + supplyItem_id + ", " + product.getName() +
                    ", Price: " + price + "₪" + "\nQuantity agreement: " + this.qAgreement;
    }

    /**
     * Compares two AgreementProduct objects based on their supplier_id catalog ID.
     *
     * @param obj Another AgreementProduct to compare.
     * @return true if both have the same supplyItem_id, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        AgreementProduct Aitem = (AgreementProduct) obj;
        if (this.supplyItem_id == Aitem.getSupplyItem_id())
            return true;
        return false;
    }

}
