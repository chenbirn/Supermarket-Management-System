package domainLayer;
import DTO.ItemDTO;
import enums.DefectiveStatus;
import enums.location;

import java.time.LocalDate;
public class ItemMapper {
    // DTO → Entity
    public static Item toEntity(ItemDTO dto) {
        return new Item(
                dto.BuyPrice(),
                dto.ItemId(),
                dto.ExpirationDate(),
                dto.Status(),
                dto.location(),
                dto.productId()
        );
    }

    // Entity → DTO
    public static ItemDTO toDto(Item item) {
        return new ItemDTO(
                item.getItemId(),
                item.getBuyPrice(),
                item.getExpirationDate(),
                item.isStatus(),
                item.getLocation(),
                item.getProductId()
        );
    }
}
