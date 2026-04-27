package domainLayer;

import DTO.AgreementProductDTO;

public class AgreementProductMapper {

    // Convert a domain AgreementProduct to a DTO
    public static AgreementProductDTO toDto(AgreementProduct product, int agreement_id) {
        return new AgreementProductDTO(
                product.getSupplyItem_id(),
                product.getProduct().getId(),
                agreement_id,
                product.getPrice()
        );
    }

    // Convert a DTO to domain AgreementProduct
    public static AgreementProduct toEntity(AgreementProductDTO dto, Product product, Supplier supplier) {
        return new AgreementProduct(dto.price(), product, supplier);
    }
}
