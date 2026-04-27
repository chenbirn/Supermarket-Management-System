package domainLayer;

import enums.DiscountMethod;

import java.util.Map;

/**
 * Represents a quantity-based discount for an item in an agreement.
 * Manages discount value, required quantity, and discount type (percentage or fixed amount).
 */
public class DiscountByQuantity {
    private double discount; // Value of discount
    private int quantity; // Minimum quantity for discount to apply
    private DiscountMethod discountMethod; // Percentage or amount

    /**
     * Constructs a DiscountByQuantity instance.
     *
     * @param discount Discount value.
     * @param quantity Minimum quantity for discount eligibility.
     * @param Dtype    Discount method (percentage or amount).
     */
    public DiscountByQuantity(double discount, int quantity, DiscountMethod Dtype) {
        setDiscount(discount);
        setQuantity(quantity);
        setDiscountMethod(Dtype);
    }

    // Getters //

    /**
     * Returns the discount value.
     *
     * @return Discount value.
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Returns the required minimum quantity for the discount.
     *
     * @return Minimum quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the discount method (percentage or amount).
     *
     * @return DiscountMethod.
     */
    public DiscountMethod getDiscountMethod() {
        return discountMethod;
    }

    // Setters//

    /**
     * Sets the discount value.
     *
     * @param discount New discount value.
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Setters with validation //

    /**
     * Sets the minimum quantity required for discount eligibility.
     *
     * @param quantity New minimum quantity.
     * @throws IllegalArgumentException if quantity is not positive.
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.quantity = quantity;
    }

    /**
     * Sets the discount method.
     *
     * @param discountMethod New discount method.
     * @throws IllegalArgumentException if discountMethod is null.
     */
    public void setDiscountMethod(DiscountMethod discountMethod) {
        if (discountMethod == null) {
            throw new IllegalArgumentException("Discount method must not be null.");
        }
        this.discountMethod = discountMethod;
    }

    /**
     * Returns a string representation of the discount details.
     *
     * @return String description of the discount.
     */
    @Override
    public String toString() {
        String unit = (discountMethod == DiscountMethod.PERCENTAGE) ? "%" : "₪";
        return "Discount: " + discount + unit + " for " + quantity + "+ units";
    }
}



