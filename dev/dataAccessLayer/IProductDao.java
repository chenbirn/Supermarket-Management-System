package dataAccessLayer;

import DTO.ProductDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IProductDao {
    ProductDto findById(int id);

    List<ProductDto> findAll();

    void save(ProductDto dto);

    void update(ProductDto dto);

    void delete(int id);

    ProductDto toDto(ResultSet rs) throws SQLException;

    void fillStatement(PreparedStatement stmt, ProductDto dto) throws SQLException;
}
