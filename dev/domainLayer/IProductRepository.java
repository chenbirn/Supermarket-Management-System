package domainLayer;

import DTO.*;
import enums.productOrder;

import java.time.LocalDate;
import java.util.*;
import java.sql.SQLException;

public interface IProductRepository {
    Product createProduct(ProductDto dto) throws SQLException;

    Product findProduct(int id) throws SQLException;

    List<Product> findAllProducts() throws SQLException;

    // void updateProductQuantity(int id, int quantity) throws SQLException;
    boolean updateProductPrice(int id, double price) throws SQLException;

    public boolean updateProductBoxUnits(int productId, int newBoxUnits) throws SQLException;

    public boolean updateProductWeight(int productId, double newWeight) throws SQLException;

    void deleteProduct(int id) throws SQLException;

    List<Product> findAllBelowMinQuantity();

    List<Item> findAndRemoveExpiredItems();

    void setStatus(int product_id) throws SQLException;

    productOrder getStatus(int productId) throws SQLException;

    //
    public boolean moveItemsFromWarehouseToShelf(int productId, int count) throws SQLException;

    //
    public boolean removeOldestItemsFromProduct(int productId, int count) throws SQLException;

    public boolean applyDiscountToProduct(int DiscountID, int productId, double percent, LocalDate start, LocalDate end) throws SQLException;

    public void printProductDetails(int productId) throws SQLException;

    public void addItemsToProduct(int productId, int count, double buyPrice, LocalDate expDate) throws SQLException;

    void deleteItem(List<Item> defectiveItems);

    void updatePackageType(int id, String type)throws SQLException;

    void updateDetails();

    }
