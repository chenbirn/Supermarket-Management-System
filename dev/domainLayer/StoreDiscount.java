package domainLayer;
import  enums.*;

import java.time.LocalDate;

// class that represent discount in the store
public class StoreDiscount {
    private int DiscountId;
    private double PercentDiscount; //SALE
    private DiscountType categoryOrProduct;
    private LocalDate StartDate;
    private LocalDate EndDate;

    // constructor for store discount
    public StoreDiscount(int discountId, double percentDiscount, DiscountType categoryOrProduct, LocalDate startDate, LocalDate endDate) {
        DiscountId = discountId;
        PercentDiscount = percentDiscount;
        this.categoryOrProduct = categoryOrProduct;
        StartDate = startDate;
        EndDate = endDate;
    }

    //// getters and setters ////
    public int getDiscountId() {
        return DiscountId;
    }

    public void setDiscountId(int discountId) {
        DiscountId = discountId;
    }


    public DiscountType getCategoryOrProduct() {
        return categoryOrProduct;
    }

    public void setCategoryOrProduct(DiscountType categoryOrProduct) {
        this.categoryOrProduct = categoryOrProduct;
    }

    public LocalDate getStartDate() {
        return StartDate;
    }

    public void setStartDate(LocalDate startDate) {
        StartDate = startDate;
    }

    public LocalDate getEndDate() {
        return EndDate;
    }

    public void setEndDate(LocalDate endDate) {
        EndDate = endDate;
    }

    public double getPercentDiscount() {
        return PercentDiscount;
    }

    public void setPercentDiscount(double percentDiscount) {
        PercentDiscount = percentDiscount;
    }
}
