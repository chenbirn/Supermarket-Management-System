package domainLayer;

import enums.*;
import dataAccessLayer.*;
import DTO.*;

import java.sql.SQLException;
import java.util.*;

public class AgreementRepository implements IAgreementRepository {
    private final Map<Integer, Agreement> agreementStorage = new HashMap<>();
    private final ISupplierRepository supplierRepository;
    private final IProductRepository productRepository;
    private final AgreementDao agreementDao;
    private final AgreementProductDao agreementProductDao;
    private final DiscountByQuantityDao discountByQuantityDao;


    public AgreementRepository(
            AgreementDao agreementDao,
            AgreementProductDao agreementProductDao,
            DiscountByQuantityDao discountByQuantityDao,
            ISupplierRepository supplierRepository,
            IProductRepository productRepository
    ) throws SQLException {
        this.agreementDao = agreementDao;
        this.agreementProductDao = agreementProductDao;
        this.discountByQuantityDao = discountByQuantityDao;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        loadAllFromDB();
    }

    @Override
    public void loadAllFromDB() throws SQLException {
        // Get all agreements from DB
        List<AgreementDTO> agreementDTOs = agreementDao.findAll();

        for (AgreementDTO agreementDTO : agreementDTOs) {
            // Find the supplier for this agreement
            Supplier supplier = supplierRepository.findSupplierById(agreementDTO.supplier_id());

            // Get all products in this agreement
            List<AgreementProductDTO> productDTOs = agreementProductDao.findAllByAgreement(agreementDTO.agreement_id());
            List<AgreementProduct> agreementProducts = new ArrayList<>();

            for (AgreementProductDTO productDTO : productDTOs) {
                // Get products from repository
                Product product = productRepository.findProduct(productDTO.product_id());
                // Create the agreementProduct entity
                AgreementProduct agreementProduct = AgreementProductMapper.toEntity(productDTO, product, supplier);

                // Try to get discount from DB for this item
                List<DiscountByQuantityDTO> discountDTOs = discountByQuantityDao.findBySupplyItem_id(productDTO.supplyItem_id());
                if (discountDTOs != null) {
                    for (DiscountByQuantityDTO d : discountDTOs) {
                        agreementProduct.setQAgreement(d.discount(), d.quantity(), d.dtype());
                    }
                }

                // Add this product to the agreement
                agreementProducts.add(agreementProduct);
            }

            // Create the full agreement entity and store it
            Agreement agreement = AgreementMapper.toEntity(agreementDTO, supplier);
            agreement.setProducts(agreementProducts);
            agreementStorage.put(agreement.getAgreement_id(), agreement);
            supplier.addAgreement(agreement);
        }
        loadAgreementsAndProductToSupplier();
    }

    //load all agreements and products of supplier
    private void loadAgreementsAndProductToSupplier() throws SQLException {
        for (Supplier supplier : supplierRepository.findAllSuppliers())
            for (Agreement agreement : findAllActiveAgreementsBySupplier(supplier)) {
                supplier.addAgreement(agreement); //load all agreements of supplier
                for (AgreementProduct agreementProduct : agreement.getProducts())
                    supplier.addSuppliedItem(agreementProduct.getProduct()); //load all products supplier supplies
            }
    }

    @Override
    public Agreement createAgreement(List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, Supplier
            supplier) throws SQLException {
        Agreement agreement = new Agreement(deliveryDays, deliveryMethod, supplier, findMaxId());
        // Save to DB
        AgreementDTO dto = AgreementMapper.toDto(agreement);
        agreementDao.save(dto);
        agreementStorage.put(agreement.getAgreement_id(), agreement);
        supplier.addAgreement(agreement);
        return agreement;
    }

    @Override
    public Agreement findAgreementById(int id) throws SQLException {
        Agreement agreement = agreementStorage.get(id);
        if (agreement == null) {
            throw new SQLException("Agreement with ID " + id + " not found.");
        }
        return agreement;
    }

    @Override
    public List<Agreement> findAllAgreements() throws SQLException {
        return new ArrayList<>(agreementStorage.values());
    }

    @Override
    public int findMaxId() throws SQLException {
        int max = 0;
        for (Agreement agreement : agreementStorage.values())
            if (agreement.getAgreement_id() > max)
                max = agreement.getAgreement_id();
        return max;
    }

    @Override
    public List<Agreement> findAllActiveAgreements() throws SQLException {
        List<Agreement> activeAgreements = new ArrayList<>();
        for (Agreement agreement : findAllAgreements())
            if (agreement.getStatus().equals(AgreementStatus.ACTIVE))
                activeAgreements.add(agreement);
        return activeAgreements;
    }

    @Override
    public List<Agreement> findAllActiveAgreementsBySupplier(Supplier supplier) throws SQLException {
        List<Agreement> activeSupplierAgreements = new ArrayList<>();
        for (Agreement agreement : findAllActiveAgreements())
            if (agreement.getSupplier().equals(supplier))
                activeSupplierAgreements.add(agreement);
        return activeSupplierAgreements;
    }

    @Override
    public List<AgreementProduct> productsInAgreement(int id) throws SQLException {
        return (List<AgreementProduct>) findAgreementById(id).getProducts();
    }

    @Override
    public int findSupplierIdByAgreementId(int agreement_id) throws SQLException {
        return findAgreementById(agreement_id).getSupplier().getSupplier_id();
    }

    @Override
    public void editDeliveryMethod(int agreement_id, DeliveryMethod deliveryMethod) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.setDeliveryMethod(deliveryMethod);
        agreementDao.update(AgreementMapper.toDto(agreement));
    }

    @Override
    public void editDeliveryDays(int agreement_id, List<DeliveryDays> deliveryDays) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.setDeliveryDays(deliveryDays);
        agreementDao.update(AgreementMapper.toDto(agreement));
    }

    @Override
    public void addProductToAgreement(int agreement_id, double price, Product product) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.addItem(price, product);
        agreement.getSupplier().addSuppliedItem(product);

        // Save to DB
        AgreementProduct newProduct = agreement.AgreementItemByProduct(product);
        AgreementProductDTO productDTO = AgreementProductMapper.toDto(newProduct, agreement_id);
        agreementProductDao.save(productDTO);
    }

    @Override
    public void addQuantityAgreement(int agreement_id, Product product, double discount,
                                     int quantity, DiscountMethod Dtype) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.setQuantityAgreementToItem(product, discount, quantity, Dtype);

        AgreementProduct ap = agreement.AgreementItemByProduct(product);
        DiscountByQuantityDTO dto = DiscountByQuantityMapper.toDto(ap.getQAgreement(), ap.getSupplyItem_id());
        discountByQuantityDao.save(dto);
    }

    @Override
    public void removeProductFromAgreement(int agreement_id, Product product) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        AgreementProduct ap = agreement.AgreementItemByProduct(product);
        agreement.removeItem(product);
        agreementProductDao.delete(ap.getSupplyItem_id());
        discountByQuantityDao.delete(ap.getSupplyItem_id());
    }

    @Override
    public void editQuantityInQA(int agreement_id, Product product, int quantity) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.AgreementItemByProduct(product).getQAgreement().setQuantity(quantity);

        AgreementProduct ap = agreement.AgreementItemByProduct(product);
        discountByQuantityDao.update(DiscountByQuantityMapper.toDto(ap.getQAgreement(), ap.getSupplyItem_id()));
    }

    @Override
    public void editDiscountInQa(int agreement_id, Product product, double discount) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.AgreementItemByProduct(product).getQAgreement().setDiscount(discount);

        AgreementProduct ap = agreement.AgreementItemByProduct(product);
        discountByQuantityDao.update(DiscountByQuantityMapper.toDto(ap.getQAgreement(), ap.getSupplyItem_id()));
    }

    @Override
    public void editDMethodInQA(int agreement_id, Product product, DiscountMethod discountMethod) throws
            SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.AgreementItemByProduct(product).getQAgreement().setDiscountMethod(discountMethod);

        AgreementProduct ap = agreement.AgreementItemByProduct(product);
        discountByQuantityDao.update(DiscountByQuantityMapper.toDto(ap.getQAgreement(), ap.getSupplyItem_id()));
    }

    @Override
    public void changeAgreementStatus(int agreement_id, AgreementStatus status) throws SQLException {
        Agreement agreement = findAgreementById(agreement_id);
        agreement.setStatus(status);
        agreementDao.update(AgreementMapper.toDto(agreement));
    }

    @Override
    public boolean checkAgreementProduct(int agreement_id, Product product) throws SQLException {
        return findAgreementById(agreement_id).checkProduct(product);
    }

    @Override
    public AgreementStatus currAgreementStatus(int agreement_id) throws SQLException {
        return findAgreementById(agreement_id).getStatus();
    }

    @Override
    public boolean checkQAgreement(Supplier supplier, Product product) throws SQLException {
        for (Agreement agreement : findAllActiveAgreementsBySupplier(supplier))
            for (AgreementProduct agreementProduct : agreement.getProducts())
                if (agreementProduct.getProduct().equals(product))
                    if (agreementProduct.getQAgreement() != null)
                        return true;
        return false;
    }
}
