package domainLayer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import enums.*;

public class PeriodicOrder extends Order {
    int frequency;

    protected PeriodicOrder(Product product, Collection<Supplier> suppliers, int max_id) {
        this(product, findCheapestSupplier(product, suppliers), max_id);
    }

    protected PeriodicOrder(Product product, Supplier cheapest, int max_id) {
        super(cheapest.getContacts().getFirst().getPhoneNumber(), cheapest, max_id);
        this.addProduct(product.getMinQuantity() * 3, true, findAgreementProduct(cheapest, product));
        this.frequency = product.getFrequncy();
    }


    private AgreementProduct findAgreementProduct(Supplier supplier, Product product) {
        for (Agreement agreement : supplier.getSignedAgreements())
            for (AgreementProduct agreementProduct : agreement.getProducts())
                if (agreementProduct.getProduct().equals(product))
                    return agreementProduct;
        return null;
    }

    //constructor for converting from DTO
    protected PeriodicOrder(int order_id, LocalDate orderDate, LocalDate deliveryDate, String contactNum, OrderStatus status, List<OrderProduct> products, Supplier supplier, int frequency) {
        super(order_id, orderDate, deliveryDate, contactNum, status, products, supplier);
        this.frequency = frequency;
    }

    private static Supplier findCheapestSupplier(Product product, Collection<Supplier> suppliers) {
        double finalPrice = Double.POSITIVE_INFINITY;
        double price;
        Supplier cheapest = null;
        int orderedQuantity = product.getMinQuantity() * 3;
        for (Supplier s : suppliers) {
            for (Agreement a : s.getSignedAgreements())
                for (AgreementProduct ap : a.getProducts())
                    if (ap.getProduct().equals(product)) {
                        if (ap.getQAgreement() != null) {
                            if (ap.getQAgreement().getQuantity() <= orderedQuantity) {
                                if (ap.getQAgreement().getDiscountMethod().equals(DiscountMethod.AMOUNT)) {
                                    price = orderedQuantity * ap.getPrice() - ap.getQAgreement().getDiscount();
                                } else {
                                    price = orderedQuantity * ap.getPrice() * (100 - ap.getQAgreement().getDiscount() / 100);
                                }
                                if (finalPrice > price) {
                                    finalPrice = price;
                                    cheapest = s;
                                }
                            }
                        }
                        if (finalPrice > ap.getPrice()) {
                            finalPrice = ap.getPrice();
                            cheapest = s;
                        }
                    }
        }
        return cheapest;
    }

    protected Product getProduct() {
        OrderProduct firstItem = new ArrayList<>(this.getProducts()).get(0);
        return firstItem.getAgreementItem().getProduct();
    }

    protected int getFrequency() {
        return this.frequency;
    }
}

