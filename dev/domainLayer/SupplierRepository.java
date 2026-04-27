package domainLayer;

import DTO.SupplierDTO;
import enums.*;
import dataAccessLayer.SupplierDaoSQLite;

import java.sql.SQLException;
import java.util.*;

public class SupplierRepository implements ISupplierRepository {

    private final Map<Integer, Supplier> supplierStorage = new HashMap<>();
    private final SupplierDaoSQLite supplierDao;

    public SupplierRepository(SupplierDaoSQLite supplierDAO) throws SQLException {
        this.supplierDao = supplierDAO;
        loadAllFromDB();
    }

    public void loadAllFromDB() throws SQLException {
        List<SupplierDTO> dtos = supplierDao.findAll();

        for (SupplierDTO dto : dtos) {
            List<Contact> contacts = new ArrayList<>();

            contacts.add(new Contact(dto.contact1(), dto.phone_number1()));

            if (dto.contact2() != null && dto.phone_number2() != null) {
                contacts.add(new Contact(dto.contact2(), dto.phone_number2()));
            }

            Supplier supplier = SupplierMapper.toEntity(dto);
            supplierStorage.put(supplier.getSupplier_id(), supplier);
        }
    }

    @Override
    public Supplier createSupplier(SupplierDTO dto) throws SQLException {
        //create contacts list
        List<Contact> contacts = new ArrayList<>();

        contacts.add(new Contact(dto.contact1(), dto.phone_number1()));

        if (dto.contact2() != null && dto.phone_number2() != null) {
            contacts.add(new Contact(dto.contact2(), dto.phone_number2()));
        }
        Supplier newSupplier = new Supplier(dto.name(), dto.address(), dto.supplier_id(), dto.paymentType(), dto.bankAccount(), contacts);
        supplierStorage.put(newSupplier.getSupplier_id(), newSupplier);
        supplierDao.save(SupplierMapper.toDto(newSupplier));
        return newSupplier;
    }

    @Override
    public Supplier findSupplierById(int id) throws SQLException {
        return supplierStorage.get(id);
    }

    @Override
    public List<Supplier> findAllSuppliers() throws SQLException {
        return  new ArrayList<>(supplierStorage.values());
    }

    @Override
    public List<Supplier> findAllActiveSuppliers() throws SQLException {
        List<Supplier> activeSuppliers = new ArrayList<>();
        for (Supplier supplier : supplierStorage.values())
            if (supplier.getSupplierStatus().equals(SupplierStatus.ACTIVE))
                activeSuppliers.add(supplier);
        return activeSuppliers;
    }

    @Override
    public void changeStatus(int id, SupplierStatus status) throws SQLException {
        Supplier supplier = supplierStorage.get(id);
        supplier.setSupplierStatus(status);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public void deleteSupplier(int supplier_id) throws SQLException {
        supplierStorage.remove(supplier_id);
        supplierDao.delete(supplier_id);
    }

    @Override
    public SupplierStatus getSupplierStatus(int id) throws SQLException {
        Supplier supplier = supplierStorage.get(id);
        return supplier.getSupplierStatus();
    }

    @Override
    public void changeSupplierName(int supplier_id, String newName) throws SQLException {
        Supplier supplier = supplierStorage.get(supplier_id);
        supplier.setName(newName);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public void changeSupplierAddress(int supplier_id, String newAddress) throws SQLException {
        Supplier supplier = supplierStorage.get(supplier_id);
        supplier.setAddress(newAddress);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public void changeSupplierPaymentTypes(int supplier_id, EnumSet<PaymentType> newPaymentT) throws SQLException {
        Supplier supplier = supplierStorage.get(supplier_id);
        supplier.setPaymentType(newPaymentT);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public void changeSupplierBankAccount(int supplier_id, int newBankA) throws SQLException {
        Supplier supplier = supplierStorage.get(supplier_id);
        supplier.setBankAccount(newBankA);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public void addContactToSupplier(int supplier_id, Contact contact) throws SQLException {
        Supplier supplier = supplierStorage.get(supplier_id);
        supplier.addContact(contact);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public void removeContactFromSupplier(int supplier_id, Contact contact) throws SQLException {
        Supplier supplier = supplierStorage.get(supplier_id);
        supplier.removeContact(contact);
        supplierDao.update(SupplierMapper.toDto(supplier));
    }

    @Override
    public Set<DeliveryDays> getDeliveryDays(int supplier_id) throws SQLException {
        Supplier supplier = findSupplierById(supplier_id);
        List<DeliveryDays> deliveryDays = new ArrayList<>();
        for (Agreement agreement : supplier.getSignedAgreements())
            deliveryDays.addAll(agreement.getDeliveryDays());
        return new HashSet<>(deliveryDays);
    }
}
