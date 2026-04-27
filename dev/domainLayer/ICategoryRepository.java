package domainLayer;

import java.time.LocalDate;
import java.util.List;

public interface ICategoryRepository {
    List<Category> getAllCategories();
    void addCategory(Category category);
    Category findCategoryByNameInHierarchy(String name);
    Category findOrCreateFullCategory(String name, String sub, String size);
    boolean deleteCategoryByName(String name);
  //  public Product findProductById(int id);
    public void printProductsByCategoryNames(List<String> categoryNames);
    public boolean applyDiscountToCategory(int discountId, String mainCategory, String subCategory, String sizeCategory, double discountPercent, LocalDate start, LocalDate end);
    public void updateProductPricesBasedOnCategoryDiscounts();
    }
