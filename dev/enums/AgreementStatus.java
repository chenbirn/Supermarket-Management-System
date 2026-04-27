package enums;

public enum AgreementStatus {
    ACTIVE {
        @Override
        public String toString() {
            return "active";
        }
    },
    INACTIVE {
        @Override
        public String toString() {
            return "inactive";
        }
    };
}
