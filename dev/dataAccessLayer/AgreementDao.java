package dataAccessLayer;

import DTO.AgreementDTO;
import enums.DeliveryDays;

import java.sql.SQLException;
import java.util.List;

public interface AgreementDao {

    // Find and return an agreement by its ID
    AgreementDTO findAgreementById(int agreement_id) throws SQLException;

    // Return all agreements from the database
    List<AgreementDTO> findAll() throws SQLException;

    // Update an existing agreement in the database
    void update(AgreementDTO dto) throws SQLException;

    // Delete an agreement from the database by ID
    void delete(int id) throws SQLException;

    // Save a new agreement to the database
    void save(AgreementDTO dto) throws SQLException;

    // Get a list of product IDs that are part of a specific agreement
    List<Integer> productsInAgreement(int agreement_id) throws SQLException;

    List<DeliveryDays> getDeliveryDays(int agreementId) throws SQLException;
}
