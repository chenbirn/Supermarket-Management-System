package DTO;

import enums.DiscountMethod;

public record DiscountByQuantityDTO(
        String supplyItem_id,
        double discount,
        int quantity,
        DiscountMethod dtype
) {}
