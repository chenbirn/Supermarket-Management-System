package domainLayer;

import DTO.AgreementDTO;

public class AgreementMapper {

    // Converts AgreementDTO into an Agreement object
    public static Agreement toEntity(AgreementDTO dto, Supplier supplier) {
        // Create Agreement using data from the DTO
        Agreement agreement = new Agreement(
                dto.agreement_id(),
                dto.deliveryDays(),
                dto.deliveryMethod(),
                supplier
        );
        agreement.setStatus(dto.status());
        return agreement;
    }

    // Converts an Agreement domain object to an AgreementDTO.
    public static AgreementDTO toDto(Agreement agreement) {
        return new AgreementDTO(
                agreement.getAgreement_id(),
                agreement.getSupplier().getSupplier_id(),
                agreement.getDeliveryDays(),
                agreement.getDeliveryMethod(),
                agreement.getStatus()
        );
    }
}
