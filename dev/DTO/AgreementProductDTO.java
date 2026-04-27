package DTO;

public record AgreementProductDTO(
        String supplyItem_id,
        int product_id,
        int agreement_id,
        double price
) {}
