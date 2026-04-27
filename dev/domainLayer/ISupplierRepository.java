package domainLayer;

import DTO.SupplierDTO;
import enums.DeliveryDays;
import enums.PaymentType;
import enums.SupplierStatus;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface ISupplierRepository {

    Supplier createSupplier(SupplierDTO dto) throws SQLException;

    Supplier findSupplierById(int id) throws SQLException;

    List<Supplier> findAllSuppliers() throws SQLException;

    List<Supplier> findAllActiveSuppliers() throws SQLException;

    void changeStatus(int id, SupplierStatus status) throws SQLException;

    void deleteSupplier(int supplier_id) throws SQLException;

    public SupplierStatus getSupplierStatus(int id) throws SQLException;

    public void changeSupplierName(int supplier_id, String newName) throws SQLException;

    public void changeSupplierAddress(int supplier_id, String newAddress) throws SQLException;

    public void changeSupplierPaymentTypes(int supplier_id, EnumSet<PaymentType> newPaymentT) throws SQLException;

    public void changeSupplierBankAccount(int supplier_id, int newBankA) throws SQLException;

    public void addContactToSupplier(int supplier_id, Contact contact) throws SQLException;

    public void removeContactFromSupplier(int supplier_id, Contact contact) throws SQLException;

    public Set<DeliveryDays> getDeliveryDays(int supplier_id) throws SQLException;


}
