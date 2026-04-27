package domainLayer;

import enums.*;

import java.time.LocalDate;

// class that represent an item
public class Item {
    private int ItemId;
    private double BuyPrice;
    ;
    private LocalDate ExpirationDate;
    private DefectiveStatus Status;
    private location location;
    // static field that will generate for every item a different item Id
    private static int nextItemId = 1000;
    private int productId;

    // constructor for item
    public Item(double buyPrice, int itemId, LocalDate expirationDate, DefectiveStatus status, location location, int productId) {
        BuyPrice = buyPrice;
        ItemId = itemId;
        ExpirationDate = expirationDate;
        this.Status = status;
        this.location = location;
        this.productId = productId;
    }

    // printing the item details
    public void printItemDetails() {
        System.out.println("Product ID: " + productId + " | " + "ID: " + ItemId + " | Price: " + BuyPrice + " | Expiration Date: " + ExpirationDate);
    }

    //// getters and setters ////
    public int getItemId() {
        return ItemId;
    }

    public DefectiveStatus getStatus() {
        return Status;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public static int generateUniqueItemId() {
        return nextItemId++;
    }

    public double getBuyPrice() {
        return BuyPrice;
    }

    public LocalDate getExpirationDate() {
        return ExpirationDate;
    }

    public DefectiveStatus isStatus() {
        return Status;
    }

    public location getLocation() {
        return location;
    }

    public void setStatus(DefectiveStatus status) {
        Status = status;
    }

    public void setLocation(location location) {
        this.location = location;
    }
}
