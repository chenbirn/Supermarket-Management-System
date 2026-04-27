package domainLayer;

import DTO.ProductDto;

import java.util.ArrayList;

public class ProductMapper {
    public static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getManufacturer(),
                product.getName(),
                product.getRealPrice(),
                product.getSalePrice(),
                product.getWeight(),
                product.getSize_ML(),
                product.getMinQuantity(),
                product.getCurrQuantity(),
                product.getFrequncy(),
                product.getItems(),
                product.getStoreDiscounts(),
                product.getPackagingOption(),
                product.getUnitType(),
                product.getBoxUnits(),
                product.getMainCategoryName(),
                product.getSubCategoryName(),
                product.getSizeCategoryName()
        );
    }

    public static Product toEntity(ProductDto dto) {
        Product product = new Product(
                dto.id(),
                dto.manufacturer(),
                dto.name(),
                dto.realPrice(),
                dto.weight(),
                dto.mainCategoryName(),
                dto.subCategoryName(),
                dto.sizeCategoryName(),
                dto.minQuantity(),
                dto.frequency()
        );

        product.setSalePrice(dto.salePrice());

        if (dto.items() != null)
            product.setItems(dto.items());
        else
            product.setItems(new ArrayList<>());

        product.setStoreDiscounts(dto.storeDiscounts());
        product.setBoxUnits(dto.boxUnits());
        product.setUnitType(dto.unitType());
        product.setPackagingOption(dto.packagingOption());
        product.setSize_ML(dto.sizeML());
        product.setCurrQuantity(dto.currQuantity());

        return product;
    }

}
