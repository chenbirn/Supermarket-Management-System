package domainLayer;
import DTO.ItemDTO;
import DTO.ProductDto;
import DTO.StoreDiscountDTO;
import dataAccessLayer.ItemDaoSQLite;
import dataAccessLayer.ProductDaoSQLite;
import dataAccessLayer.StoreDiscountDaoSQLite;
import enums.PackagingOption;
import enums.location;
import enums.productOrder;
import enums.DiscountType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ProductRepository implements IProductRepository {
    private final Map<Integer, Product> productStorage = new HashMap<>();
    private final ProductDaoSQLite productDao;
    private final ICategoryRepository categoryRepository;
    private final ItemDaoSQLite itemDao;
    private final StoreDiscountDaoSQLite storeDiscountDao;


    public ProductRepository(ICategoryRepository categoryRepository, ProductDaoSQLite productDao, ItemDaoSQLite itemDao, StoreDiscountDaoSQLite storeDiscountDao) {
        this.categoryRepository = categoryRepository;
        this.productDao = productDao;
        this.itemDao = itemDao;
        this.storeDiscountDao = storeDiscountDao;
        loadAllFromDB();
    }
    // loading all the data from the tables
    public void loadAllFromDB() {
        List<ProductDto> dtos = productDao.findAll();
        for (ProductDto dto : dtos) {
            Product p = ProductMapper.toEntity(dto);
            productStorage.put(p.getId(), p);

            Category c = categoryRepository.findOrCreateFullCategory(
                    dto.mainCategoryName(),
                    dto.subCategoryName(),
                    dto.sizeCategoryName()
            );
            c.addProduct(p);
        }
        List<ItemDTO> itemDtos = itemDao.findAll();
        for (ItemDTO itemDto : itemDtos) {
            Item item = ItemMapper.toEntity(itemDto);
            Product product = productStorage.get(item.getProductId());
            if (product != null) {
                product.getItems().add(item);
            }
        }
        for (Product p : productStorage.values()) {
            p.setCurrQuantity(p.getItems().size());
        }

        List<StoreDiscountDTO> discountDTOs = storeDiscountDao.findAll();
        for (StoreDiscountDTO dto : discountDTOs) {
            if (dto.productId() != null) {
                Product p = productStorage.get(dto.productId());
                if (p != null) {
                    p.getStoreDiscounts().add(StoreDiscountMapper.toEntity(dto));
                }
            }
        }

    }

        @Override
        public Product createProduct (ProductDto dto){
            Product p = ProductMapper.toEntity(dto);
            productStorage.put(p.getId(), p);
            productDao.save(dto);

            // adding to the category
            Category c = categoryRepository.findOrCreateFullCategory(
                    dto.mainCategoryName(),
                    dto.subCategoryName(),
                    dto.sizeCategoryName()
            );
            c.addProduct(p);  // adding the product

            return p;
        }
        // finding the product
        @Override
        public Product findProduct ( int id) throws SQLException {
            Product product = productStorage.get(id);
            if (product == null) {
                throw new SQLException("Product with ID " + id + " not found.");
            }
            return product;
        }

        @Override
        public List<Product> findAllProducts () throws SQLException {
            return new ArrayList<>(productStorage.values());
        }




        //delete the product
        @Override
        public void deleteProduct ( int id) throws SQLException {
            if (!productStorage.containsKey(id)) {
                throw new SQLException("Cannot delete: Product with ID " + id + " not found.");
            }
            productStorage.remove(id);
            //delete from DB
            productDao.delete(id);
        }
        // find products that below minimum quantity
        public List<Product> findAllBelowMinQuantity () {
            List<Product> result = new ArrayList<>();
            for (Product p : productStorage.values()) {
                if (p.getCurrQuantity() <= p.getMinQuantity()) {
                    result.add(p);
                }
            }
            return result;
        }
        // finding all expired items
        public List<Item> findAndRemoveExpiredItems () {
            LocalDate today = LocalDate.now();
            List<Item> expiredItems = new ArrayList<>();

            for (Product p : productStorage.values()) {
                List<Item> items = p.getItems();
                List<Item> toRemove = new ArrayList<>();
                for (Item item : items) {
                    if (item.getExpirationDate().isBefore(today)) {
                        expiredItems.add(item);
                        toRemove.add(item);
                    }
                }
                items.removeAll(toRemove);
                p.setCurrQuantity(p.getItems().size());
                productDao.update(ProductMapper.toDto(p));
            }

            return expiredItems;
        }

        public void deleteItem (List < Item > itemsToRemove) {
            for (Item item : itemsToRemove) {
                itemDao.delete(item.getItemId());
            }
        }

        public void setStatus ( int product_id) throws SQLException {
            Product p = findProduct(product_id);
            p.setStatus();
            productDao.update(ProductMapper.toDto(p));
        }

        public productOrder getStatus ( int product_id) throws SQLException {
            Product p = findProduct(product_id);
            return p.getStatus();

        }

        // updating the product price
        public boolean updateProductPrice ( int productId, double newPrice) throws SQLException {
            Product product = findProduct(productId);
            if (product != null) {
                product.setSalePrice(newPrice);
                productDao.update(ProductMapper.toDto(product));
                return true;
            }
            return false;
        }
        // updating the weight of the product
        public boolean updateProductWeight(int productId, double newWeight) throws SQLException {
            Product product = findProduct(productId);
            if (product != null) {
                product.setWeight(newWeight);
                productDao.update(ProductMapper.toDto(product));
                return true;
            }
            return false;
        }
        // updating the box units of the product
        public boolean updateProductBoxUnits(int productId, int newBoxUnits) throws SQLException {
            Product product = findProduct(productId);
            if (product != null) {
                product.setBoxUnits(newBoxUnits);
                productDao.update(ProductMapper.toDto(product));
                return true;
            }
            return false;
        }

        // function to move items from the warehouse to the shelf
        public boolean moveItemsFromWarehouseToShelf ( int productId, int count) throws SQLException {
            Product product = findProduct(productId);
            if (product != null) {
                int moved = 0;
                for (Item item : product.getItems()) {
                    if (item.getLocation() == location.Warehouse) {
                        item.setLocation(location.Shelf);
                        itemDao.update(ItemMapper.toDto(item)); // רק כאן
                        System.out.println("item id: "+item.getItemId()+" is moved from Warehouse to Shelf");
                        moved++;
                        if (moved == count) {
                            break;
                        }
                    }
                }
                return moved == count;
            }
            return false;
        }

        public boolean removeOldestItemsFromProduct ( int productId, int count) throws SQLException {
            Product product = findProduct(productId);
            if (product != null) {
                List<Item> items = product.getItems();
                if (items.size() >= count) {
                    for (int i = 0; i < count; i++) {
                        Item removed = items.remove(0);
                        itemDao.delete(removed.getItemId());
                        System.out.println("Item with ID: " + removed.getItemId() + " has been removed");
                    }
                    product.setCurrQuantity(product.getCurrQuantity());/////////////////////////////////
                    productDao.update(ProductMapper.toDto(product));
                    return true;
                }
            }
            return false;
        }

        // adding to the product discount list a new discount
        public boolean applyDiscountToProduct ( int DiscountID, int productId, double percent, LocalDate
        start, LocalDate end) throws SQLException {
            Product product = findProduct(productId);
            boolean result = product.applyDiscountToProduct(DiscountID, percent, start, end);
                if (!result) return false;

                StoreDiscountDTO dto = new StoreDiscountDTO(
                        DiscountID,
                        percent,
                        DiscountType.product,
                        start,
                        end,
                        productId,
                        null, // main_category_name
                        null, // sub_category_name
                        null  // size_category_name
                );
            storeDiscountDao.save(dto);
            productDao.update(ProductMapper.toDto(product));
            return result;
        }


        //printing the product details
        public void printProductDetails ( int productId) throws SQLException {
            Product product = findProduct(productId);
            if (product != null) {
                product.printProductDetails();
            } else {
                System.out.println("The product does not exist.");
            }
        }

        // adding items to product
        public void addItemsToProduct ( int productId, int count, double buyPrice, LocalDate expDate) throws
        SQLException {
            Product product = findProduct(productId);
            List<Item> newItems = product.addItemsToProduct(count, buyPrice, expDate);
            for (Item item : newItems) {
                itemDao.save(ItemMapper.toDto(item));
            }
            product.setCurrQuantity(product.getItems().size());
            productDao.update(ProductMapper.toDto(product));


        }
        // updating the package type
        public void updatePackageType(int id,String type) throws SQLException {
            PackagingOption packagingOption;

            if (type.equals("single units")) {
                packagingOption = PackagingOption.SINGLE_UNITS;
            } else if (type.equals("box only")) {
                packagingOption = PackagingOption.BOX_ONLY;
            } else if (type.equals("both options")) {
                packagingOption = PackagingOption.BOTH;
            } else {
                throw new IllegalArgumentException("Invalid packaging option: " + type);
            }
            Product product = findProduct(id);
            product.setPackagingOption(packagingOption);
            productDao.update(ProductMapper.toDto(product));
        }

        public void updateDetails(){
            for (Product product : productStorage.values()) {
                productDao.update(ProductMapper.toDto(product));
            }
        }

    }






