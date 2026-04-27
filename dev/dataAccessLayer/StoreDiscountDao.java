package dataAccessLayer;

import DTO.StoreDiscountDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface StoreDiscountDao {
    StoreDiscountDTO findById(int id);

    List<StoreDiscountDTO> findAll();

    void save(StoreDiscountDTO dto);

    void update(StoreDiscountDTO dto);

    void delete(int id);

    StoreDiscountDTO toDto(ResultSet rs) throws SQLException;

    void fillStatement(PreparedStatement stmt, StoreDiscountDTO dto) throws SQLException;

    List<StoreDiscountDTO> findByProductId(int productId);
}
