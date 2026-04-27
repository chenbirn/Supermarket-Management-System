package domainLayer;

import DTO.StoreDiscountDTO;
import dataAccessLayer.StoreDiscountDaoSQLite;
import enums.DiscountType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements ICategoryRepository {
    private final List<Category> categories = new ArrayList<>();
    private final StoreDiscountDaoSQLite storeDiscountDao;


    public CategoryRepository(StoreDiscountDaoSQLite storeDiscountDao) {
        this.storeDiscountDao = storeDiscountDao;

    }

    // return category
    public List<Category> getAllCategories() {
        return categories;
    }

    // adding a main category
    public void addCategory(Category category) {
        categories.add(category);
    }

    // find category by name
    public Category findCategoryByNameInHierarchy(String name) {
        for (Category main : categories) {
            if (main.getName().equalsIgnoreCase(name)) return main;

            for (Category sub : main.getSubCategories()) {
                if (sub.getName().equalsIgnoreCase(name)) return sub;

                for (Category subsub : sub.getSubCategories()) {
                    if (subsub.getName().equalsIgnoreCase(name)) return subsub;
                }
            }
        }
        return null;
    }

    // delete by name
    public boolean deleteCategoryByName(String name) {
        for (Category main : categories) {
            if (main.getName().equalsIgnoreCase(name)) {
                return categories.remove(main);
            }

            for (Category sub : main.getSubCategories()) {
                if (sub.getName().equalsIgnoreCase(name)) {
                    return main.getSubCategories().remove(sub);
                }

                for (Category subsub : sub.getSubCategories()) {
                    if (subsub.getName().equalsIgnoreCase(name)) {
                        return sub.getSubCategories().remove(subsub);
                    }
                }
            }
        }
        return false;
    }

    // a function that find or create if isn't found for a category
    public Category findOrCreateFullCategory(String name, String sub, String size) {

        // finding or creating a parent category
        Category mainCategory = null;
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                mainCategory = category;
                break;
            }
        }

        if (mainCategory == null) {
            mainCategory = new Category(name);
            categories.add(mainCategory);
        }

        // finding or creating a sub category
        Category subCategory = null;
        for (Category cat : mainCategory.getSubCategories()) {
            if (cat.getName().equalsIgnoreCase(sub)) {
                subCategory = cat;
                break;
            }
        }

        if (subCategory == null) {
            subCategory = new Category(sub, mainCategory);
        }

        // finding or creating a size category
        Category sizeCategory = null;
        for (Category cat : subCategory.getSubCategories()) {
            if (cat.getName().equalsIgnoreCase(size)) {
                sizeCategory = cat;
                break;
            }
        }

        if (sizeCategory == null) {
            sizeCategory = new Category(size, subCategory);
        }

        return sizeCategory;
    }

    // function that prints the products inside a category
    public void printProductsByCategoryNames(List<String> categoryNames) {

        if (categoryNames == null || categoryNames.isEmpty()) {
            System.out.println("No category names were provided.");
            return;
        }
        boolean anyMatch = false;
        for (String name : categoryNames) {
            Category found = findCategoryByNameInHierarchy(name);
            if (found != null) {
                System.out.println("Category: " + found.getName());
                printAllLeafProducts(found);
                anyMatch = true;
            }
        }

        if (!anyMatch) {
            System.out.println("No matching categories were found.");
        }
    }

    // print all the products in the categories
    private void printAllLeafProducts(Category rootCategory) {
        for (Category subCategory : rootCategory.getSubCategories()) {
            for (Category leafCategory : subCategory.getSubCategories()) {
                // check if it's a leaf (i.e., it has no further subcategories)
                if (leafCategory.isLeaf()) {
                    List<Product> products = leafCategory.getProducts();
                    if (products.isEmpty()) {
                        System.out.println("No products in category: " + buildCategoryPath(leafCategory));
                    } else {
                        for (Product product : products) {
                            System.out.println("Product name: " + product.getName());
                            System.out.println("Category path: " + buildCategoryPath(leafCategory));
                            System.out.println("Minimum quantity: " + product.getMinQuantity());
                            System.out.println("Current quantity: " + product.getCurrQuantity());
                            System.out.println("-----------------------------");
                        }
                    }
                }
            }
        }
    }

    // builds the category path of a product
    private String buildCategoryPath(Category category) {
        StringBuilder path = new StringBuilder(category.getName());
        Category parent = category.getParent();

        while (parent != null) {
            path.insert(0, parent.getName() + " > ");
            parent = parent.getParent();
        }

        return path.toString();
    }

    // adding to the category discount list a new discount
    public boolean applyDiscountToCategory(int discountId, String mainCategory, String subCategory, String sizeCategory, double discountPercent, LocalDate start, LocalDate end) {
        // מוצא את הקטגוריה בהיררכיה
        Category found = findFullCategory(mainCategory, subCategory, sizeCategory);
        if (found == null) {
            System.out.println("Category not found: " + mainCategory + " > " + subCategory + " > " + sizeCategory);
            return false;
        }

        // בדיקה אם יש הנחה חופפת בזמנים
        for (StoreDiscount existingDiscount : found.getStoreDiscounts()) {
            if (!(end.isBefore(existingDiscount.getStartDate()) || start.isAfter(existingDiscount.getEndDate()))) {
                System.out.println("There is already a discount active during these dates.");
                return false;
            }
        }

        // יצירת ההנחה והוספתה לקטגוריה
        StoreDiscount discount = new StoreDiscount(discountId, discountPercent, DiscountType.category, start, end);
        found.getStoreDiscounts().add(discount);
        StoreDiscountDTO dto = StoreDiscountMapper.toDto(
                discount,
                null,
                mainCategory,
                subCategory,
                sizeCategory
        );
        storeDiscountDao.save(dto);
        return true;
    }


    public Category findFullCategory(String main, String sub, String size) {
        for (Category mainCat : categories) {
            if (!mainCat.getName().equalsIgnoreCase(main)) continue;

            if (sub == null && size == null) return mainCat;

            for (Category subCat : mainCat.getSubCategories()) {
                if (!subCat.getName().equalsIgnoreCase(sub)) continue;

                if (size == null) return subCat;

                for (Category sizeCat : subCat.getSubCategories()) {
                    if (sizeCat.getName().equalsIgnoreCase(size)) {
                        return sizeCat;
                    }
                }
            }
        }
  return null;
    }


    //// discount updating ////
    public void updateProductPricesBasedOnCategoryDiscounts() {

        LocalDate today = LocalDate.now();

        for (Category mainCategory : categories) {
            StoreDiscount mainDiscount = findActiveDiscount(mainCategory, today);

            for (Category subCategory : mainCategory.getSubCategories()) {
                StoreDiscount subDiscount = mainDiscount != null ? mainDiscount : findActiveDiscount(subCategory, today);

                for (Category sizeCategory : subCategory.getSubCategories()) {
                    StoreDiscount sizeDiscount = subDiscount != null ? subDiscount : findActiveDiscount(sizeCategory, today);

                    List<Product> products = sizeCategory.getProducts();
                    if (products == null || products.isEmpty()) continue;

                    for (Product product : products) {
                        product.applyBestDiscount( sizeDiscount, today);
                    }
                }
            }
        }
    }

    // helper function to find an active discount
    private StoreDiscount findActiveDiscount(Category category, LocalDate today) {
        for (StoreDiscount discount : category.getStoreDiscounts()) {
            if (discount.getCategoryOrProduct() == DiscountType.category &&
                    (today.isEqual(discount.getStartDate()) || today.isAfter(discount.getStartDate())) &&
                    (today.isEqual(discount.getEndDate()) || today.isBefore(discount.getEndDate()))) {
                return discount;
            }
        }
        return null;
    }
}

