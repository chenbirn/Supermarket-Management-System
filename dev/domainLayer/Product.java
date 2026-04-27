package domainLayer;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import enums.*;

import static enums.productOrder.NOT_ORDERED;
import static enums.productOrder.ORDERED;

// class that represent a product that we sell in the store
public class Product {
    private int id;
    private String Manufacturer;
    private String name;
    private double RealPrice;
    private double salePrice;
    private double Weight; // kilo of product
    private double size_ML; // ml of product and needs to ask kilo
    private int MinQuantity;
    private int CurrQuantity;
    public List<Item> items;
    private List<StoreDiscount> StoreDiscounts;
    private PackagingOption packagingOption;
    private UnitType unitType; // ask : kilo/ml
    private int boxUnits;
    private productOrder status;
    private int frequncy;
    String mainCategoryName;
    String subCategoryName;
    String sizeCategoryName;

    // constructor for product
    public Product(int id, String manufacturer, String name, double realPrice, double weight, String mainCategoryName,
                   String subCategoryName,
                   String sizeCategoryName, int minQuantity,int frequncy) {
        this.id = id;
        Manufacturer = manufacturer;
        this.name = name;
        this.salePrice = realPrice;
        RealPrice = realPrice;
        Weight = weight;
        this.mainCategoryName = mainCategoryName;
        this.subCategoryName = subCategoryName;
        this.sizeCategoryName = sizeCategoryName;
        MinQuantity = minQuantity;
        this.CurrQuantity = -1;
        this.StoreDiscounts = new ArrayList<>();
        this.items = new ArrayList<>();
        this.status = NOT_ORDERED;
        this.frequncy = frequncy;
    }


    //check if we need to order some items from product
    public int HowMuchToOrder(){
        return getCurrQuantity()-getMinQuantity();
    }

    //// getters and setters ////
    public int getFrequncy() {
        return frequncy;
    }

    public void setFrequncy(int frequncy) {
        this.frequncy = frequncy;
    }

    // the amount of items will be the size of items list
    public int getCurrQuantity() {
        if ((items == null || items.isEmpty()) && CurrQuantity == 0) {
            return -1;
        }
        return items.size();
    }

    public void setCurrQuantity(int currQuantity) {
        CurrQuantity = currQuantity;
    }

    public productOrder getStatus() {
        return status;
    }

    public void setStatus() {
        if(this.status == NOT_ORDERED){
            this.status = ORDERED;
        }
        else {
            this.status = NOT_ORDERED;
        }
    }

    public int getId() {
        return id;
    }

    public String getManufacturer() {
        return Manufacturer;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainCategoryName() {
        return mainCategoryName;
    }

    public void setMainCategoryName(String mainCategoryName) {
        this.mainCategoryName = mainCategoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSizeCategoryName() {
        return sizeCategoryName;
    }

    public void setSizeCategoryName(String sizeCategoryName) {
        this.sizeCategoryName = sizeCategoryName;
    }

    public int getMinQuantity() {
        return MinQuantity;
    }

    public double getWeight() {
        return Weight;
    }

    public List<Item> getItems() {
        return items;
    }



    public List<StoreDiscount> getStoreDiscounts() {
        return StoreDiscounts;
    }


    public void setStoreDiscounts(List<StoreDiscount> storeDiscounts) {
        StoreDiscounts = storeDiscounts;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setSalePrice(double salePrice)
    {
        this.salePrice = salePrice;
    }


    public void setMinQuantity(int minQuantity) {
        MinQuantity = minQuantity;
    }

    public double getRealPrice() {
        return RealPrice;
    }

    public void setRealPrice(double realPrice) {
        RealPrice = realPrice;
    }


    public double getSize_ML() {
        return size_ML;
    }

    public void setSize_ML(double size_ML) {
        this.size_ML = size_ML;
    }

    public PackagingOption getPackagingOption() {
        return packagingOption;
    }

    public void setPackagingOption(PackagingOption packagingOption) {
        this.packagingOption = packagingOption;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    // a function that runs over items list and counts the amount on the shelf and in the warehouse
    public Map<location, Integer> countItemsByLocation() {
        Map<location, Integer> locationCounts = new HashMap<>();

        if (items == null || items.isEmpty()) {
            return locationCounts;
        }

        for (Item item : items) {
            location loc = item.getLocation();
            locationCounts.put(loc, locationCounts.getOrDefault(loc, 0) + 1);
        }

        return locationCounts;

    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    // show product details
    public void printProductDetails() {
        System.out.println(" Product Details");
        System.out.println("------------------------------");
        System.out.println(" ID: " + id);
        System.out.println(" Manufacturer: " + Manufacturer);
        System.out.println(" Name: " + name);
        System.out.println(" Weight: " + Weight + " kg");
        System.out.println(" Sale Price: " + salePrice + " ₪");
        System.out.println(" Min Quantity: " + MinQuantity);
        System.out.println(" Current Quantity: " + getCurrQuantity());
        System.out.println(" Unit type : " + getPackagingOption());
        if(getPackagingOption() == PackagingOption.BOTH || getPackagingOption() == PackagingOption.BOX_ONLY){
            System.out.println(" Units in one box : " + getBoxUnits());
        }

        System.out.println("\n Item distribution by location:");
        int shelfCount;
        int warhouseCount;
        Map<location, Integer> counts = countItemsByLocation();
        if (counts.isEmpty()) {
             shelfCount = 0;
             warhouseCount = 0;
        } else {
             shelfCount = counts.getOrDefault(location.Shelf, 0);
             warhouseCount = counts.getOrDefault(location.Warehouse, 0);

        }

        System.out.println("  Shelf: " + shelfCount);
        System.out.println("  Warhouse: " + warhouseCount);

        System.out.println("------------------------------\n");
    }

    // find item by itemId
    public Item findItemById(int itemId) {
        if (items == null || items.isEmpty()) {
            return null; // אין פריטים בכלל
        }

        for (Item item : items) {
            if (item.getItemId() == itemId) {
                return item; // מצאנו את הפריט
            }
        }

        return null;
    }

    // remove item from items list
    public boolean removeItem(Item item) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        boolean removed = items.remove(item);
        return removed;
    }

    //////////////////////////////////////////////////////////////////////////////

    public void setBoxUnits(int boxUnits) {
        if (boxUnits < 0) {
            throw new IllegalArgumentException("Box units cannot be negative.");
        }
        this.boxUnits = boxUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product P = (Product) o;
        return id == P.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Sets the unit type after validating it is not null.
     *
     * @param unitType The unit type to set.
     */
    public void setUnitType(UnitType unitType) {
        if (unitType == null) {
            throw new IllegalArgumentException("Unit type cannot be null.");
        }
        this.unitType =unitType;
    }

    public int getBoxUnits() {
        if(boxUnits != 0){

        }
        return boxUnits;
    }



    // adding to the product discount list a new discount
    public boolean applyDiscountToProduct(int DiscountID, double percent, LocalDate start, LocalDate end) throws SQLException {
        for (StoreDiscount existingDiscount : getStoreDiscounts()) {
            LocalDate existingStart = existingDiscount.getStartDate();
            LocalDate existingEnd = existingDiscount.getEndDate();

            if (!(end.isBefore(existingStart) || start.isAfter(existingEnd))) {
                System.out.println("There is already a discount active for this product during these dates.");
                return false;
            }
        }

        StoreDiscount discount = new StoreDiscount(DiscountID, percent, DiscountType.product, start, end);
        getStoreDiscounts().add(discount);
        ////////////////
        applyBestDiscount(null, LocalDate.now());
        return true;
    }

    // applying thr discount from the top of the hierarchy
    public void applyBestDiscount(StoreDiscount categoryDiscount, LocalDate today) {
        if (categoryDiscount != null) {
            // parent category
            double discountedPrice = getRealPrice() * (1 - categoryDiscount.getPercentDiscount() / 100.0);
            setSalePrice(discountedPrice);
        } else {

            StoreDiscount productDiscount = null;
            for (StoreDiscount discount : getStoreDiscounts()) {
                if (discount.getCategoryOrProduct() == DiscountType.product &&
                        (today.isEqual(discount.getStartDate()) || today.isAfter(discount.getStartDate())) &&
                        (today.isEqual(discount.getEndDate()) || today.isBefore(discount.getEndDate()))) {
                    productDiscount = discount;
                    break;
                }
            }

            if (productDiscount != null) {
                double discountedPrice = getRealPrice() * (1 - productDiscount.getPercentDiscount() / 100.0);
                setSalePrice(discountedPrice);
            } else {
                // setting the price to the pld price
                setSalePrice(getRealPrice());
            }
        }
    }

    // adding items to product
    public List<Item> addItemsToProduct( int count, double buyPrice, LocalDate expDate) {
        List<Item> newItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int itemId = Item.generateUniqueItemId();
            Item item = new Item(buyPrice, itemId, expDate, DefectiveStatus.GoodCondition, location.Warehouse, this.id);
            getItems().add(item);
            newItems.add(item);
            System.out.println("Created item with ID: " + itemId);
        }
            // setting the new quantity
            setCurrQuantity(getCurrQuantity());

        return newItems;
    }


}
