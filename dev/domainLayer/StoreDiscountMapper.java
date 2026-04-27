package domainLayer;

import DTO.StoreDiscountDTO;
import enums.DiscountType;

public class StoreDiscountMapper {

    // DTO → Entity
    public static StoreDiscount toEntity(StoreDiscountDTO dto) {
        return new StoreDiscount(
                dto.discountId(),
                dto.percentDiscount(),
                dto.categoryOrProduct(),
                dto.startDate(),
                dto.endDate()
        );
    }

    // Entity → DTO
    public static StoreDiscountDTO toDto(StoreDiscount entity, Integer productId,
                                         String mainCategoryName, String subCategoryName, String sizeCategoryName) {
        return new StoreDiscountDTO(
                entity.getDiscountId(),
                entity.getPercentDiscount(),
                entity.getCategoryOrProduct(),
                entity.getStartDate(),
                entity.getEndDate(),
                productId,
                mainCategoryName,
                subCategoryName,
                sizeCategoryName
        );
    }
}

