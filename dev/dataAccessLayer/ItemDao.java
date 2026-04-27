package dataAccessLayer;

import DTO.ItemDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ItemDao {
    // find item by id
    ItemDTO findById(int id);

    // find all items
    List<ItemDTO> findAll();

    // save an item
    void save(ItemDTO dto);

    // update an item
    void update(ItemDTO dto);

    // delete an item
    void delete(int id);

    ItemDTO toDto(ResultSet rs) throws SQLException;

    void fillStatement(PreparedStatement stmt, ItemDTO dto) throws SQLException;
}
