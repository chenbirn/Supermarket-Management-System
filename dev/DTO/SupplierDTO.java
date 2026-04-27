package DTO;

import domainLayer.Agreement;
import domainLayer.Contact;
import domainLayer.Order;
import enums.PaymentType;
import enums.SupplierStatus;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public record SupplierDTO(
        int supplier_id,
        String name,
        String address,
        EnumSet<PaymentType>paymentType,
        int bankAccount,
        SupplierStatus supplierStatus,
        // 1-2 contacts per supplier_id
        String contact1,
        String phone_number1,
        String contact2,
        String phone_number2
) {
}
