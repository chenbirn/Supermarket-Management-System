package domainLayer;

import DTO.ProductDto;
import enums.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;
    private final List<Item> defectiveItems = new ArrayList<>();

    // constructor for controler
    public Controller(ICategoryRepository categoryRepo, IProductRepository productRepo) {
        this.categoryRepository = categoryRepo;
        this.productRepository = productRepo;
    }

    //// functions for the report menu ////

    //// InventoryReport ////
    //function that produces a report of the inventory in the store based on categories names
    public void InventoryReport(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("No input provided.");
            return;
        }

        input = input.trim();

        if (!input.contains(",") && input.split("\\s+").length > 1) {
            System.out.println("Multiple category names detected without commas. Please separate them using commas.");
            return;
        }

        List<String> categoryNames = new ArrayList<>();

        if (input.contains(",")) {
            String[] names = input.split(",");
            for (String name : names) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    categoryNames.add(trimmed);
                }
            }
        } else {
            if (!input.isEmpty()) {
                categoryNames.add(input);
            }
        }

        if (categoryNames.isEmpty()) {
            System.out.println("No valid category names were entered.");
        } else {
            printProductsByCategoryNames(categoryNames);
        }
    }

    //// orderReport ////
    // function that produces a report of items we need to order and the amount
    public void orderReport() throws SQLException {
        List<Product> products = productRepository.findAllProducts();
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getCurrQuantity() <= p.getMinQuantity()) {
                lowStock.add(p);
            }
        }

        if (lowStock.isEmpty()) {
            System.out.println("No products need to be ordered.");
        } else {
            for (Product p : lowStock) {
                //  int toOrder = p.getMaxQuantity() - p.getCurrQuantity();
                System.out.println("Product ID: " + p.getId());
                System.out.println("Name: " + p.getName());
                System.out.println("Min: " + p.getMinQuantity() + ", Current: " + p.getCurrQuantity());
                //  System.out.println("Order: " + toOrder + " units");
                System.out.println("-----------------------------");
            }
        }
    }


    //// DefectiveReport ////
    // creating the defective report
    public void DefectiveReport() throws SQLException {
        collectExpiredItems();
        for (Product P : productRepository.findAllProducts()) {
            List<Item> items = new ArrayList<>(P.getItems());
            for (Item item : items) {
                if (item.getStatus() == DefectiveStatus.Defective) {
                    defectiveItems.add(item);
                    P.removeItem(item);
                }
            }
        }
        if (defectiveItems.isEmpty()) {
            System.out.println("No defective items found.");
            return;
        }
        System.out.println("Defective Items Report:");
        for (Item item : defectiveItems) {
            item.printItemDetails();
        }
        productRepository.deleteItem(defectiveItems);
        defectiveItems.clear();
    }


    public List<Product> findAllBelowMinQuantity() {
        return productRepository.findAllBelowMinQuantity();

    }


    // function that prints the products inside a category
    public void printProductsByCategoryNames(List<String> categoryNames) {
        categoryRepository.printProductsByCategoryNames(categoryNames);

    }


    // function that goes over all the items in the store and check if an item is expired
    public void collectExpiredItems() throws SQLException {
        List<Item> expired = productRepository.findAndRemoveExpiredItems();
        defectiveItems.addAll(expired);
        //System.out.println("Expired items collected and removed.");
    }


    // finds a product based on his id in the categories
    public Product findProductById(int id) throws SQLException {
        return productRepository.findProduct(id);
    }

    // finds a category based on the name
    private Category findCategoryByNameInHierarchy(String name) {
        return categoryRepository.findCategoryByNameInHierarchy(name);
    }

    // adding to the category discount list a new discount
    public boolean applyDiscountToCategory(int DiscountID, String mainCategory, String subCategory, String sizeCategory, double discountPercent, LocalDate start, LocalDate end) {
        return categoryRepository.applyDiscountToCategory(DiscountID, mainCategory, subCategory, sizeCategory, discountPercent, start, end);
    }

    public void UpdateStatusProduct(int product_id) throws SQLException {
        productRepository.setStatus(product_id);

    }

    public productOrder getStatus(int product_id) throws SQLException {
        return productRepository.getStatus(product_id);
    }


    // adding to the product discount list a new discount
    public boolean applyDiscountToProduct(int DiscountID, int productId, double percent, LocalDate start, LocalDate end) throws SQLException {
        return productRepository.applyDiscountToProduct(DiscountID, productId, percent, start, end);

    }


    //printing the product details
    public void printProductDetails(int productId) throws SQLException {
        productRepository.printProductDetails(productId);

    }

    // updating the product price
    public boolean updateProductPrice(int productId, double newPrice) throws SQLException {
        return productRepository.updateProductPrice(productId, newPrice);
    }

    // updating the product weight
    public boolean updateProductWeight(int id, double weight) throws SQLException {
        return productRepository.updateProductWeight(id, weight);
    }

    // updating the amount in a box
    public boolean updateProductBoxUnits(int productId, int newBoxUnits) throws SQLException {
        return productRepository.updateProductBoxUnits(productId, newBoxUnits);
    }


    // adding items to product
    public void addItemsToProduct(int productId, int count, double buyPrice, LocalDate expDate) throws SQLException {
        productRepository.addItemsToProduct(productId, count, buyPrice, expDate);

    }

    // report item is defective
    public boolean reportItemAsDefective(int productId, int itemId) throws SQLException {
        Product product = productRepository.findProduct(productId);
        if (product != null) {
            Item item = product.findItemById(itemId);
            if (item != null) {
                defectiveItems.add(item);
                product.removeItem(item);
                return true;
            }
        }
        return false;
    }

    // function to remove items from a product list if they were sold in FIFO
    public boolean removeOldestItemsFromProduct(int productId, int count) throws SQLException {
        return productRepository.removeOldestItemsFromProduct(productId, count);
    }

    public boolean moveItemsFromWarehouseToShelf(int productId, int count) throws SQLException {

        return productRepository.moveItemsFromWarehouseToShelf(productId, count);
    }


    public Product addProduct(ProductDto dto) throws SQLException {
        return productRepository.createProduct(dto);

    }

    public void updateProductPricesBasedOnCategoryDiscounts() {
        categoryRepository.updateProductPricesBasedOnCategoryDiscounts();
        productRepository.updateDetails();
    }

    public void updatePackageType(int id, String type) throws SQLException {
        productRepository.updatePackageType(id, type);
    }


}


