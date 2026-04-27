package domainLayer;

import DTO.OrderDTO;
import DTO.OrderProductDTO;
import enums.OrderType;

import java.sql.SQLException;
import java.util.List;

public class OrderProductMapper {
    public static OrderProductDTO toDto(OrderProduct orderProduct) {
        return new OrderProductDTO(orderProduct.getQuantity(), orderProduct.getPrice(), orderProduct.isUseDiscount(), orderProduct.getDiscount(),
                orderProduct.getFinalPrice(), orderProduct.getOrder_id(), orderProduct.getAgreementItem().getSupplyItem_id());
    }

    public static OrderProduct toEntity(OrderProductDTO dto, AgreementProduct agreementProduct) throws SQLException {
        return new OrderProduct(dto.quantity(), dto.useDiscount(), dto.order_id(), agreementProduct);
    }
}
