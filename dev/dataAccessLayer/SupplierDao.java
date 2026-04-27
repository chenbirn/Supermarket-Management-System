package dataAccessLayer;

import DTO.SupplierDTO;
import enums.PaymentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SupplierDao {

    SupplierDTO findById(int id) throws SQLException;

    List<SupplierDTO> findAll() throws SQLException;

    void save(SupplierDTO dto) throws SQLException;

    void update(SupplierDTO dto) throws SQLException;

    void delete(int supplierId) throws SQLException;

    SupplierDTO mapResultSetToDTO(ResultSet rs) throws SQLException;
}
