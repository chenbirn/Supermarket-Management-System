package domainLayer;

import static enums.DiscountMethod.PERCENTAGE;

/**
 * Represents an item within an order.
 * Contains quantity, pricing, discount details, and associated agreement item.
 */
public class OrderProduct {
    private int quantity; //quantity in order
    private double price; // price
    private boolean useDiscount; // if to use quantity agreement or not
    private double discount; // discount
    private double finalPrice; //final price after discount
    private final int order_id; // id of order the item is in
    private AgreementProduct agreementProduct; // order item is based on agreement item

    /**
     * Constructs a new OrderProduct.
     *
     * @param quantity    Quantity of the item.
     * @param useDiscount Whether to apply a discount.
     * @param order_id    The associated Order.
     * @param agItem      The associated AgreementProduct.
     */
    protected OrderProduct(int quantity, boolean useDiscount, int order_id, AgreementProduct agItem) {
        this.order_id = order_id;
        this.quantity = quantity;
        this.price = agItem.getPrice();
        this.useDiscount = useDiscount;
        //calculate discount
        if (agItem.getQAgreement() != null) {
            if (useDiscount && quantity >= agItem.getQAgreement().getQuantity()) {
                this.discount = agItem.getQAgreement().getDiscount();
                if (agItem.getQAgreement().getDiscountMethod().equals(PERCENTAGE)) {
                    this.finalPrice = price * this.quantity * ((100 - discount) / 100);
                } else {
                    this.finalPrice = this.price * this.quantity - discount;
                }
            } else {
                //no discount
                this.discount = 0;
                this.finalPrice = this.price * this.quantity;
            }
        } else {
            this.discount = 0;
            this.finalPrice = this.price * this.quantity;
        }
        this.agreementProduct = agItem;
    }

    //getters

    /**
     * Returns the quantity of the item in the order.
     *
     * @return Quantity.
     */
    protected int getQuantity() {
        return quantity;
    }

    /**
     * Returns the unique identifier of the order.
     *
     * @return Order ID.
     */
    protected int getOrder_id() {
        return order_id;
    }

    /**
     * Returns the agreement item associated with this order item.
     *
     * @return AgreementProduct.
     */
    protected AgreementProduct getAgreementItem() {
        return agreementProduct;
    }

    /**
     * Returns the price per unit for the item.
     *
     * @return Price per unit.
     */
    protected double getPrice() {
        return price;
    }

    /**
     * Returns the discount applied to the item.
     *
     * @return Discount value.
     */
    protected double getDiscount() {
        return discount;
    }

    /**
     * Returns the final price after applying discount.
     *
     * @return Final price.
     */
    protected double getFinalPrice() {
        return finalPrice;
    }

    //setters

    /**
     * Sets the quantity of the item.
     *
     * @param quantity New quantity.
     */
    protected void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the price per unit of the item.
     *
     * @param price New price value.
     */
    protected void setPrice(double price) {
        this.price = price;
    }

    /**
     * Sets the discount value.
     *
     * @param discount New discount amount.
     */
    protected void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * Sets the final price after applying discount.
     *
     * @param finalPrice New final price value.
     */
    protected void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }


    protected void setAgreementItem(AgreementProduct agreementProduct) {
        this.agreementProduct = agreementProduct;
    }

    /**
     * Sets whether the discount should be applied.
     *
     * @param useDiscount true to apply discount, false otherwise.
     */
    protected void setUseDiscount(boolean useDiscount) {
        this.useDiscount = useDiscount;
    }

    /**
     * Returns a string representation of the order item, including item name, quantity, price, and discount.
     *
     * @return String description of the order item.
     */
    @Override
    public String toString() {
        return "==" + this.agreementProduct.getProduct().getName() + "==\nItem's catalog number: " + this.agreementProduct.getSupplyItem_id() +
                "\nQuantity: " + this.quantity + "\nPrice: " + this.price + "\nDiscount: " + this.discount + "\nFinal Price: " + this.finalPrice + "\n";
    }

    /**
     * Indicates whether the discount should be applied to this item.
     *
     * @return true if discount is used, false otherwise.
     */
    protected boolean isUseDiscount() {
        return useDiscount;
    }
}
