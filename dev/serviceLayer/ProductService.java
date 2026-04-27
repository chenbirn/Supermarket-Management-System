package serviceLayer;

import DTO.ProductDto;
import domainLayer.MainController;
import domainLayer.Product;

import java.sql.SQLException;
import java.time.LocalDate;
// Product service layer – handles business logic and passes the calls to the main controller.
public class ProductService {
    private final MainController con;

    public ProductService(MainController con) {
        this.con = con;
    }

    public void InventoryReport(String input) {
        con.InventoryReport(input);
    }

    public void orderReport() throws SQLException {
        con.orderReport();
    }

    public void DefectiveReport() throws SQLException {
        con.DefectiveReport();
    }

    public void printProductDetails(int productId) throws SQLException {
        con.printProductDetails(productId);
    }

    public Product addProduct(ProductDto dto) throws SQLException {
        return con.addProduct(dto);

    }

    public boolean updateProductPrice(int productId, double newPrice) throws SQLException {
        return con.updateProductPrice(productId, newPrice);
    }

    public boolean addItemsToProduct(int productId, int count, double buyPrice, LocalDate expDate) throws SQLException {
        return con.addItemsToProduct(productId, count, buyPrice, expDate);
    }

    public boolean reportItemAsDefective(int productId, int itemId) throws SQLException {
        return con.reportItemAsDefective(productId, itemId);
    }

    public boolean removeOldestItemsFromProduct(int productId, int count) throws SQLException {
        return con.removeOldestItemsFromProduct(productId, count);
    }

    public boolean moveItemsFromWarehouseToShelf(int productId, int count) throws SQLException {
        return con.moveItemsFromWarehouseToShelf(productId, count);
    }

    public boolean updateProductWeight(int id, double weight) throws SQLException {
        return con.updateProductWeight(id, weight);
    }

    public boolean updateProductBoxUnits(int productId, int newBoxUnits) throws SQLException {
        return con.updateProductBoxUnits(productId, newBoxUnits);
    }

    public boolean applyDiscountToProduct(int DiscountID, int productId, double percent, LocalDate start, LocalDate end) throws SQLException {
        return con.applyDiscountToProduct(DiscountID, productId, percent, start, end);
    }

    public boolean applyDiscountToCategory(int DiscountID, String mainCategory, String subCategory, String sizeCategory, double discountPercent, LocalDate start, LocalDate end) {
        return con.applyDiscountToCategory(DiscountID, mainCategory, subCategory, sizeCategory, discountPercent, start, end);
    }

    public void updateProductPricesBasedOnCategoryDiscounts() {
        con.updateProductPricesBasedOnCategoryDiscounts();
    }
    public Product findProductById(int id) throws SQLException {
        return con.findProductById(id);
    }
    public void updatePackageType(int id,String type)throws SQLException{
        con.updatePackageType( id, type);
    }
}
