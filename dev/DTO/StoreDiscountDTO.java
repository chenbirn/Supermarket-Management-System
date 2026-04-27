package DTO;

import enums.DiscountType;
import java.time.LocalDate;

public record StoreDiscountDTO(
        int discountId,
        double percentDiscount,
        DiscountType categoryOrProduct,
        LocalDate startDate,
        LocalDate endDate,
        Integer productId,            // nullable
        String mainCategoryName,     // nullable
        String subCategoryName,      // nullable
        String sizeCategoryName      // nullable
) {}

