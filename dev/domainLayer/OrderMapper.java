package domainLayer;

import enums.*;
import DTO.OrderDTO;

import java.sql.SQLException;
import java.util.List;

public class OrderMapper {

    public static OrderDTO toDto(Order order) {
        Integer frequency = null;
        OrderType orderType = OrderType.MANUAL;

        if (order instanceof PeriodicOrder) {
            frequency = ((PeriodicOrder) order).getFrequency();
            orderType = OrderType.PERIODIC;
        } else if (order instanceof ShortageOrder) {
            orderType = OrderType.SHORTAGE;
        }

        return new OrderDTO(
                order.getOrder_id(),
                order.getOrderDate(),
                order.getContactNum(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getSupplier().getSupplier_id(),
                order.getDeliveryDate(),
                frequency,
                orderType
        );
    }

    public static Order toEntity(OrderDTO dto, Supplier supplier, List<OrderProduct> orderProducts) throws SQLException {
        switch (dto.order_type()) {
            case SHORTAGE -> {
                return new ShortageOrder(dto.order_id(), dto.orderDate(), dto.deliveryDate(), dto.contactNum(), dto.status(), orderProducts, supplier);
            }

            case PERIODIC -> {
                return new PeriodicOrder(dto.order_id(), dto.orderDate(), dto.deliveryDate(), dto.contactNum(), dto.status(), orderProducts, supplier, dto.frequency());
            }

            default -> {
                return new Order(dto.order_id(), dto.orderDate(), dto.deliveryDate(), dto.contactNum(), dto.status(), orderProducts, supplier);
            }
        }
    }
}
