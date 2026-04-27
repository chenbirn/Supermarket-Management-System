package dataAccessLayer;

import DTO.AgreementProductDTO;
import java.sql.SQLException;
import java.util.List;

public interface AgreementProductDao {

    // Save a new agreement-product connection in the database
    void save(AgreementProductDTO dto) throws SQLException;

    // Find a specific agreement-product by its supplyItem ID
    AgreementProductDTO findBySupplyItem_id(String supplyItem_id) throws SQLException;

    // Get all agreement-products for a specific agreement ID
    List<AgreementProductDTO> findAllByAgreement(int agreement_id) throws SQLException;

    // Update existing agreement-product information in the database
    void update(AgreementProductDTO dto) throws SQLException;

    // Delete an agreement-product by its supplyItem ID
    void delete(String supplyItem_id) throws SQLException;

}
