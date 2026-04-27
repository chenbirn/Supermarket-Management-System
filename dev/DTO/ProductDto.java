package DTO;

import domainLayer.Item;
import domainLayer.StoreDiscount;
import enums.PackagingOption;
import enums.UnitType;

import java.util.List;
/**
 * Data Transfer Object for Product – holds all product-related data.
 */
public record ProductDto(
        int id,
        String manufacturer,
        String name,
        double realPrice,
        double salePrice,
        double weight,
        double sizeML,
        int minQuantity,
        int currQuantity,
        int frequency,
        List<Item> items,
        List<StoreDiscount> storeDiscounts,
        PackagingOption packagingOption,
        UnitType unitType,
        int boxUnits,
        String mainCategoryName,
        String subCategoryName,
        String sizeCategoryName
) {}
