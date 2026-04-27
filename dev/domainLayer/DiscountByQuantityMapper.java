package domainLayer;

import DTO.DiscountByQuantityDTO;

public class DiscountByQuantityMapper {

    public static DiscountByQuantityDTO toDto(DiscountByQuantity entity, String supplyItem_id) {
        return new DiscountByQuantityDTO(
                supplyItem_id,
                entity.getDiscount(),
                entity.getQuantity(),
                entity.getDiscountMethod()
        );
    }

    public static DiscountByQuantity toEntity(DiscountByQuantityDTO dto) {
        return new DiscountByQuantity(
                dto.discount(),
                dto.quantity(),
                dto.dtype()
        );
    }

}
