package domainLayer;

import DTO.SupplierDTO;
import enums.PaymentType;
import enums.SupplierStatus;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class SupplierMapper {

    public static SupplierDTO toDto(Supplier supplier) {
        List<Contact> contacts = supplier.getContacts();

        String contactName1 = contacts.get(0).getName();
        String contactPhone1 = contacts.get(0).getPhoneNumber();

        //if supplier has only 1 contact, the second would be null
        String contactName2 = contacts.size() > 1 ? contacts.get(1).getName() : null;
        String contactPhone2 = contacts.size() > 1 ? contacts.get(1).getPhoneNumber() : null;

        return new SupplierDTO(
                supplier.getSupplier_id(),
                supplier.getName(),
                supplier.getAddress(),
                supplier.getPaymentType(),
                supplier.getBankAccount(),
                supplier.getSupplierStatus(),
                contactName1,
                contactPhone1,
                contactName2,
                contactPhone2
        );
    }

    public static Supplier toEntity(SupplierDTO dto) {
        List<Contact> contacts = new ArrayList<>();

        if (dto.contact1() != null && !dto.contact1().trim().isEmpty()
                && dto.phone_number1() != null && !dto.phone_number1().trim().isEmpty()) {
            contacts.add(new Contact(dto.contact1(), dto.phone_number1()));
        }

        if (dto.contact2() != null && !dto.contact2().trim().isEmpty()
                && dto.phone_number2() != null && !dto.phone_number2().trim().isEmpty()) {
            contacts.add(new Contact(dto.contact2(), dto.phone_number2()));
        }

        return new Supplier(
                dto.name(),
                dto.address(),
                dto.supplier_id(),
                dto.paymentType(),
                dto.bankAccount(),
                contacts,
                dto.supplierStatus()
        );
    }

}
