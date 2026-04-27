package DTO;

public record OrderProductDTO(
        int quantity, //quantity in order
        double price, // price
        boolean useDiscount, // if to use quantity agreement or not
        double discount, // discount
        double finalPrice, //final price after discount
        int order_id, // id of order the product is in
        String supplier_ProductId // order product is based on agreement product with supplier-product id
) {
}
