package dataAccessLayer;

import DTO.DiscountByQuantityDTO;
import java.sql.SQLException;
import java.util.List;

public interface DiscountByQuantityDao {

    // Save a new discount rule in the database
    void save(DiscountByQuantityDTO dto) throws SQLException;

    // Find a discount rule by the supply item ID
    List<DiscountByQuantityDTO> findBySupplyItem_id(String supplyItem_id) throws SQLException;

    // Update an existing discount rule
    void update(DiscountByQuantityDTO dto) throws SQLException;

    // Delete a discount rule by the supply item ID
    void delete(String supplyItem_id) throws SQLException;
}
