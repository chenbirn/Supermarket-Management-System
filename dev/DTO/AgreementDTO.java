package DTO;

import enums.AgreementStatus;
import enums.DeliveryDays;
import enums.DeliveryMethod;

import java.util.Collection;
import java.util.List;

public record AgreementDTO(
        int agreement_id,
        int supplier_id,
        List<DeliveryDays> deliveryDays,
        DeliveryMethod deliveryMethod,
        AgreementStatus status
        ) {}
