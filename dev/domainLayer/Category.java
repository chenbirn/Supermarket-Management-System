package domainLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// category class holds a list of products and discounts
// and sub categories
public class Category {
    private String Name;
    private Category parent;
    private List<Category> SubCategories;
    private List<Product> products;
    private List<StoreDiscount> StoreDiscounts;


    // constructor that receives only the name of the category
    public Category(String categoryName){
        Name = categoryName;
        this.parent = null;
        SubCategories = new ArrayList<>();
        products = new ArrayList<>();
        StoreDiscounts = new ArrayList<>();

    }

    // constructor that receives the name of the category and the parent category
    public Category( String name,Category parent) {
        Name = name;
        this.parent = parent;
        SubCategories = new ArrayList<>();
        products = new ArrayList<>();
        StoreDiscounts = new ArrayList<>();
        parent.addSubcategory(this);
    }

    // Add a subcategory to a parent or sub category
    public void addSubcategory(Category subcategory) {
        this.SubCategories.add(subcategory);
    }

    // Add product only if it's a leaf category
    public void addProduct(Product product) {
        // function that check if a product is a leaf in the hierarchy
        if (!isLeaf()) {
            throw new IllegalStateException("Cannot add products to a non-leaf category.");
        }
        this.products.add(product);
    }

    // Print the hierarchy starting from this category
    public void printHierarchy(String indent) {
        System.out.println(indent + "- " + Name);
        for (Product p : products) {
            System.out.println(indent + "  • " + p.getName());
        }
        for (Category sub : SubCategories) {
            sub.printHierarchy(indent + "  ");
        }
    }

    // check if a category is the root of the hierarchy
    public boolean isRoot() {
        return parent == null;
    }

    // check if a category is the leaf in the hierarchy
    public boolean isLeaf() {
        return SubCategories.isEmpty();
    }

    //// getters and setters ////
    public List<StoreDiscount> getStoreDiscounts() {
        return StoreDiscounts;
    }

    public void setStoreDiscounts(List<StoreDiscount> storeDiscounts) {
        StoreDiscounts = storeDiscounts;
    }

    public String getName() {
        return Name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Category> getSubCategories() {
        return SubCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        SubCategories = subCategories;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

}
