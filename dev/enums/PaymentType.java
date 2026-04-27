package enums;

public enum PaymentType {
    CREDIT_CARD {
        @Override
        public String toString() {
            return "Credit Card";
        }
    },
    CASH {
        @Override
        public String toString() {
            return "Cash";
        }
    },
    CHECK {
        @Override
        public String toString() {
            return "Check";
        }
    },
    BANK_TRANSFER {
        @Override
        public String toString() {
            return "Bank Transfer";
        }
    };
}
