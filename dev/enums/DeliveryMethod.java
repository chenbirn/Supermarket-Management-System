package enums;

public enum DeliveryMethod {
    SELF_PICKUP {
        @Override
        public String toString() {
            return "Self Pickup";
        }
    },
    DELIVERY {
        @Override
        public String toString() {
            return "Delivery";
        }
    };
}
