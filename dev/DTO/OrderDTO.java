package DTO;

import domainLayer.OrderProduct;
import enums.*;

import java.time.LocalDate;
import java.util.Collection;

public record OrderDTO(
        int order_id, //order's id
        LocalDate orderDate, //order's creation date
        String contactNum, //contact phone number
        double totalPrice, //total price
        OrderStatus status, //status (in process/ready/done)
        int supplier_id, //supplier_id for items in order
        LocalDate deliveryDate,
        Integer frequency,
        OrderType order_type

) {
}



