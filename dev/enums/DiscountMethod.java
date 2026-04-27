package enums;

public enum DiscountMethod {
    PERCENTAGE {
        @Override
        public String toString() {
            return "Percentage";
        }
    },
    AMOUNT {
        @Override
        public String toString() {
            return "Amount";
        }
    };
}
