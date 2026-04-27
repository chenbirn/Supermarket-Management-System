package domainLayer;

import enums.AgreementStatus;
import enums.DeliveryDays;
import enums.DeliveryMethod;
import enums.DiscountMethod;

import java.sql.SQLException;
import java.util.List;

public interface IAgreementRepository {

    public void loadAllFromDB() throws SQLException;

    Agreement createAgreement(List<DeliveryDays> deliveryDays, DeliveryMethod deliveryMethod, Supplier supplier) throws SQLException;

    Agreement findAgreementById(int id) throws SQLException;

    List<Agreement> findAllAgreements() throws SQLException;

    int findMaxId() throws SQLException;

    public List<Agreement> findAllActiveAgreements() throws SQLException;

    List<Agreement> findAllActiveAgreementsBySupplier(Supplier supplier) throws SQLException;

    List<AgreementProduct> productsInAgreement(int id) throws SQLException;

    int findSupplierIdByAgreementId(int agreement_id) throws SQLException;

    public void editDeliveryMethod(int agreement_id, DeliveryMethod deliveryMethod) throws SQLException;

    public void editDeliveryDays(int agreement_id, List<DeliveryDays> deliveryDays) throws SQLException;

    public void addProductToAgreement(int agreement_id, double price, Product product) throws SQLException;

    public void addQuantityAgreement(int agreement_id, Product product, double discount, int quantity, DiscountMethod Dtype) throws SQLException;

    public void removeProductFromAgreement(int agreement_id, Product product) throws SQLException;

    public void editQuantityInQA(int agreement_id, Product product, int quantity) throws SQLException;

    public void editDiscountInQa(int agreement_id, Product product, double discount) throws SQLException;

    public void editDMethodInQA(int agreement_id, Product product, DiscountMethod discountMethod) throws SQLException;

    public void changeAgreementStatus(int agreement_id, AgreementStatus status) throws SQLException;

    public boolean checkAgreementProduct(int agreement_id, Product product) throws SQLException;

    public AgreementStatus currAgreementStatus(int agreement_id) throws SQLException;

    public boolean checkQAgreement(Supplier supplier, Product product) throws SQLException;
}
